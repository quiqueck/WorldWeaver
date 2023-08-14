package org.betterx.wover.datagen.api;

/**
 * A factory for multiple Data Providers.
 */
public interface WoverMultiProvider {
    /**
     * Called, when The DataProviders need to be added to a pack.
     *
     * @param pack The {@link PackBuilder} to register the providers to.
     */
    void registerAllProviders(PackBuilder pack);
}
