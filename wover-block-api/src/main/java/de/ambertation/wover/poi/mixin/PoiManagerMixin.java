package de.ambertation.wover.poi.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.ai.village.poi.PoiSection;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

import com.mojang.serialization.Codec;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Older WorldWeaver versions registered the FOOT half of every bed as a {@code minecraft:home} POI
 * (quiqueck/BCLib#231). Those records were saved into sections flagged {@code Valid}, and vanilla's
 * {@link PoiManager#checkConsistencyWithBlocks} only rebuilds <em>invalid</em> sections
 * ({@code PoiSection.refresh} is a no-op otherwise). And since the foot state no longer maps to HOME,
 * breaking the bed doesn't remove the record either - so the stale entries would stay forever.
 * <p>
 * On chunk load we therefore drop every HOME record whose block state isn't a HOME state anymore.
 * Records of any other type are left alone.
 */
@Mixin(PoiManager.class)
public abstract class PoiManagerMixin extends SectionStorage<PoiSection, PoiSection.Packed> {
    public PoiManagerMixin(
            SimpleRegionStorage simpleRegionStorage,
            Codec<PoiSection.Packed> codec,
            Function<PoiSection, PoiSection.Packed> packer,
            BiFunction<PoiSection.Packed, Runnable, PoiSection> unpacker,
            Function<Runnable, PoiSection> factory,
            RegistryAccess registryAccess,
            ChunkIOErrorReporter errorReporter,
            LevelHeightAccessor levelHeightAccessor
    ) {
        super(simpleRegionStorage, codec, packer, unpacker, factory, registryAccess, errorReporter, levelHeightAccessor);
    }

    @Inject(method = "checkConsistencyWithBlocks", at = @At("HEAD"))
    private void wover_removeStaleHomeRecords(
            SectionPos sectionPos,
            LevelChunkSection blockSection,
            CallbackInfo ci
    ) {
        this.getOrLoad(sectionPos.asLong()).ifPresent(section -> {
            // PoiSection.isValid() isn't accessible on this version; removing stale records from a section that
            // vanilla is about to rebuild anyway is harmless

            final List<BlockPos> stale = section
                    .getRecords(type -> type.is(PoiTypes.HOME), PoiManager.Occupancy.ANY)
                    .map(PoiRecord::getPos)
                    .filter(pos -> !PoiTypes
                            .forState(blockSection.getBlockState(
                                    SectionPos.sectionRelative(pos.getX()),
                                    SectionPos.sectionRelative(pos.getY()),
                                    SectionPos.sectionRelative(pos.getZ())
                            ))
                            .map(type -> type.is(PoiTypes.HOME))
                            .orElse(false))
                    .toList();
            stale.forEach(section::remove);
        });
    }
}
