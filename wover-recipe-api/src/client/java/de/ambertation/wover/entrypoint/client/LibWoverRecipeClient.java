package de.ambertation.wover.entrypoint.client;

import de.ambertation.wover.recipe.impl.SyncedRecipesImpl;
import de.ambertation.wunderlib.network.ClientNetworkRegistry;
import de.ambertation.wunderlib.network.ExecutionPhase;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

@Environment(EnvType.CLIENT)
public class LibWoverRecipeClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // NETWORK_THREAD is load-bearing, not an optimisation. GAME_THREAD would hand the batch to
        // Minecraft.execute(), which appends it to the end of the client's task queue - behind the
        // ClientboundUpdateRecipesPacket that arrived right after it and that JEI rebuilds its plugins on. The
        // recipes would then land a moment after the viewer had already read an empty store, which looks
        // exactly like no synchronisation at all. Running where the payload is delivered keeps the two in the
        // order the server sent them. Nothing here touches game state, so there is nothing to schedule for.
        ClientNetworkRegistry.addClientHandler(
                SyncedRecipesImpl.MESSAGE,
                ExecutionPhase.NETWORK_THREAD,
                (batch, ctx) -> SyncedRecipesImpl.acceptOnClient(batch)
        );

        // Without this, the recipes of the last server would still be listed after joining one that does not
        // have the mod - the join push is what normally replaces them, and that server sends none.
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> SyncedRecipesImpl.clearOnClient());
    }
}
