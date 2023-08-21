package org.betterx.wover.generator.impl.map.square;

import org.betterx.wover.generator.api.biomesource.WoverBiomePicker;
import org.betterx.wover.generator.api.map.BiomeChunk;
import org.betterx.wover.generator.api.map.BiomeMap;
import org.betterx.wover.math.api.MathHelper;
import org.betterx.wover.math.api.noise.OpenSimplexNoise;
import org.betterx.wover.util.function.TriConsumer;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;

import com.google.common.collect.Maps;

import java.util.Map;

public class SquareBiomeMap implements BiomeMap {
    private final Map<ChunkPos, SquareBiomeChunk> maps = Maps.newHashMap();
    private final OpenSimplexNoise noiseX;
    private final OpenSimplexNoise noiseZ;
    private final WorldgenRandom random;
    private final WoverBiomePicker picker;

    private final int sizeXZ;
    private final int depth;
    private final int size;

    private TriConsumer<Integer, Integer, Integer> processor;

    public SquareBiomeMap(long seed, int size, WoverBiomePicker picker) {
        random = new WorldgenRandom(new LegacyRandomSource(seed));
        noiseX = new OpenSimplexNoise(random.nextLong());
        noiseZ = new OpenSimplexNoise(random.nextLong());
        this.sizeXZ = size;
        depth = (int) Math.ceil(Math.log(size) / Math.log(2)) - 2;
        this.size = 1 << depth;
        this.picker = picker;
    }

    @Override
    public void clearCache() {
        if (maps.size() > 32) {
            maps.clear();
        }
    }

    @Override
    public WoverBiomePicker.PickableBiome getBiome(double x, double y, double z) {
        WoverBiomePicker.PickableBiome biome = getRawBiome(x, z);

        if (biome.getEdge() != null || (biome.getParentBiome() != null && biome.getParentBiome().getEdge() != null)) {
            WoverBiomePicker.PickableBiome search = biome;
            if (biome.getParentBiome() != null) {
                search = biome.getParentBiome();
            }

            int size = search.edgeSize;
            boolean edge = !search.isSame(getRawBiome(x + size, z));
            edge = edge || !search.isSame(getRawBiome(x - size, z));
            edge = edge || !search.isSame(getRawBiome(x, z + size));
            edge = edge || !search.isSame(getRawBiome(x, z - size));
            edge = edge || !search.isSame(getRawBiome(x - 1, z - 1));
            edge = edge || !search.isSame(getRawBiome(x - 1, z + 1));
            edge = edge || !search.isSame(getRawBiome(x + 1, z - 1));
            edge = edge || !search.isSame(getRawBiome(x + 1, z + 1));

            if (edge) {
                biome = search.getEdge();
            }
        }

        return biome;
    }

    @Override
    public void setChunkProcessor(TriConsumer<Integer, Integer, Integer> processor) {
        this.processor = processor;
    }

    @Override
    public BiomeChunk getChunk(int cx, int cz, boolean update) {
        ChunkPos cpos = new ChunkPos(cx, cz);
        SquareBiomeChunk chunk = maps.get(cpos);
        if (chunk == null) {
            synchronized (random) {
                random.setLargeFeatureWithSalt(0, cpos.x, cpos.z, 0);
                chunk = new SquareBiomeChunk(random, picker);
            }
            maps.put(cpos, chunk);

            if (update && processor != null) {
                processor.accept(cx, cz, chunk.getSide());
            }
        }

        return chunk;
    }

    private WoverBiomePicker.PickableBiome getRawBiome(double bx, double bz) {
        double x = bx * size / sizeXZ;
        double z = bz * size / sizeXZ;

        double px = bx * 0.2;
        double pz = bz * 0.2;

        for (int i = 0; i < depth; i++) {
            double nx = (x + noiseX.eval(px, pz)) / 2F;
            double nz = (z + noiseZ.eval(px, pz)) / 2F;

            x = nx;
            z = nz;

            px = px / 2 + i;
            pz = pz / 2 + i;
        }

        int ix = MathHelper.floor(x);
        int iz = MathHelper.floor(z);

        if ((ix & SquareBiomeChunk.MASK_WIDTH) == SquareBiomeChunk.MASK_WIDTH) {
            x += (iz / 2) & 1;
        }
        if ((iz & SquareBiomeChunk.MASK_WIDTH) == SquareBiomeChunk.MASK_WIDTH) {
            z += (ix / 2) & 1;
        }

        ChunkPos cpos = new ChunkPos(
                MathHelper.floor(x / SquareBiomeChunk.WIDTH),
                MathHelper.floor(z / SquareBiomeChunk.WIDTH)
        );
        SquareBiomeChunk chunk = maps.get(cpos);
        if (chunk == null) {
            synchronized (random) {
                random.setLargeFeatureWithSalt(0, cpos.x, cpos.z, 0);
                chunk = new SquareBiomeChunk(random, picker);
            }
            maps.put(cpos, chunk);
        }

        return chunk.getBiome(MathHelper.floor(x), MathHelper.floor(z));
    }
}
