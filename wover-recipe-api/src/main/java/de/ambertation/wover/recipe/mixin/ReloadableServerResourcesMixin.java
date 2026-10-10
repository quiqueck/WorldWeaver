package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.item.crafting.RecipeManager;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Marks the {@link RecipeManager} and {@link ServerAdvancementManager} that belong to the server's resource
 * reload, so {@link RecipeManagerMixin} and {@link ServerAdvancementManagerMixin} only act on those two.
 * <p>
 * Since 26.3 both managers fill themselves in their constructors, and our mixins on those constructors run for
 * <em>every</em> instance. Vanilla builds them in exactly one place - this constructor, for the initial load
 * and every {@code /reload} - but other mods build their own: Polymer's {@code FakeWorld} creates a
 * {@code RecipeManager} over a minimal registry set the first time a virtual entity (a Universal Graves grave,
 * for example) is set up. Running the runtime recipe contributors against that set crashed the server, re-ran
 * every {@code BOOTSTRAP_RECIPES} subscriber, and replaced the advancements parked for the real reload.
 * <p>
 * Marking the construction site is the robust way to tell the managers apart: it does not depend on who else
 * constructs one, on class names, or on which registries happen to be present, and it restores exactly the
 * pre-26.3 behaviour, where runtime recipes were only added in the reload's {@code apply} phase.
 */
@Mixin(ReloadableServerResources.class)
public class ReloadableServerResourcesMixin {
    @WrapOperation(
            method = "<init>",
            at = @At(value = "NEW", target = "net/minecraft/world/item/crafting/RecipeManager")
    )
    private RecipeManager wover_markServerRecipeManager(
            HolderLookup.Provider registries,
            Operation<RecipeManager> original
    ) {
        return RecipeRuntimeProviderImpl.inServerReload(registries, () -> original.call(registries));
    }

    @WrapOperation(
            method = "<init>",
            at = @At(value = "NEW", target = "net/minecraft/server/ServerAdvancementManager")
    )
    private ServerAdvancementManager wover_markServerAdvancementManager(
            HolderLookup.Provider registries,
            Operation<ServerAdvancementManager> original
    ) {
        return RecipeRuntimeProviderImpl.inServerReload(registries, () -> original.call(registries));
    }
}
