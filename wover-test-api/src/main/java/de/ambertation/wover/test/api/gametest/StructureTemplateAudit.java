package de.ambertation.wover.test.api.gametest;

import de.ambertation.wover.structure.api.StructureNBT;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Checks a mod's shipped structure templates ({@code data/<namespace>/structure/*.nbt}) for the ways a template
 * silently places the wrong thing. Every check returns one line per problem, so a gametest can collect them and
 * fail with {@link GameTestAssertions#failIfAny}.
 * <ul>
 *     <li>{@link #loadsLikeVanilla}: {@link StructureNBT#readTemplate} must load a template block-for-block and
 *     entity-for-entity as vanilla's template manager does. Vanilla runs the {@code STRUCTURE} DataFixer on every
 *     outdated file; a loader that skips it loads renamed blocks - on 26.3, after the palette key rename, every
 *     block of a pre-26.3 template - as air.</li>
 *     <li>{@link #blocksLoadingAsAir}: a palette entry that is not air but decodes to air. The fixer upgrades
 *     block names by data version, so a hand-edited template whose {@code DataVersion} was bumped without
 *     renaming its blocks keeps the old names, and they are placed as air.</li>
 *     <li>{@link #mismatchedBlockEntityIds}: block entity data whose {@code id} is not the block entity its block
 *     creates. Placement ignores that id, but the DataFixer picks its fixes by it - data under an id it does not
 *     know (a removed modded type, say) is never upgraded.</li>
 *     <li>{@link #outdatedDataVersions}: a template not saved at the current data version pays for the fixer on
 *     every load, and is a sign it was never upgraded (WunderVault {@code structuregen/upgrade_structures.sh}).</li>
 * </ul>
 */
public final class StructureTemplateAudit {
    private StructureTemplateAudit() {}

    /** Every template id under {@code data/<namespace>/structure/}, sorted. */
    public static List<Identifier> shippedTemplates(ServerLevel level, String namespace) {
        final List<Identifier> ids = new ArrayList<>();
        level.getServer()
             .getResourceManager()
             .listResources("structure", loc -> loc.getPath().endsWith(".nbt"))
             .keySet()
             .stream()
             .filter(loc -> loc.getNamespace().equals(namespace))
             .sorted()
             .forEach(loc -> ids.add(loc.withPath(p -> p.substring("structure/".length(), p.length() - 4))));
        return ids;
    }

    /** The data version this game saves with. */
    public static int currentDataVersion() {
        // Read back through NbtUtils rather than SharedConstants: the version accessor differs between
        // Minecraft versions, this does not.
        return NbtUtils.getDataVersion(NbtUtils.addCurrentDataVersion(new CompoundTag()), 0);
    }

    /** The template file exactly as shipped, before any DataFixer pass. */
    public static CompoundTag readRaw(ServerLevel level, Identifier template) throws IOException {
        final Identifier file = template.withPath(p -> "structure/" + p + ".nbt");
        final Resource resource = level.getServer()
                                       .getResourceManager()
                                       .getResource(file)
                                       .orElseThrow(() -> new IOException("missing resource " + file));
        try (InputStream in = resource.open()) {
            return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
        }
    }

    /** One line per template not saved at {@link #currentDataVersion()}. */
    public static List<String> outdatedDataVersions(ServerLevel level, List<Identifier> templates) {
        final List<String> failures = new ArrayList<>();
        final int current = currentDataVersion();
        for (Identifier id : templates) {
            try {
                final int version = NbtUtils.getDataVersion(readRaw(level, id), 500);
                if (version != current) failures.add(id + " is at data version " + version + ", not " + current);
            } catch (IOException e) {
                failures.add(id + ": cannot read (" + e.getMessage() + ")");
            }
        }
        return failures;
    }

    /**
     * One line per palette entry that is not air but decodes to air - after the DataFixer pass the loader
     * applies, so only entries no fixer maps are reported. Covers every namespace: the mods are loaded here.
     */
    public static List<String> blocksLoadingAsAir(ServerLevel level, Identifier id) {
        final List<String> failures = new ArrayList<>();
        final CompoundTag tag;
        try {
            tag = fixed(readRaw(level, id));
        } catch (IOException e) {
            return List.of(id + ": cannot read (" + e.getMessage() + ")");
        }
        final Set<String> seen = new TreeSet<>();
        for (CompoundTag entry : paletteEntries(tag)) {
            final String name = entry.getStringOr("id", entry.getStringOr("Name", ""));
            if (name.isEmpty() || name.endsWith("air") || !seen.add(name)) continue;
            if (NbtUtils.readBlockState(BuiltInRegistries.BLOCK, entry).isAir()) {
                failures.add(id + ": palette entry " + name + " loads as air");
            }
        }
        return failures;
    }

    /**
     * One line per block entity id that is not the block entity its block creates (each distinct
     * {@code id -> expected} pair once per template).
     */
    public static List<String> mismatchedBlockEntityIds(ServerLevel level, Identifier id) {
        final CompoundTag tag;
        try {
            tag = fixed(readRaw(level, id));
        } catch (IOException e) {
            return List.of(id + ": cannot read (" + e.getMessage() + ")");
        }
        final List<BlockState> palette = new ArrayList<>();
        final List<CompoundTag> entries = new ArrayList<>();
        tag.getListOrEmpty("palette").compoundStream().forEach(entries::add);
        if (entries.isEmpty()) tag.getListOrEmpty("palettes").stream().findFirst()
                                  .ifPresent(l -> ((ListTag) l).compoundStream().forEach(entries::add));
        for (CompoundTag entry : entries) palette.add(NbtUtils.readBlockState(BuiltInRegistries.BLOCK, entry));

        final Set<String> problems = new TreeSet<>();
        tag.getListOrEmpty("blocks").compoundStream().forEach(block -> {
            final CompoundTag nbt = block.getCompound("nbt").orElse(null);
            if (nbt == null || !nbt.contains("id")) return;
            final String actual = nbt.getStringOr("id", "");
            final int index = block.getIntOr("state", -1);
            if (index < 0 || index >= palette.size()) return;
            final BlockState state = palette.get(index);
            if (state.isAir()) return; // reported by blocksLoadingAsAir
            final String expected = expectedBlockEntity(state);
            if (expected == null) {
                problems.add(actual + " on " + BuiltInRegistries.BLOCK.getKey(state.getBlock()) + ", which has no block entity");
            } else if (!expected.equals(actual)) {
                problems.add(actual + " should be " + expected + " (block " + BuiltInRegistries.BLOCK.getKey(state.getBlock()) + ")");
            }
        });
        final List<String> failures = new ArrayList<>();
        problems.forEach(p -> failures.add(id + ": block entity " + p));
        return failures;
    }

    /**
     * {@code null} if {@link StructureNBT#readTemplate} places the same blocks and entities as vanilla's template
     * manager, else a description of the first difference.
     */
    public static String loadsLikeVanilla(ServerLevel level, Identifier id) {
        final StructureTemplate reference = level.getStructureManager().get(id).orElse(null);
        if (reference == null) return id + ": vanilla's template manager cannot load it";
        final StructureTemplate loaded;
        try {
            loaded = StructureNBT.readTemplate(id);
        } catch (RuntimeException e) {
            return id + ": StructureNBT threw " + e;
        }
        if (loaded == null) return id + ": StructureNBT could not read it";
        final String diff = Snapshot.of(reference).diff(Snapshot.of(loaded));
        return diff == null ? null : id + ": " + diff;
    }

    private static CompoundTag fixed(CompoundTag raw) {
        final int version = NbtUtils.getDataVersion(raw, 500);
        return DataFixTypes.STRUCTURE.updateToCurrentVersion(DataFixers.getDataFixer(), raw, version);
    }

    private static List<CompoundTag> paletteEntries(CompoundTag tag) {
        final List<CompoundTag> entries = new ArrayList<>();
        tag.getListOrEmpty("palette").compoundStream().forEach(entries::add);
        tag.getListOrEmpty("palettes").stream().forEach(l -> ((ListTag) l).compoundStream().forEach(entries::add));
        return entries;
    }

    private static String expectedBlockEntity(BlockState state) {
        if (!(state.getBlock() instanceof EntityBlock entityBlock)) return null;
        final BlockEntity entity = entityBlock.newBlockEntity(BlockPos.ZERO, state);
        if (entity == null) return null;
        return Objects.toString(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(entity.getType()));
    }

    /** What a template places: per palette the block at each position, plus the entity ids. */
    private record Snapshot(List<Map<BlockPos, BlockState>> palettes, List<String> entities) {
        static Snapshot of(StructureTemplate template) {
            final List<Map<BlockPos, BlockState>> palettes = new ArrayList<>();
            for (StructureTemplate.Palette palette : field(template, "palettes", StructureTemplate.Palette.class)) {
                final Map<BlockPos, BlockState> blocks = new HashMap<>();
                palette.blocks().forEach(info -> blocks.put(info.pos(), info.state()));
                palettes.add(blocks);
            }
            final List<String> entities = new ArrayList<>();
            for (StructureTemplate.StructureEntityInfo info : field(
                    template,
                    "entityInfoList",
                    StructureTemplate.StructureEntityInfo.class
            )) {
                entities.add(info.nbt.getStringOr("id", "?"));
            }
            return new Snapshot(palettes, entities);
        }

        /** {@code null} if equal, else a short description of the first difference. */
        String diff(Snapshot actual) {
            if (palettes.size() != actual.palettes.size()) {
                return palettes.size() + " palettes expected, got " + actual.palettes.size();
            }
            for (int i = 0; i < palettes.size(); i++) {
                final Map<BlockPos, BlockState> want = palettes.get(i);
                final Map<BlockPos, BlockState> got = actual.palettes.get(i);
                int differing = 0;
                String first = null;
                for (Map.Entry<BlockPos, BlockState> e : want.entrySet()) {
                    final BlockState other = got.get(e.getKey());
                    if (!Objects.equals(e.getValue(), other)) {
                        if (first == null) {
                            first = e.getKey().toShortString() + " should be " + e.getValue() + ", is " + other;
                        }
                        differing++;
                    }
                }
                if (got.size() != want.size() || differing > 0) {
                    return differing + " of " + want.size() + " blocks differ (" + got.size()
                            + " loaded) in palette " + i + (first == null ? "" : ", e.g. " + first);
                }
            }
            if (!entities.equals(actual.entities)) {
                return "entities " + entities + " expected, got " + actual.entities;
            }
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> List<T> field(StructureTemplate template, String name, Class<T> type) {
        try {
            final Field f = StructureTemplate.class.getDeclaredField(name);
            f.setAccessible(true);
            return (List<T>) f.get(template);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("StructureTemplate." + name + " not accessible", e);
        }
    }
}
