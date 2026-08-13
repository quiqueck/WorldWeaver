package de.ambertation.wover.biome.api.modification;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * Where a {@link BiomeModification}'s features are inserted into a decoration step that already has
 * features in it.
 * <p>
 * <b>This is not cosmetic, it decides whether a world can generate at all.</b> Vanilla's
 * {@link net.minecraft.world.level.biome.FeatureSorter} builds <i>one</i> global order per
 * {@link GenerationStep.Decoration} out of every biome in a dimension, by topologically sorting the
 * pairwise "a comes before b" constraints each biome's own list contributes. Two biomes that disagree
 * about the order of the same two features are a cycle, and a cycle is a hard
 * {@link IllegalStateException} the first time a chunk reaches the {@code features} status - i.e. the
 * dimension becomes unenterable.
 * <p>
 * That is easy to cause by accident, because a mod adds the same feature to biomes in two different ways:
 * baked into its own biomes at build time, and injected into foreign biomes at runtime. Wover applies its
 * modifications at {@code createLevels}, deliberately <i>after</i> Fabric's own biome modifications (see
 * {@code BiomeModificationRegistryImpl#initialize}), so a third-party mod that adds features to the same
 * step through Fabric's API gets its features in first. {@link #APPEND} then puts ours behind theirs in
 * the foreign biome while our own biomes still have ours in front - the exact contradiction the sorter
 * rejects. Reproduced with BetterEnd + TechReborn, which both add ores to
 * {@link GenerationStep.Decoration#UNDERGROUND_ORES} in the End
 * (<a href="https://github.com/quiqueck/BetterEnd/issues/596">BetterEnd#596</a>).
 * <p>
 * So pick by where the same features sit in <i>your own</i> biomes:
 * <ul>
 *     <li>{@link #PREPEND} when they are the first thing in that step (typical for a step vanilla leaves
 *     empty, like ores in the End) - our features then lead in every biome, foreign or not.</li>
 *     <li>{@link #APPEND} when they follow features that the target biomes already have (BetterEnd's
 *     crashed ship sits behind {@code minecraft:end_gateway_return}, so it has to stay behind it
 *     everywhere).</li>
 * </ul>
 */
public enum FeaturePosition implements StringRepresentable {
    /** Insert behind everything the step already contains. The default, and the historical behaviour. */
    APPEND("append"),
    /**
     * Insert in front of everything the step already contains.
     * <p>
     * Successive prepending modifications keep their application order rather than reversing it: the
     * second one to run inserts behind the first one's features, not in front of them.
     */
    PREPEND("prepend");

    /** Codec for the {@code "feature_position"} field of a {@link BiomeModification}. */
    public static final Codec<FeaturePosition> CODEC = StringRepresentable.fromEnum(FeaturePosition::values);

    private final String name;

    FeaturePosition(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
