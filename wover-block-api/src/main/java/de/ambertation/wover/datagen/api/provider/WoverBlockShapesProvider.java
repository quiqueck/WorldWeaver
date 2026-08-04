package de.ambertation.wover.datagen.api.provider;

import de.ambertation.wover.core.api.ModCore;
import de.ambertation.wover.datagen.api.WoverDataProvider;

import com.google.common.hash.Hashing;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Writes a deterministic {@code block_shapes.txt} snapshot of the outline ({@code shape=}) and collision
 * ({@code collision=}) {@link VoxelShape}s of every block in the consuming mod's namespace, evaluated per
 * blockstate against {@link EmptyBlockGetter}.
 *
 * <p>This is the oracle that makes reparenting a block from a custom class to a vanilla class verifiable: a
 * {@code getShape}/{@code getCollisionShape} override is the most likely silent regression there and has no other
 * datagen surface. When every blockstate shares the same pair of shapes a single line is emitted; otherwise one
 * line per state (in {@link net.minecraft.world.level.block.state.StateDefinition#getPossibleStates()} order) with
 * the state's property string. A state whose shape cannot be computed without a real level yields
 * {@code shape=unavailable}.
 *
 * <p>Like {@code block_properties.txt} this file is committed under {@code src/main/generated} but excluded from
 * the packed jar via {@code processResources { exclude 'block_shapes.txt' }} in the consuming mod's build.gradle.
 */
public class WoverBlockShapesProvider implements WoverDataProvider<DataProvider> {
    protected final ModCore modCore;

    public WoverBlockShapesProvider(ModCore modCore) {
        this.modCore = modCore;
    }

    @Override
    public DataProvider getProvider(
            FabricDataOutput output,
            CompletableFuture<HolderLookup.Provider> registriesFuture
    ) {
        return new Provider(output);
    }

    /**
     * Formats a coordinate deterministically and locale-independently: up to 4 decimals with trailing zeros (and
     * a trailing dot) stripped, and negative zero normalized to {@code 0}. Yields e.g. {@code 0}, {@code 0.5},
     * {@code 1}.
     */
    private static String coord(double value) {
        if (Math.abs(value) < 1.0e-9) value = 0.0;
        String s = String.format(Locale.ROOT, "%.4f", value);
        if (s.indexOf('.') >= 0) {
            int end = s.length();
            while (end > 0 && s.charAt(end - 1) == '0') end--;
            if (end > 0 && s.charAt(end - 1) == '.') end--;
            s = s.substring(0, end);
        }
        return s;
    }

    private static String aabb(AABB box) {
        return "[" + coord(box.minX) + "," + coord(box.minY) + "," + coord(box.minZ)
                + "|" + coord(box.maxX) + "," + coord(box.maxY) + "," + coord(box.maxZ) + "]";
    }

    /**
     * The sorted-AABB string form of a shape, or {@code unavailable} if evaluating it throws (a shape that needs
     * real world context). An empty shape is emitted as {@code empty}.
     */
    private static String shapeString(BlockState state, Function<BlockState, VoxelShape> extractor) {
        final VoxelShape shape;
        try {
            shape = extractor.apply(state);
        } catch (Throwable t) {
            return "unavailable";
        }
        if (shape == null || shape.isEmpty()) return "empty";
        final List<String> boxes = new ArrayList<>();
        for (AABB box : shape.toAabbs()) {
            boxes.add(aabb(box));
        }
        // Sort the boxes so the value is independent of toAabbs() iteration order.
        Collections.sort(boxes);
        return String.join("", boxes);
    }

    private static VoxelShape outline(BlockState state) {
        return state.getShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    private static VoxelShape collision(BlockState state) {
        return state.getCollisionShape(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
    }

    private static String shapesFor(BlockState state) {
        return "shape=" + shapeString(state, WoverBlockShapesProvider::outline)
                + " collision=" + shapeString(state, WoverBlockShapesProvider::collision);
    }

    private static <T extends Comparable<T>> String propertyValue(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static String propertyString(BlockState state) {
        final TreeMap<String, String> values = new TreeMap<>();
        for (Property<?> property : state.getProperties()) {
            values.put(property.getName(), propertyValue(state, property));
        }
        final StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            if (!first) sb.append(',');
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            first = false;
        }
        return sb.append(']').toString();
    }

    private static void appendLines(List<String> out, ResourceLocation id, Block block) {
        final List<BlockState> states = block.getStateDefinition().getPossibleStates();
        final String first = shapesFor(states.get(0));
        boolean uniform = true;
        for (BlockState state : states) {
            if (!shapesFor(state).equals(first)) {
                uniform = false;
                break;
            }
        }
        if (uniform) {
            out.add(id + "  " + first);
        } else {
            // One line per state, in getPossibleStates() order (deterministic).
            for (BlockState state : states) {
                out.add(id + propertyString(state) + "  " + shapesFor(state));
            }
        }
    }

    private class Provider implements DataProvider {
        private final FabricDataOutput output;

        private Provider(FabricDataOutput output) {
            this.output = output;
        }

        @Override
        public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
            // Collect the mod-namespace blocks, sorted by id; per-block lines preserve getPossibleStates() order.
            final TreeMap<String, Block> byId = new TreeMap<>();
            for (Block block : BuiltInRegistries.BLOCK) {
                final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
                if (!id.getNamespace().equals(modCore.namespace)) continue;
                byId.put(id.toString(), block);
            }

            final List<String> lines = new ArrayList<>();
            for (Map.Entry<String, Block> entry : byId.entrySet()) {
                appendLines(lines, ResourceLocation.parse(entry.getKey()), entry.getValue());
            }

            final byte[] bytes = (String.join("\n", lines) + "\n").getBytes(StandardCharsets.UTF_8);
            final Path path = output.getOutputFolder().resolve("block_shapes.txt");
            try {
                writer.writeIfNeeded(path, bytes, Hashing.sha1().hashBytes(bytes));
            } catch (IOException e) {
                return CompletableFuture.failedFuture(e);
            }
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public @NotNull String getName() {
            return "Block Shapes Audit (" + modCore.namespace + ")";
        }
    }
}
