package de.ambertation.wover.block.api.model;

import de.ambertation.wover.entrypoint.LibWoverBlock;
import de.ambertation.wover.item.api.model.ItemModelBinding;
import de.ambertation.wover.item.api.model.ItemModelKey;
import de.ambertation.wover.item.api.model.ItemModelKeys;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;
import org.jetbrains.annotations.Nullable;

/**
 * A library of common block/item model patterns. Every method returns a common-safe
 * {@link BlockModelBinding}/{@link ItemModelBinding} - a {@code (ModelKey, payload)} pair - so it can be called
 * unconditionally from common block/item registration code (like any {@code BlockTraits} entry) without dragging
 * in vanilla client-only datagen types. The actual generator code lives in the client source set, registered
 * against the matching {@link BlockModelKeys}/{@link ItemModelKeys} key and resolved while walking blocks/items
 * at datagen time.
 */
public class ModelTraitLibrary {
    /**
     * Attaches an arbitrary model shape by key and payload. Third-party mods use this (or a helper of their own)
     * to attach a model they registered a client factory for.
     *
     * @param key     the model shape to generate
     * @param payload the common-safe payload for the shape
     * @param <P>     the payload type
     * @return the binding
     */
    public static <P> BlockModelBinding bind(ModelKey<P> key, P payload) {
        return BlockModelBinding.of(key, payload);
    }

    /**
     * Attaches an arbitrary item model shape by key and payload.
     *
     * @param key     the item model shape to generate
     * @param payload the common-safe payload for the shape
     * @param <P>     the payload type
     * @return the binding
     */
    public static <P> ItemModelBinding bindItem(ItemModelKey<P> key, P payload) {
        return ItemModelBinding.of(key, payload);
    }

    /** A bark/stripped-bark rotated pillar reusing {@code logBlock}'s {@code _side} texture. */
    public static BlockModelBinding bark(
            Supplier<Block> logBlock,
            boolean mirroredTexture,
            String... alternativeTextureSuffixe
    ) {
        return bind(BlockModelKeys.BARK, new BlockModelKeys.BarkParams(logBlock, mirroredTexture, alternativeTextureSuffixe));
    }

    /** A vanilla-style barrel model (closed/open top variants, rotated by facing). */
    public static BlockModelBinding barrel() {
        return bind(BlockModelKeys.BARREL, null);
    }

