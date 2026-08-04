package de.ambertation.wover.tag.mixin;

import de.ambertation.wover.tag.impl.TagManagerImpl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Final
    @Shadow
    private String directory;

    @Inject(method = "load", at = @At(value = "RETURN"))
    public void wover_modifyTags(
            ResourceManager resourceManager,
            CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir
    ) {
        TagManagerImpl.didLoadTagMap(directory, cir.getReturnValue());
    }
}
