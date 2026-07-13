package org.betterx.wover.structure.api.processors;

import org.betterx.wover.structure.api.pools.StructurePoolBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import org.jetbrains.annotations.NotNull;

/**
 * A builder for {@link StructureProcessorList}s. Created by calling
 * {@link StructureProcessorKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}.
 * <p>
 * Use {@link #add(StructureProcessor)} to add a pre-built {@link StructureProcessor}, or
 * {@link #startRule()} to build a {@link net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor}
 * (a list of block-swap {@link net.minecraft.world.level.levelgen.structure.templatesystem.ProcessorRule}s)
 * inline.
 */
public interface StructureProcessorBuilder {
    /**
     * Adds a pre-built {@link StructureProcessor} to the list.
     *
     * @param processor The processor to add
     * @return This builder instance, for chaining
     */
    @NotNull
    StructureProcessorBuilder add(@NotNull StructureProcessor processor);

    /**
     * Starts building a {@link net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor}
     * consisting of one or more {@link ProcessorRule}s. Call {@link RuleProcessorBuilder#endRule()} to add
     * the finished rule processor to this builder.
     *
     * @return A builder for the new rule processor
     */
    @NotNull
    RuleProcessorBuilder startRule();

    /**
     * Registers the {@link StructureTemplatePool} with the currently active
     * {@link net.minecraft.data.worldgen.BootstrapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstrapContext}
     * are null.
     *
     * @return the holder
     */
    @NotNull
    Holder<StructureProcessorList> register();

    /**
     * Creates an unnamed {@link Holder} for this {@link StructurePoolBuilder}.
     * <p>
     * This method is useful, if you want to create an anonymous {@link StructureTemplatePool}
     * that is directly inlined
     *
     * @return the holder
     */
    @NotNull
    Holder<StructureProcessorList> directHolder();

    /**
     * A builder for a {@link net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor},
     * i.e. a list of {@link ProcessorRule}s. Created via {@link StructureProcessorBuilder#startRule()}.
     */
    interface RuleProcessorBuilder {
        /**
         * Adds a pre-built {@link ProcessorRule} to this rule processor.
         *
         * @param rule The rule to add
         * @return This builder instance, for chaining
         */
        @NotNull
        RuleProcessorBuilder add(@NotNull ProcessorRule rule);

        /**
         * Starts building a {@link ProcessorRule} inline. Call {@link ProcessorRuleBuilder#endProcessor()}
         * to add the finished rule to this rule processor.
         *
         * @return A builder for the new rule
         */
        @NotNull
        ProcessorRuleBuilder startProcessor();

        /**
         * Finishes this rule processor and adds it to the owning {@link StructureProcessorBuilder}.
         *
         * @return The owning {@link StructureProcessorBuilder}, for chaining
         */
        @NotNull
        StructureProcessorBuilder endRule();

        /**
         * Starts building another {@link net.minecraft.world.level.levelgen.structure.templatesystem.RuleProcessor}
         * as a sibling of this one, on the owning {@link StructureProcessorBuilder}. Shorthand for
         * {@code endRule().startRule()}.
         *
         * @return A builder for the new rule processor
         */
        @NotNull
        RuleProcessorBuilder startRule();

        /**
         * A builder for a single {@link ProcessorRule}, consisting of an input predicate, an optional
         * location/position predicate, an optional {@link RuleBlockEntityModifier}, and an output
         * {@link BlockState}. Created via {@link RuleProcessorBuilder#startProcessor()}.
         */
        interface ProcessorRuleBuilder {
            /**
             * Sets the predicate tested against the block currently present at the target position.
             *
             * @param inputPredicate The predicate to test
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder inputPredicate(@NotNull RuleTest inputPredicate);

            /**
             * Sets the input predicate to match a specific {@link Block} (any block state of it).
             *
             * @param block The block to match
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder inputPredicate(@NotNull Block block);

            /**
             * Sets the input predicate to match a specific {@link BlockState}.
             *
             * @param state The block state to match
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder inputPredicate(@NotNull BlockState state);

            /**
             * Sets the input predicate to match a specific {@link Block} with the given probability.
             *
             * @param block  The block to match
             * @param chance The probability (0..1) of the rule matching
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder inputPredicateRandom(@NotNull Block block, float chance);

            /**
             * Sets the input predicate to match a specific {@link BlockState} with the given probability.
             *
             * @param block  The block state to match
             * @param chance The probability (0..1) of the rule matching
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder inputPredicateRandom(@NotNull BlockState block, float chance);

            /**
             * Sets the predicate tested against the block currently present at the position the input
             * block will be placed at in the world.
             *
             * @param locPredicate The predicate to test
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationPredicate(@NotNull RuleTest locPredicate);

            /**
             * Sets the location predicate to match a specific {@link Block} with the given probability.
             *
             * @param block  The block to match
             * @param chance The probability (0..1) of the rule matching
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationPredicateRandom(@NotNull Block block, float chance);

            /**
             * Sets the location predicate to match a specific {@link Block} (any block state of it).
             *
             * @param block The block to match
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationPredicate(@NotNull Block block);

            /**
             * Sets the location predicate to match a specific {@link BlockState}.
             *
             * @param state The block state to match
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationPredicate(@NotNull BlockState state);

            /**
             * Sets the location predicate to always match, regardless of the block present at the
             * target location.
             *
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationAlways();

            /**
             * Sets the location predicate to match a specific {@link BlockState} with the given
             * probability.
             *
             * @param block  The block state to match
             * @param chance The probability (0..1) of the rule matching
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder locationPredicateRandom(@NotNull BlockState block, float chance);

            /**
             * Sets the predicate tested against the position of the block, relative to the structure.
             *
             * @param posPredicate The predicate to test
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder positionPredicate(@NotNull PosRuleTest posPredicate);

            /**
             * Sets a modifier applied to the block entity (if any) at the target position after the
             * rule matches.
             *
             * @param blockEntityModifier The block entity modifier to apply
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder blockEntityModifier(@NotNull RuleBlockEntityModifier blockEntityModifier);

            /**
             * Sets the {@link BlockState} the input block is replaced with when this rule matches.
             *
             * @param outputState The output block state
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder outputState(@NotNull BlockState outputState);

            /**
             * Sets the output block (using its default {@link BlockState}). See {@link #outputState(BlockState)}.
             *
             * @param outputBlock The output block
             * @return This builder instance, for chaining
             */
            @NotNull
            ProcessorRuleBuilder outputState(@NotNull Block outputBlock);

            /**
             * Finishes this rule and adds it to the owning {@link RuleProcessorBuilder}.
             *
             * @return The owning {@link RuleProcessorBuilder}, for chaining
             */
            @NotNull
            RuleProcessorBuilder endProcessor();

            /**
             * Starts building another {@link ProcessorRule} as a sibling of this one, on the owning
             * {@link RuleProcessorBuilder}. Shorthand for {@code endProcessor().startProcessor()}.
             *
             * @return A builder for the new rule
             */
            @NotNull
            ProcessorRuleBuilder startProcessor();
        }
    }
}
