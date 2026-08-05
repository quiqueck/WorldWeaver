package de.ambertation.wover.loot.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;

import java.util.ArrayList;
import java.util.List;

/**
 * The contents of one {@code data/<namespace>/wover/loot_addition/<name>.json} file.
 *
 * <h2>Why this exists</h2>
 * Fabric's own {@code fabric-loot-api-v3} ({@code LootTableEvents.MODIFY}) lets a mod add loot in Java, but
 * only in Java: there is no vanilla or Fabric route for a datapack to extend a table it doesn't own. Loot
 * tables are a reloadable registry where an entry is <em>replaced</em> wholesale, never merged - two packs
 * that both ship {@code minecraft:chests/bastion_treasure} do not combine, the higher one simply wins. This
 * file format is the additive layer on top: additions are collected from every pack and applied to the tables
 * while the registry is being rebuilt, just before vanilla validates it - the very same pass
 * {@link LootTableAppenders} runs its Java-registered appenders through.
 *
 * <h2>Merging - the same rules as a vanilla tag</h2>
 * A file is identified by its id, {@code <namespace>:<name>}, derived from its path exactly like a tag's is.
 * <b>Every</b> pack that ships a file under that id is read, in pack order (lowest priority first), and their
 * {@link #additions()} are concatenated:
 * <ul>
 *     <li>{@code "replace": false} (the default) <b>appends</b> to whatever the packs below contributed;</li>
 *     <li>{@code "replace": true} <b>discards</b> everything the packs below contributed for this id, then
 *     appends its own - the escape hatch for a pack that wants to override, not extend, another pack's file;</li>
 *     <li>a file whose {@code "fabric:load_conditions"} are not met is treated as if it were not there at all -
 *     it contributes nothing and its {@code "replace"} does not fire either.</li>
 * </ul>
 * Two <em>different</em> file ids that target the same loot table never conflict: both apply, ordered by file
 * id and then by position within the file, so the result is deterministic and independent of mod load order.
 * Additions are purely additive, so the only way two packs can interact is through
 * {@link LootAdditionOp.AddCondition}, which AND's - it can never widen what another pack restricted.
 *
 * <h2>Example</h2>
 * {@code data/betternether/wover/loot_addition/portal_chests.json}:
 * <pre>{@code
 * {
 *   "replace": false,
 *   "fabric:load_conditions": [
 *     { "condition": "fabric:all_mods_loaded", "values": ["betternether"] }
 *   ],
 *   "additions": [
 *     {
 *       "target": ["minecraft:chests/ruined_portal", "minecraft:chests/nether_bridge"],
 *       "operations": [
 *         {
 *           "type": "add_pool",
 *           "pools": [
 *             {
 *               "rolls": { "type": "minecraft:uniform", "min": 0.0, "max": 4.0 },
 *               "entries": [
 *                 { "type": "minecraft:item", "name": "betternether:blue_obsidian", "weight": 1 },
 *                 { "type": "minecraft:empty", "weight": 9 }
 *               ]
 *             }
 *           ]
 *         }
 *       ]
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @param replace    whether this file discards the additions the packs below it contributed for the same id
 * @param conditions Fabric resource conditions gating the whole file
 * @param additions  the additions this file contributes
 */
public record LootAdditionFile(
        boolean replace,
        List<ResourceCondition> conditions,
        List<LootAddition> additions
) {
    /**
     * Codec for a whole {@code wover/loot_addition} file.
     */
    public static final Codec<LootAdditionFile> CODEC = RecordCodecBuilder.create(instance -> instance
            .group(
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(LootAdditionFile::replace),
                    ResourceCondition.LIST_CODEC
                            .optionalFieldOf(ResourceConditions.CONDITIONS_KEY, List.of())
                            .forGetter(LootAdditionFile::conditions),
                    LootAddition.CODEC.listOf().fieldOf("additions").forGetter(LootAdditionFile::additions)
            )
            .apply(instance, LootAdditionFile::new));

    /**
     * @return a new, empty builder ({@code replace = false}, no conditions)
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builds a {@link LootAdditionFile} for
     * {@link de.ambertation.wover.datagen.api.provider.WoverLootAdditionProvider datagen}.
     */
    public static final class Builder {
        private final List<ResourceCondition> conditions = new ArrayList<>();
        private final List<LootAddition.Builder> additions = new ArrayList<>();
        private boolean replace = false;

        Builder() {
        }

        /**
         * Makes this file discard whatever the packs below it contributed for the same file id. Rarely what a
         * mod wants - the default, appending, is what lets several packs extend the same table.
         *
         * @return this builder, for chaining
         */
        public Builder replace() {
            this.replace = true;
            return this;
        }

        /**
         * Gates the whole file on Fabric resource conditions, e.g.
         * {@code ResourceConditions.allModsLoaded("some_other_mod")}. All of them must hold.
         *
         * @param conditions the conditions to add
         * @return this builder, for chaining
         */
        public Builder when(ResourceCondition... conditions) {
            this.conditions.addAll(List.of(conditions));
            return this;
        }

        /**
         * Starts a new addition for the given loot tables. The returned builder is already registered with
         * this file, so it does not have to be handed back.
         *
         * @param targets the loot tables to extend
         * @return the addition builder, for chaining
         */
        @SafeVarargs
        public final LootAddition.Builder forTables(ResourceKey<LootTable>... targets) {
            final LootAddition.Builder builder = LootAddition.forTables(targets);
            additions.add(builder);
            return builder;
        }

        /**
         * @return the finished file
         */
        public LootAdditionFile build() {
            if (additions.isEmpty()) {
                throw new IllegalStateException("A wover/loot_addition file must contain at least one addition");
            }
            return new LootAdditionFile(
                    replace,
                    List.copyOf(conditions),
                    additions.stream().map(LootAddition.Builder::build).toList()
            );
        }
    }
}
