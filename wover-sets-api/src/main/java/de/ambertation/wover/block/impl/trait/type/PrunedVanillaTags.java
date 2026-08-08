package de.ambertation.wover.block.impl.trait.type;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * Vanilla tags that lost their Java constant in 26.3 but are still <em>shipped</em> by vanilla as data.
 * <p>
 * 26.3 pruned a batch of {@link ItemTags} handles ({@code DOORS}, {@code BUTTONS}, {@code STONE_BUTTONS},
 * {@code FENCES}, {@code STAIRS}, {@code SLABS}, {@code TRAPDOORS}) and the {@link BlockTags} handle
 * {@code LOGS_THAT_BURN}. In every case only the Java handle went away - vanilla still contains
 * {@code data/minecraft/tags/item/<name>.json} resp. {@code data/minecraft/tags/block/logs_that_burn.json},
 * with unchanged contents and unchanged meaning.
 * <p>
 * Dropping the {@code addTags(...)} calls that used those constants would therefore have silently removed our
 * blocks from live vanilla tags (and removed the generated tag files from the committed datagen output), so the
 * keys are recreated here instead. This mirrors what {@code wover-block-api}'s testmod does for
 * {@code ItemTags.DOORS} (commit {@code 98821f36}).
 * <p>
 * The block-tag siblings ({@link BlockTags#DOORS}, {@link BlockTags#BUTTONS}, {@link BlockTags#SLABS}, ...) all
 * kept their constants and are still used directly.
 */
@ApiStatus.Internal
final class PrunedVanillaTags {
    private PrunedVanillaTags() {
    }

    private static TagKey<Item> item(String name) {
        return TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace(name));
    }

    private static TagKey<Block> block(String name) {
        return TagKey.create(Registries.BLOCK, Identifier.withDefaultNamespace(name));
    }

    /** {@code minecraft:doors} - was {@code ItemTags.DOORS}. */
    static final TagKey<Item> ITEM_DOORS = item("doors");
    /** {@code minecraft:buttons} - was {@code ItemTags.BUTTONS}. */
    static final TagKey<Item> ITEM_BUTTONS = item("buttons");
    /** {@code minecraft:stone_buttons} - was {@code ItemTags.STONE_BUTTONS}. */
    static final TagKey<Item> ITEM_STONE_BUTTONS = item("stone_buttons");
    /** {@code minecraft:fences} - was {@code ItemTags.FENCES}. */
    static final TagKey<Item> ITEM_FENCES = item("fences");
    /** {@code minecraft:stairs} - was {@code ItemTags.STAIRS}. */
    static final TagKey<Item> ITEM_STAIRS = item("stairs");
    /** {@code minecraft:slabs} - was {@code ItemTags.SLABS}. */
    static final TagKey<Item> ITEM_SLABS = item("slabs");
    /** {@code minecraft:trapdoors} - was {@code ItemTags.TRAPDOORS}. */
    static final TagKey<Item> ITEM_TRAPDOORS = item("trapdoors");

    /** {@code minecraft:logs_that_burn} - was {@code BlockTags.LOGS_THAT_BURN}. */
    static final TagKey<Block> BLOCK_LOGS_THAT_BURN = block("logs_that_burn");
}
