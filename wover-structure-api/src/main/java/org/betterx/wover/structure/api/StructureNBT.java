package org.betterx.wover.structure.api;

import org.betterx.wover.block.api.BlockHelper;
import org.betterx.wover.entrypoint.LibWoverStructure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import com.google.common.collect.Maps;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 * Loads and caches a {@code .nbt} {@link StructureTemplate} from a namespace's
 * {@code data/<namespace>/structure/} folder (either from the classpath/jar or, via
 * {@link #createResourcesFrom(ResourceLocation, int)}, by walking a whole folder) and provides helper
 * methods to place it into a {@link ServerLevelAccessor} directly, without going through the vanilla
 * {@link net.minecraft.world.level.levelgen.structure.Structure}/{@link net.minecraft.world.level.levelgen.structure.pieces.StructurePiece}
 * pipeline.
 * <p>
 * Instances are cached by {@link ResourceLocation}; use {@link #create(ResourceLocation)} rather than
 * calling a constructor directly so repeated lookups reuse the same loaded template.
 */
public class StructureNBT {
    /**
     * The location of the {@code .nbt} file this instance was loaded from, relative to
     * {@code data/<namespace>/structure/}.
     */
    public final ResourceLocation location;
    /**
     * The loaded {@link StructureTemplate}, or {@code null} if loading failed.
     */
    protected StructureTemplate structure;


    protected StructureNBT(ResourceLocation location) {
        this.location = location;
        this.structure = readStructureFromJar(location);
    }

    protected StructureNBT(ResourceLocation location, StructureTemplate structure) {
        this.location = location;
        this.structure = structure;
    }

    /**
     * Picks a uniformly random {@link Rotation}.
     *
     * @param random The random source to use
     * @return A random {@link Rotation}
     */
    public static Rotation getRandomRotation(RandomSource random) {
        return Rotation.getRandom(random);
    }

    /**
     * Picks a uniformly random {@link Mirror} (one of {@link Mirror#NONE}, {@link Mirror#LEFT_RIGHT} or
     * {@link Mirror#FRONT_BACK}).
     *
     * @param random The random source to use
     * @return A random {@link Mirror}
     */
    public static Mirror getRandomMirror(RandomSource random) {
        return Mirror.values()[random.nextInt(3)];
    }

    private static final Map<ResourceLocation, StructureNBT> STRUCTURE_CACHE = Maps.newHashMap();

    /**
     * Gets (or lazily loads and caches) the {@link StructureNBT} for the given {@link ResourceLocation}.
     * The location is resolved to {@code data/<namespace>/structure/<path>.nbt} on the classpath.
     *
     * @param location The location of the {@code .nbt} file, without the {@code .nbt} extension
     * @return The (possibly cached) {@link StructureNBT}
     */
    public static StructureNBT create(ResourceLocation location) {
        return STRUCTURE_CACHE.computeIfAbsent(location, StructureNBT::new);
    }

    /**
     * Places the structure so that it is centered on {@code pos} (taking {@code rotation} and
     * {@code mirror} into account), using {@link org.betterx.wover.block.api.BlockHelper#SET_SILENT}
     * block update flags.
     *
     * @param world    The world to place the structure in
     * @param pos      The position to center the structure on
     * @param rotation The rotation to apply
     * @param mirror   The mirror to apply
     * @return {@code true} if the structure was placed, {@code false} if the template failed to load
     */
    public boolean generateCentered(ServerLevelAccessor world, BlockPos pos, Rotation rotation, Mirror mirror) {
        BlockPos newPos = getCenteredPos(pos, rotation, mirror);
        if (newPos == null) return false;
        StructurePlaceSettings data = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror);
        structure.placeInWorld(
                world, newPos, newPos,
                data, world.getRandom(),
                BlockHelper.SET_SILENT
        );
        return true;
    }

    /**
     * Places the structure with its origin (the {@code .nbt} file's {@code 0,0,0}) at {@code pos},
     * using {@link org.betterx.wover.block.api.BlockHelper#SET_SILENT} block update flags.
     *
     * @param world    The world to place the structure in
     * @param pos      The origin position to place the structure at
     * @param rotation The rotation to apply
     * @param mirror   The mirror to apply
     * @return {@code true} if the structure was placed, {@code false} if the template failed to load
     */
    public boolean generateAt(ServerLevelAccessor world, BlockPos pos, Rotation rotation, Mirror mirror) {
        if (structure == null) {
            LibWoverStructure.C.log.error("No structure: " + location.toString());
            return false;
        }
        StructurePlaceSettings data = new StructurePlaceSettings().setRotation(rotation).setMirror(mirror);
        structure.placeInWorld(
                world, pos, pos,
                data, world.getRandom(),
                BlockHelper.SET_SILENT
        );
        return true;
    }

    @Nullable
    private BlockPos getCenteredPos(BlockPos pos, Rotation rotation, Mirror mirror) {
        if (structure == null) {
            LibWoverStructure.C.log.error("No structure: " + location.toString());
            return null;
        }

        BlockPos.MutableBlockPos blockpos2 = new BlockPos.MutableBlockPos().set(structure.getSize());
        if (mirror == Mirror.FRONT_BACK)
            blockpos2.setX(-blockpos2.getX());
        if (mirror == Mirror.LEFT_RIGHT)
            blockpos2.setZ(-blockpos2.getZ());
        blockpos2.set(blockpos2.rotate(rotation));
        return pos.offset(-blockpos2.getX() >> 1, 0, -blockpos2.getZ() >> 1);
    }

    private static final Map<ResourceLocation, StructureTemplate> READER_CACHE = Maps.newHashMap();

    private static StructureTemplate readStructureFromJar(ResourceLocation resource) {
        return READER_CACHE.computeIfAbsent(resource, r -> _readStructureFromJar(r));
    }

    private static String getStructurePath(ResourceLocation resource) {
        return "data/" + resource.getNamespace() + "/structure/" + resource.getPath();
    }

    private static StructureTemplate _readStructureFromJar(ResourceLocation resource) {
        try {
            InputStream inputstream = MinecraftServer.class.getResourceAsStream("/" + getStructurePath(resource) + ".nbt");
            return readStructureFromStream(inputstream);
        } catch (IOException e) {
            LibWoverStructure.C.log.error("Unable to load Structure " + resource, e);
        }

        return null;
    }

    private static StructureTemplate readStructureFromStream(InputStream stream) throws IOException {
        CompoundTag nbttagcompound = NbtIo.readCompressed(stream, NbtAccounter.unlimitedHeap());

        StructureTemplate template = new StructureTemplate();

        template.load(BuiltInRegistries.BLOCK, nbttagcompound);

        return template;
    }

    /**
     * Returns the size of the structure, swapping the X/Z extents for a {@link Rotation} of 90 or 270
     * degrees.
     *
     * @param rotation The rotation to account for
     * @return The (rotated) size of the structure
     */
    public BlockPos getSize(Rotation rotation) {
        if (rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180)
            return new BlockPos(structure.getSize());
        else {
            Vec3i size = structure.getSize();
            int x = size.getX();
            int z = size.getZ();
            return new BlockPos(z, size.getY(), x);
        }
    }

    /**
     * Returns the path component of {@link #location}, i.e. the structure's file name (without the
     * {@code .nbt} extension and namespace).
     *
     * @return The structure's name
     */
    public String getName() {
        return location.getPath();
    }

    /**
     * Gets the {@link BoundingBox} the structure would occupy if placed with its origin at {@code pos},
     * as done by {@link #generateAt(ServerLevelAccessor, BlockPos, Rotation, Mirror)}.
     *
     * @param pos      The origin position
     * @param rotation The rotation to apply
     * @param mirror   The mirror to apply
     * @return The bounding box
     */
    public BoundingBox getBoundingBox(BlockPos pos, Rotation rotation, Mirror mirror) {
        return structure.getBoundingBox(new StructurePlaceSettings().setRotation(rotation).setMirror(mirror), pos);
    }

    /**
     * Gets the {@link BoundingBox} the structure would occupy if placed centered on {@code pos}, as done
     * by {@link #generateCentered(ServerLevelAccessor, BlockPos, Rotation, Mirror)}.
     *
     * @param pos      The position to center the structure on
     * @param rotation The rotation to apply
     * @param mirror   The mirror to apply
     * @return The bounding box
     */
    public BoundingBox getCenteredBoundingBox(BlockPos pos, Rotation rotation, Mirror mirror) {
        return structure.getBoundingBox(
                new StructurePlaceSettings().setRotation(rotation).setMirror(mirror),
                getCenteredPos(pos, rotation, mirror)
        );
    }

    /**
     * Returns a list of all structures found at the given resource location.
     *
     * @param resource       The resource location to search.
     * @param recursionDepth The maximum recursion depth or 0 to indicate no limitation
     * @return A list of all structures found at the given resource location.
     */
    public static List<StructureNBT> createResourcesFrom(ResourceLocation resource, int recursionDepth) {
        String ns = resource.getNamespace();
        String nm = resource.getPath();

        final String resourceFolder = getStructurePath(resource);
        final URL url = MinecraftServer.class.getClassLoader().getResource(resourceFolder);
        if (url != null) {
            final URI uri;
            try {
                uri = url.toURI();
            } catch (URISyntaxException e) {
                LibWoverStructure.C.log.error("Unable to load Resources: ", e);
                return null;
            }
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = null;
                try {
                    fileSystem = FileSystems.getFileSystem(uri);
                } catch (FileSystemNotFoundException notLoaded) {
                    try {
                        fileSystem = FileSystems.newFileSystem(uri, new HashMap<>());
                    } catch (IOException e) {
                        LibWoverStructure.C.log.error("Unable to load Filesystem: ", e);
                        return null;
                    }
                }

                myPath = fileSystem.getPath(resourceFolder);
            } else {
                myPath = Paths.get(uri);
            }
            if (Files.isDirectory(myPath)) {
                try {
                    // /bclib place nbt minecraft:village/plains 0 southOf 0 -60 -0 controller
                    return Files.walk(myPath, recursionDepth <= 0 ? Integer.MAX_VALUE : recursionDepth)
                                .filter(p -> Files.isRegularFile(p))
                                .map(p -> {
                                    if (p.isAbsolute())
                                        return Path.of(uri).relativize(p).toString();
                                    else {
                                        return p.toString().replace(resourceFolder, "").replaceAll("^/+", "");
                                    }
                                })
                                .filter(s -> s.endsWith(".nbt"))
                                .map(s -> ResourceLocation.fromNamespaceAndPath(
                                        ns,
                                        (nm.isEmpty() ? "" : (nm + "/")) + s.substring(0, s.length() - 4)
                                ))
                                .sorted(Comparator.comparing(ResourceLocation::toString))
                                .map(r -> {
                                    LibWoverStructure.C.log.info("Loading Structure: " + r);
                                    try {
                                        return StructureNBT.create(r);
                                    } catch (Exception e) {
                                        LibWoverStructure.C.log.error("Unable to load Structure " + r, e);
                                    }
                                    return null;
                                })
                                .toList();
                } catch (IOException e) {
                    LibWoverStructure.C.log.error("Unable to load Resources: ", e);
                    return null;
                }
            }
        }
        return null;
    }

}