    /** A vanilla-style bookshelf model. */
    public static BlockModelBinding bookshelf(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.BOOKSHELF, planksMaterial);
    }

    /**
     * A vanilla-style chiseled bookshelf model: the body and inventory models plus the twelve
     * empty/occupied book-slot overlays, dispatched by a multipart blockstate over facing and the six
     * {@code slot_N_occupied} properties. Textures are the block's own {@code _top}/{@code _side}/
     * {@code _empty}/{@code _occupied} variants.
     */
    public static BlockModelBinding chiseledBookshelf() {
        return bind(BlockModelKeys.CHISELED_BOOKSHELF, null);
    }

    /** A vanilla-style rotated pillar model. */
    public static BlockModelBinding pillar() {
        return bind(BlockModelKeys.PILLAR, null);
    }

    /** A vanilla-style button model. */
    public static BlockModelBinding button(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.BUTTON, planksMaterial);
    }

    /** The chest-special-renderer id used for chests built through {@link #chest}. */
    public static ResourceLocation chestRendered = LibWoverBlock.C.mk("wooden_chest");

    /** A vanilla-style chest model (particle-only block + special-renderer item model). */
    public static BlockModelBinding chest(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.CHEST, planksMaterial);
    }

    /** A log model: rotated pillar with the block's own side/top textures. */
    public static BlockModelBinding log(boolean mirroredTexture, String... alternativeTextureSuffixe) {
        return bind(BlockModelKeys.LOG, new BlockModelKeys.LogParams(mirroredTexture, alternativeTextureSuffixe));
    }

    /** A vanilla-style crafting-table-like model (distinct top/front/side/bottom textures). */
    public static BlockModelBinding craftingTable(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.CRAFTING_TABLE, planksMaterial);
    }

    /** A vanilla-style slab model (bottom/top/double variants). */
    public static BlockModelBinding slab(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.SLAB, planksMaterial);
    }

    /** A plain full-cube model plus a delegated item model, using the block's own texture. */
    public static BlockModelBinding planks() {
        return bind(BlockModelKeys.PLANKS, null);
    }

    /** A vanilla-style composter model. */
    public static BlockModelBinding composter() {
        return bind(BlockModelKeys.COMPOSTER, null);
    }

    /** A vanilla-style door model. */
    public static BlockModelBinding door() {
        return bind(BlockModelKeys.DOOR, null);
    }

    /** A vanilla-style fence model. */
    public static BlockModelBinding fence(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.FENCE, planksMaterial);
    }

    /** A vanilla-style fence gate model. */
    public static BlockModelBinding gate(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.GATE, planksMaterial);
    }

    /** A vanilla-style hanging sign model. */
    public static BlockModelBinding hangingSign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return bind(BlockModelKeys.HANGING_SIGN, new BlockModelKeys.SignParams(logMaterial, wallSignBlock));
    }

    /** A vanilla-style (standing/wall) sign model. */
    public static BlockModelBinding sign(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {
        return bind(BlockModelKeys.SIGN, new BlockModelKeys.SignParams(logMaterial, wallSignBlock));
    }

    /** A vanilla-style ladder model. */
    public static BlockModelBinding ladder() {
        return bind(BlockModelKeys.LADDER, null);
    }

    /** A vanilla-style pressure plate model. */
    public static BlockModelBinding pressurePlate(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.PRESSURE_PLATE, planksMaterial);
    }

    /** A chain-style model reusing vanilla's {@code minecraft:block/chain} shape. */
    public static BlockModelBinding chain() {
        return bind(BlockModelKeys.CHAIN, null);
    }

    /** A vanilla iron-bars-style model. */
    public static BlockModelBinding bars() {
        return bind(BlockModelKeys.BARS, null);
    }

    /** A vanilla-style stairs model. */
    public static BlockModelBinding stairs(Supplier<Block> planksMaterial) {
        return bind(BlockModelKeys.STAIRS, planksMaterial);
    }

    /** A vanilla-style (non-orientable) trapdoor model. */
    public static BlockModelBinding trapdoor() {
        return bind(BlockModelKeys.TRAPDOOR, null);
    }

    /** A vanilla-style orientable trapdoor model. */
    public static BlockModelBinding orientableTrapdoor() {
        return bind(BlockModelKeys.ORIENTABLE_TRAPDOOR, null);
    }

    /** A vanilla-style wall model (post/side variants). */
    public static BlockModelBinding wall(Supplier<Block> sourceMaterial) {
        return bind(BlockModelKeys.WALL, sourceMaterial);
    }

    /** A plain full-cube model using the block's own texture. */
    public static BlockModelBinding cube() {
        return bind(BlockModelKeys.CUBE, null);
    }

    /** A plain full-cube model plus a matching flat item icon. */
    public static BlockModelBinding cubeWithFlatItem() {
        return bind(BlockModelKeys.CUBE_WITH_FLAT_ITEM, null);
    }

    /** A vanilla cross-shaped (untinted) plant model plus a matching flat item icon. */
    public static BlockModelBinding crossPlant() {
        return bind(BlockModelKeys.CROSS_PLANT, null);
    }

    /** A block whose blockstate/model and item model are both hand-authored static assets. */
    public static BlockModelBinding externalModel() {
        return bind(BlockModelKeys.EXTERNAL_MODEL, null);
    }

    /** Like {@link #externalModel()}, but the item model is derived from the block's own texture. */
    public static BlockModelBinding externalModelDelegatedItem() {
        return bind(BlockModelKeys.EXTERNAL_MODEL_DELEGATED_ITEM, null);
    }

    /** Like {@link #externalModel()}, but the item model points at an explicit model location. */
    public static BlockModelBinding externalModelDelegatedItem(Supplier<ResourceLocation> itemModel) {
        return bind(BlockModelKeys.EXTERNAL_MODEL_DELEGATED_ITEM_LOCATION, itemModel);
    }

    /** A hand-authored blockstate/model with a generated flat item model (from an optional texture). */
    public static BlockModelBinding externalModelFlatItem(@Nullable Supplier<ResourceLocation> itemTexture) {
        return bind(BlockModelKeys.EXTERNAL_MODEL_FLAT_ITEM, itemTexture);
    }

    /** A plain flat item model using the item's own texture. */
    public static ItemModelBinding itemModel() {
        return bindItem(ItemModelKeys.FLAT_ITEM, null);
    }

    /** A plain flat item model, reusing another item's texture as the model's layer. */
    public static ItemModelBinding itemModel(Supplier<Item> material) {
        return bindItem(ItemModelKeys.FLAT_ITEM_FROM, material);
    }

    /** A vanilla-style elytra item model, dispatching normal/broken by durability. */
    public static ItemModelBinding elytra() {
        return bindItem(ItemModelKeys.ELYTRA, null);
    }

    /** A plain flat item model for a boat. */
    public static ItemModelBinding boat() {
        return bindItem(ItemModelKeys.BOAT, null);
    }

    /** A plain flat item model for a chest boat (identical to {@link #boat()}). */
    public static ItemModelBinding chestBoat() {
        return boat();
    }
}
