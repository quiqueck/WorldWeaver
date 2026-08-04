package de.ambertation.wover.block.api.model;

import de.ambertation.wover.block.api.trait.BlockTraitKey;
import de.ambertation.wover.entrypoint.LibWoverBlock;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * The catalogue of built-in {@link ModelKey}s, one per ready-made shape in {@link ModelTraitLibrary}. Each key
 * identifies a client-side model factory that {@code wover-block-api}'s client source set registers against it
 * (see {@code ClientBlockModelRegistry} / the {@code wover.client.traits} entrypoint). Payload types use only
 * common-safe values, so the matching {@link BlockModelBinding} can be attached from common block-creation code.
 * <p>
 * Third-party mods are not limited to this catalogue: they can define their own {@code ModelKey} + payload in
 * their common code and register a factory against it from a {@code wover.client.traits} entrypoint.
 */
public class BlockModelKeys {
    private static <P> ModelKey<P> key(String path) {
        return new ModelKey<>(BlockTraitKey.ofUnique(LibWoverBlock.C, "model/" + path));
    }

    /** Payload for {@link #BARK}: the log block to reuse, mirroring, and extra texture-suffix variants. */
    public record BarkParams(
            Supplier<Block> logBlock,
            boolean mirroredTexture,
            String... alternativeTextureSuffixe
    ) {}

    /** Payload for {@link #LOG}: mirroring and extra texture-suffix variants (textures come from the block itself). */
    public record LogParams(boolean mirroredTexture, String... alternativeTextureSuffixe) {}

    /** Payload for {@link #SIGN}/{@link #HANGING_SIGN}: the log material and this sign's wall variant. */
    public record SignParams(Supplier<Block> logMaterial, Supplier<Block> wallSignBlock) {}

    /** A bark/stripped-bark rotated pillar reusing the log block's side texture. */
    public static final ModelKey<BarkParams> BARK = key("bark");
    /** A vanilla-style barrel (closed/open top, rotated by facing). */
    public static final ModelKey<Void> BARREL = key("barrel");
    /** A vanilla-style bookshelf using the planks material for its frame. */
    public static final ModelKey<Supplier<Block>> BOOKSHELF = key("bookshelf");
    /** A vanilla-style rotated pillar using the block's own textures. */
    public static final ModelKey<Void> PILLAR = key("pillar");
    /** A vanilla-style button using the planks material. */
    public static final ModelKey<Supplier<Block>> BUTTON = key("button");
    /** A vanilla-style chest (particle-only block + special-renderer item model) using the planks material. */
    public static final ModelKey<Supplier<Block>> CHEST = key("chest");
    /** A log rotated pillar using the block's own side/top textures. */
    public static final ModelKey<LogParams> LOG = key("log");
    /** A crafting-table-like model (distinct top/front/side/bottom faces) using the planks material. */
    public static final ModelKey<Supplier<Block>> CRAFTING_TABLE = key("crafting_table");
    /** A vanilla-style slab (bottom/top/double) using the planks material. */
    public static final ModelKey<Supplier<Block>> SLAB = key("slab");
    /** A plain full-cube model plus a delegated item model, using the block's own texture. */
    public static final ModelKey<Void> PLANKS = key("planks");
    /** A vanilla-style composter. */
    public static final ModelKey<Void> COMPOSTER = key("composter");
    /** A vanilla-style door. */
    public static final ModelKey<Void> DOOR = key("door");
    /** A vanilla-style fence using the planks material. */
    public static final ModelKey<Supplier<Block>> FENCE = key("fence");
    /** A vanilla-style fence gate using the planks material. */
    public static final ModelKey<Supplier<Block>> GATE = key("gate");
    /** A vanilla-style hanging sign. */
    public static final ModelKey<SignParams> HANGING_SIGN = key("hanging_sign");
    /** A vanilla-style (standing/wall) sign. */
    public static final ModelKey<SignParams> SIGN = key("sign");
    /** A vanilla-style ladder. */
    public static final ModelKey<Void> LADDER = key("ladder");
    /** A vanilla-style pressure plate using the planks material. */
    public static final ModelKey<Supplier<Block>> PRESSURE_PLATE = key("pressure_plate");
    /** A chain-style model reusing vanilla's {@code minecraft:block/chain} shape and the block's own texture. */
    public static final ModelKey<Void> CHAIN = key("chain");
    /** A vanilla iron-bars-style model using the block's own texture. */
    public static final ModelKey<Void> BARS = key("bars");
    /** A vanilla-style stairs model using the planks material. */
    public static final ModelKey<Supplier<Block>> STAIRS = key("stairs");
    /** A vanilla-style (non-orientable) trapdoor. */
    public static final ModelKey<Void> TRAPDOOR = key("trapdoor");
    /** A vanilla-style orientable trapdoor. */
    public static final ModelKey<Void> ORIENTABLE_TRAPDOOR = key("orientable_trapdoor");
    /** A vanilla-style wall (post/side variants) using the source material. */
    public static final ModelKey<Supplier<Block>> WALL = key("wall");
    /** A plain full-cube model using the block's own texture. */
    public static final ModelKey<Void> CUBE = key("cube");
    /** A plain full-cube model plus a matching flat item icon. */
    public static final ModelKey<Void> CUBE_WITH_FLAT_ITEM = key("cube_with_flat_item");
    /** A vanilla cross-shaped (untinted) plant model plus a matching flat item icon. */
    public static final ModelKey<Void> CROSS_PLANT = key("cross_plant");
    /** A block whose blockstate/model and item model are both hand-authored static assets. */
    public static final ModelKey<Void> EXTERNAL_MODEL = key("external_model");
    /** Like {@link #EXTERNAL_MODEL}, but the item model is derived from the block's own texture. */
    public static final ModelKey<Void> EXTERNAL_MODEL_DELEGATED_ITEM = key("external_model_delegated_item");
    /** Like {@link #EXTERNAL_MODEL}, but the item model points at an explicit model location. */
    public static final ModelKey<Supplier<ResourceLocation>> EXTERNAL_MODEL_DELEGATED_ITEM_LOCATION = key("external_model_delegated_item_location");
    /** A hand-authored blockstate/model with a generated flat item model (from an optional texture). */
    public static final ModelKey<Supplier<ResourceLocation>> EXTERNAL_MODEL_FLAT_ITEM = key("external_model_flat_item");

    private BlockModelKeys() {
    }
}
