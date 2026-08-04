package de.ambertation.wover.block.mixin.client;

import de.ambertation.wover.block.impl.client.render.ClientChestMaterials;

import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.ChestType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(Sheets.class)
public abstract class SheetsMixin {

    @Inject(method = "chooseMaterial(Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/level/block/state/properties/ChestType;Z)Lnet/minecraft/client/resources/model/Material;", at = @At("HEAD"), cancellable = true)
    private static void wover_chooseMaterial(
            BlockEntity blockEntity,
            ChestType chestType,
            boolean xmasTextures,
            CallbackInfoReturnable<Material> cir
    ) {
        final var mat = ClientChestMaterials.materialFor(blockEntity.getBlockState().getBlock());

        if (mat != null) {
            cir.setReturnValue(chooseMaterial(
                    chestType,
                    mat.single(), mat.left(), mat.right()
            ));
        }
    }

    @Shadow
    private static Material chooseMaterial(
            ChestType chestType,
            Material material,
            Material material2,
            Material material3
    ) {
        throw new AssertionError();
    }
}
