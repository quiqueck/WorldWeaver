package de.ambertation.wover.testmod.item.mixin;

import de.ambertation.wover.tag.api.predefined.CommonBlockTags;
import de.ambertation.wover.testmod.entrypoint.TestModWoverItem;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Player.class)
public class PlayerMixin {
    @WrapOperation(method = "createAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;createLivingAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"))
    private static AttributeSupplier.Builder wover_test_createAttributes(Operation<AttributeSupplier.Builder> original) {
        return original.call().add(TestModWoverItem.OBSIDIAN_BLOCK_BREAK_SPEED);
    }

    @WrapOperation(method = "getDestroySpeed", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;)F"))
    float wover_test_getDestroySpeed(ItemStack instance, BlockState blockState, Operation<Float> original) {
        final LivingEntity entity = (LivingEntity) (Object) this;
        float speed = original.call(instance, blockState);
        if (blockState.is(CommonBlockTags.IS_OBSIDIAN)) {
            speed *= (float) entity.getAttributeValue(TestModWoverItem.OBSIDIAN_BLOCK_BREAK_SPEED);
        }
        return speed;
    }
}
