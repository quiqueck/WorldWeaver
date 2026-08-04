package de.ambertation.wover.block.api.trait;

import de.ambertation.wover.block.api.BlockDefinition;
import de.ambertation.wover.block.api.BlockRegistry;
import de.ambertation.wover.block.api.model.BlockModelBinding;
import de.ambertation.wover.block.api.trait.behaviour.LootTableTrait;
import de.ambertation.wover.item.api.BlockItemDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;

import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockDefinition} that builds a standing (or hanging) sign together with its matching wall variant and
 * a single shared {@link SignItem}.
 * <p>
 * Vanilla signs always come in pairs (e.g. {@code StandingSignBlock}/{@code WallSignBlock}, or
 * {@code CeilingHangingSignBlock}/{@code WallHangingSignBlock}), but only need one {@code BlockItem} between them.
 * This class builds and registers the primary (standing/ceiling) block through the normal
 * {@link BlockDefinition} lifecycle, then builds and registers the wall variant as a nested
 * {@link WallSignBlockDefinition} that copies over every non-model, non-recipe, non-loot-table trait added to
 * this definition (see {@link #buildAndRegisterWallSignBlock(Block)}). Call {@link #buildAndRegisterSign()}
 * instead of the inherited {@code buildAndRegister()} to get both blocks and the shared item back at once. See
 * {@code de.ambertation.wover.sets.api.blocks.types.Sign}/{@code HangingSign} for real usage.
 */
public class SignBlockDefinition extends BlockDefinition<SignBlock, SignBlockDefinition> {
    /**
     * Creates the shared {@link SignItem} for a sign/wall-sign pair.
     */
    public interface SignItemFactory {
        /**
         * Creates the {@link SignItem} shared by the standing/hanging sign and its wall variant.
         *
         * @param signBlock     the primary (standing/ceiling) sign block
         * @param wallSignBlock the matching wall sign block
         * @param properties    the item properties to use
         * @return the new {@link SignItem}
         */
        SignItem create(SignBlock signBlock, SignBlock wallSignBlock, Item.Properties properties);
    }

    /**
     * The fully built and registered result of {@link #buildAndRegisterSign()}.
     *
     * @param signBlock     the primary (standing/ceiling) sign block
     * @param wallSignBlock the matching wall sign block
     * @param signItem      the shared {@link BlockItem}
     */
    public record SignType(
            SignBlock signBlock,
            SignBlock wallSignBlock,
            BlockItem signItem
    ) {
    }

    protected final BlockFactory<SignBlock, WallSignBlockDefinition> wallSignFactory;
    protected final SignItemFactory itemFactory;
    protected final String wallSignName;
    protected SignBlock wallSignBlock;

    /**
     * Creates a new sign definition.
     * <p>
     * No default {@link BlockItem} is created (see {@link BlockDefinition#noBlockItem()}); the shared
     * {@link SignItem} is created lazily from {@code itemFactory} once both blocks exist.
     *
     * @param registry       the registry this definition will register both blocks with
     * @param signName       the registration name of the standing/ceiling sign block
     * @param wallSignName   the registration name of the wall sign block
     * @param signFactory    creates the standing/ceiling sign block
     * @param wallSignFactory creates the wall sign block
     * @param itemFactory    creates the shared {@link SignItem} once both blocks are registered
     */
    public SignBlockDefinition(
            BlockRegistry registry,
            String signName,
            String wallSignName,
            BlockFactory<SignBlock, SignBlockDefinition> signFactory,
            BlockFactory<SignBlock, WallSignBlockDefinition> wallSignFactory,
            SignItemFactory itemFactory
    ) {
        super(registry, signName, signFactory);
        this.wallSignFactory = wallSignFactory;
        this.wallSignName = wallSignName;
        this.itemFactory = itemFactory;
        this.noBlockItem();
    }

    @Override
    protected void beforeBuild() {

    }

    @Override
    protected SignBlock beforeRegister(SignBlock block) {
        return block;
    }


    @Override
    protected SignBlock afterRegister(SignBlock signBlock) {
        this.wallSignBlock = buildAndRegisterWallSignBlock(signBlock);

        return super.afterRegister(signBlock);
    }

    @Override
    protected @Nullable BlockItemDefinition<?, ?> getBlockItemDefinition(SignBlock signBlock) {
        return new BlockItemDefinition<>(
                this, definition -> itemFactory.create(
                signBlock,
                wallSignBlock,
                definition.getProperties()
        )
        )
                .stacksTo(16)
                .addTags(this.itemTags);
    }

    /**
     * Builds and registers both the primary sign block and its wall variant, plus their shared {@link BlockItem}.
     *
     * @return the built {@link SignType}
     */
    public SignType buildAndRegisterSign() {
        var signBlock = buildAndRegister();
        return new SignType(signBlock, wallSignBlock, blockItem);
    }

    /**
     * Builds and registers the wall variant of this sign, reusing this definition's traits (except
     * {@link de.ambertation.wover.block.api.client.trait.BlockModelTrait}, {@link BlockRecipeTrait} and
     * {@link LootTableTrait}, which the wall block instead inherits directly from the primary sign block).
     *
     * @param signBlock the already-registered primary (standing/ceiling) sign block
     * @return the built and registered wall sign block
     */
    protected SignBlock buildAndRegisterWallSignBlock(Block signBlock) {
        var wallSignDefinition = new WallSignBlockDefinition(
                registry,
                wallSignName, wallSignFactory, signBlock
        )
                .overrideLootTable(signBlock.getLootTable())
                .overrideDescription(signBlock.getDescriptionId())
                .noBlockItem();

        if (traits != null) {
            for (var trait : traits) {
                // Skip any model trait (data-driven BlockModelBinding or the client escape-hatch trait, which
                // share this key): the wall block generates its blockstate/model through the primary sign's
                // model factory, so copying it here would double-generate the wall blockstate.
                if (trait.is(BlockModelBinding.MODEL_TRAIT_KEY)) continue;
                if (trait instanceof BlockRecipeTrait) continue;
                if (trait instanceof LootTableTrait) continue;

                wallSignDefinition.addTrait(trait);
            }
        }

        return wallSignDefinition.buildAndRegister();
    }
}
