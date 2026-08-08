package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.recipe.impl.SyncedRecipesImpl;

import net.minecraft.server.players.PlayerList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Re-sends the synchronised recipes after {@code /reload}.
 * <p>
 * {@code PlayerList.reloadResources} is the method that tells every connected client the recipes changed, and
 * a recipe viewer rebuilds itself off exactly that packet. Injecting at its head means the new recipe set is
 * already in place (the caller swapped the {@code RecipeManager} in before getting here) and the client has
 * the fresh list in hand by the time the packet it reacts to arrives. Fabric's {@code END_DATA_PACK_RELOAD}
 * fires too late for that - it waits for the whole reload future, which is after this method returns.
 *
 * @see SyncedRecipesImpl
 */
@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "reloadResources", at = @At("HEAD"))
    void wover_resyncRecipes(CallbackInfo ci) {
        SyncedRecipesImpl.onDataPackReload((PlayerList) (Object) this);
    }
}
