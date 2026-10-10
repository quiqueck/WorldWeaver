package de.ambertation.wover.testmod.biome.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Regression guard for issue #19 (crash on bootstrap with Nyf's Quivers).
 * <p>
 * Nyf's Quivers hooks {@code Registries.tagsDirPath} and, from there, reflects over static fields, which
 * force-initializes {@code RegistryDataLoader}. Our {@code RegistryDataLoaderMixin} runs the
 * {@code wover.datapack.registry} entrypoints from that class's static initializer. When the first
 * {@code tagsDirPath} call comes from {@code TagManagerImpl.<clinit>}, those entrypoints see the tag API
 * half initialized ({@code TagManager.BIOMES == null}) and the game crashed with an
 * {@link ExceptionInInitializerError}.
 * <p>
 * This mixin reproduces exactly that: the first time {@code tagsDirPath} is called from inside
 * {@code TagManagerImpl}'s static initializer, it force-initializes {@code RegistryDataLoader}. Without the
 * fix the testmod server does not boot; with it, the biome GameTests (in particular
 * {@code BiomeTagReloadGameTest}, which needs the biome-tag subscription made in that cycle) pass.
 */
@Mixin(Registries.class)
public class RegistriesMixin {
    private static boolean wover_test_didForceInit = false;

    @Inject(method = "tagsDirPath", at = @At("HEAD"))
    private static void wover_test_forceRegistryDataLoaderInit(
            ResourceKey<? extends Registry<?>> registryKey,
            CallbackInfoReturnable<String> cir
    ) {
        if (wover_test_didForceInit) return;
        final boolean fromTagManagerInit = StackWalker
                .getInstance()
                .walk(frames -> frames.anyMatch(f ->
                        f.getClassName().equals("de.ambertation.wover.tag.impl.TagManagerImpl")
                                && f.getMethodName().equals("<clinit>")
                ));
        if (!fromTagManagerInit) return;
        wover_test_didForceInit = true;

        try {
            Class.forName(
                    "net.minecraft.resources.RegistryDataLoader",
                    true,
                    Registries.class.getClassLoader()
            );
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
