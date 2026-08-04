package de.ambertation.wover.poi.mixin;

import de.ambertation.wover.poi.api.PoiTypeExtension;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PoiType.class)
public class PoiTypeMixin implements PoiTypeExtension {
    private TagKey<Block> wover_tag = null;


    @Inject(method = "is", cancellable = true, at = @At("HEAD"))
    void wover_is(BlockState blockState, CallbackInfoReturnable<Boolean> cir) {
        if (wover_tag != null && blockState.is(wover_tag)) {
            cir.setReturnValue(true);
        }
    }

    public void wover_setTag(TagKey<Block> tag) {
        wover_tag = tag;
    }

    public TagKey<Block> wover_getTag() {
        return wover_tag;
    }

}
