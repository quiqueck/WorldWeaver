package de.ambertation.wover.block.mixin.client;

import de.ambertation.wover.block.impl.client.render.ClientChestMaterials;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.renderer.blockentity.state.ChestRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.ChestType;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * The vanilla chest renderer used to resolve its {@code Material}/{@code SpriteId} directly from the
 * {@link net.minecraft.world.level.block.entity.BlockEntity} being rendered. It now resolves a fixed
 * {@link ChestRenderState.ChestMaterialType} once during state extraction and only turns that (plus the
 * {@link ChestType}) into a {@link SpriteId} via {@link Sheets#chooseSprite} at submit time - which no longer
 * has access to the block being rendered. We redirect that call so blocks carrying the
 * {@link de.ambertation.wover.block.api.render.ChestRendererBinding} marker still get their custom sprites.
 */
@Environment(EnvType.CLIENT)
@Mixin(ChestRenderer.class)
public abstract class SheetsMixin {

    @Redirect(
            method = "submit(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/Sheets;chooseSprite(Lnet/minecraft/client/renderer/blockentity/state/ChestRenderState$ChestMaterialType;Lnet/minecraft/world/level/block/state/properties/ChestType;)Lnet/minecraft/client/resources/model/sprite/SpriteId;"
            )
    )
    private SpriteId wover_chooseSprite(
            ChestRenderState.ChestMaterialType material,
            ChestType chestType,
            ChestRenderState state,
            PoseStack poseStack,
            SubmitNodeCollector collector,
            CameraRenderState cameraState
    ) {
        final var level = Minecraft.getInstance().level;
        final Block block = level != null ? level.getBlockState(state.blockPos).getBlock() : null;
        final var mat = block != null ? ClientChestMaterials.materialFor(block) : null;

        if (mat != null) {
            return switch (chestType) {
                case LEFT -> mat.left();
                case RIGHT -> mat.right();
                default -> mat.single();
            };
        }

        return Sheets.chooseSprite(material, chestType);
    }
}
