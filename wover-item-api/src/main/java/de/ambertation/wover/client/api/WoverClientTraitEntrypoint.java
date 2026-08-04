package de.ambertation.wover.client.api;

/**
 * The contract for the {@code wover.client.traits} entrypoint. Any mod that ships client-only trait
 * implementations (block/item model factories, render-layer / renderer appliers, ...) registers a class of this
 * type under {@code wover.client.traits} in its {@code fabric.mod.json}, and wires up its factories inside
 * {@link #registerClientTraits()} (e.g. {@code ClientBlockModelRegistry.register(myKey, myFactory)}).
 * <p>
 * wover invokes every {@code wover.client.traits} entrypoint through {@link ClientTraitBootstrap} in <em>both</em>
 * the client launch (renderer wiring) and the datagen launch (model generation), collecting all registrations
 * before it walks blocks/items. This gives third-party mods a single, launch-agnostic place to plug in, instead
 * of racing Fabric's {@code ClientModInitializer}. Implementations must only touch client-side registries, which
 * is safe because both launches that invoke them run with a client present.
 */
public interface WoverClientTraitEntrypoint {
    /**
     * Registers this mod's client-only trait implementations against the wover client registries. Called once,
     * before wover walks blocks/items.
     */
    void registerClientTraits();
}
