package org.betterx.wover.generator.mixin.DSL;

import org.betterx.wover.generator.impl.chunkgenerator.ChunkGeneratorManagerImpl;

import com.mojang.datafixers.types.templates.TypeTemplate;
import net.minecraft.util.datafix.schemas.V2551;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Map;
import java.util.function.Supplier;

@Mixin(V2551.class)
public class V2551Mixin {
    @ModifyArg(method = "method_28297", at = @At(value = "INVOKE", target = "Lcom/mojang/datafixers/DSL;taggedChoiceLazy(Ljava/lang/String;Lcom/mojang/datafixers/types/Type;Ljava/util/Map;)Lcom/mojang/datafixers/types/templates/TaggedChoice;"))
    private static Map<String, Supplier<TypeTemplate>> wover_addGenerator(Map<String, Supplier<TypeTemplate>> map) {
        return ChunkGeneratorManagerImpl.addGeneratorDSL(map);
    }
}
