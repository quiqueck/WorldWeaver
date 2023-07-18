package org.betterx.wover.tag.mixin;

import org.betterx.wover.tag.impl.TagManagerImpl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagLoader;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.List;
import java.util.Map;

@Mixin(TagLoader.class)
public class TagLoaderMixin {
    @Final
    @Shadow
    private String directory;

    @ModifyArg(method = "loadAndBuild", at = @At(value = "INVOKE", target = "Lnet/minecraft/tags/TagLoader;build(Ljava/util/Map;)Ljava/util/Map;"))
    public Map<ResourceLocation, List<TagLoader.EntryWithSource>> be_modifyTags(Map<ResourceLocation, List<TagLoader.EntryWithSource>> tagsMap) {
        return TagManagerImpl.didLoadTagMap(directory, tagsMap);
    }
}
