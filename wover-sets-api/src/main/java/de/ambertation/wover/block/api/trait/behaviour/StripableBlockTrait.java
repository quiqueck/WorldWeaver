package de.ambertation.wover.block.api.trait.behaviour;

import de.ambertation.wover.block.api.trait.BlockTrait;
import de.ambertation.wover.block.api.trait.BlockTraitBuilder;
import de.ambertation.wover.block.api.trait.RuntimeBlockTrait;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.CopyPropertiesProvider;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A {@link BlockTrait} that marks a block as strippable (e.g. logs or bark), so that an axe interaction turns it
 * into a matching stripped {@link BlockState}.
 * <h2>How this works in 26.3</h2>
 * Up to 26.1 stripping lived in {@code AxeItem#getStripped}, and this trait was consumed by a mixin into that
 * method. 26.3 deleted {@code AxeItem} - and every other per-tool {@code Item} subclass - and made stripping
 * data instead: an axe carries a {@code minecraft:block_transformer} component holding a list of
 * {@link net.minecraft.core.component.BlockTransformer.BlockTransformData} entries, each pairing a
 * {@link net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate} that selects the block being hit
 * with a {@link BlockStateProvider} that produces its replacement.
 * <p>
 * So this trait no longer describes a Java callback; it describes one such {@code BlockTransformData}. Every
 * block carrying it contributes an entry that {@code wover-sets} appends to the axe component (see
 * {@code StripableBlockTransformers}). Two consequences follow, and they are the reason this trait's API
 * changed rather than being kept source-compatible:
 * <ul>
 *     <li>The stripped state is described by a <em>serializable</em> {@link BlockStateProvider} rather than by
 *     an arbitrary {@code (BlockState) -> BlockState} lambda. The component is written into the item's default
 *     components and synchronised to clients, so an unregistered provider implementation could not survive the
 *     round trip. This is a real narrowing, but the trait's own use sites only ever needed
 *     "target block, keeping the shared properties", which {@link CopyPropertiesProvider} expresses exactly.</li>
 *     <li>The old {@code copyRotatedPillarBlockState(...)} helper is gone. It hand-copied
 *     {@link net.minecraft.world.level.block.RotatedPillarBlock#AXIS}; {@link CopyPropertiesProvider} copies
 *     every property the old and new block share, which is both a superset of that and exactly what vanilla's
 *     own strippables use. {@link Builder#with(Block)} applies it for you.</li>
 * </ul>
 */
public interface StripableBlockTrait extends BlockTrait<Block, StripableBlockTrait>, RuntimeBlockTrait<Block, StripableBlockTrait> {
    /**
     * Builds {@link StripableBlockTrait} instances.
     */
    interface Builder extends BlockTraitBuilder<Block, StripableBlockTrait> {
        /**
         * Creates a trait that strips to {@code strippedBlock}, carrying over every property the two blocks
         * share (notably {@link net.minecraft.world.level.block.RotatedPillarBlock#AXIS} for logs and bark).
         * This is the shape vanilla uses for all of its own strippables.
         *
         * @param strippedBlock the block to strip to, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable Block strippedBlock);

        /**
         * Creates a trait that always strips to a fixed {@link BlockState}, ignoring the original state's
         * properties.
         *
         * @param strippedBlock the state to strip to, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable BlockState strippedBlock);

        /**
         * Creates a trait that strips to whatever {@code strippedBlock} yields. Use
         * {@link CopyPropertiesProvider} to keep properties of the block being stripped, or any other
         * registered {@link BlockStateProvider} for more exotic results.
         *
         * @param strippedBlock produces the stripped state, or {@code null} to remove the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait with(@Nullable BlockStateProvider strippedBlock);

        /**
         * Deferred variant of {@link #with(BlockStateProvider)}, for the common case where the block being
         * stripped <em>to</em> is not registered yet when the trait is created (a block set registers its log
         * and its stripped log in one pass). The supplier is read once, while the axe's block-transformer
         * component is being assembled - long after every block exists.
         *
         * @param strippedBlock supplies the provider producing the stripped state, or {@code null} to remove
         *                      the trait
         * @return the new trait
         */
        @Nullable StripableBlockTrait withDeferred(@Nullable Supplier<BlockStateProvider> strippedBlock);
    }

    /**
     * Resolves the {@link BlockStateProvider} that produces this block's stripped state. Called once per
     * datapack reload, while the axe's {@code minecraft:block_transformer} component is assembled, so it is
     * safe for the trait to have been created before the target block existed.
     *
     * @return the provider producing the stripped state
     */
    @NotNull BlockStateProvider strippedBlock();
}
