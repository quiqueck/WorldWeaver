package org.betterx.wover.structure.api.processors;

import org.betterx.wover.structure.api.pools.StructurePoolBuilder;

import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import org.jetbrains.annotations.NotNull;

public interface StructureProcessorBuilder {
    @NotNull StructureProcessorBuilder add(@NotNull StructureProcessor processor);

    @NotNull RuleProcessorBuilder startRule();

    /**
     * Registers the {@link StructureTemplatePool} with the currently active
     * {@link net.minecraft.data.worldgen.BootstapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstapContext}
     * are null.
     *
     * @return the holder
     */
    @NotNull Holder<StructureProcessorList> register();

    /**
     * Creates an unnamed {@link Holder} for this {@link StructurePoolBuilder}.
     * <p>
     * This method is useful, if you want to create an anonymous {@link StructureTemplatePool}
     * that is directly inlined
     *
     * @return the holder
     */
    @NotNull Holder<StructureProcessorList> directHolder();

    interface RuleProcessorBuilder {
        @NotNull RuleProcessorBuilder add(@NotNull ProcessorRule rule);
        @NotNull ProcessorRuleBuilder startProcessor();
        @NotNull StructureProcessorBuilder endRule();
        @NotNull RuleProcessorBuilder startRule();

        interface ProcessorRuleBuilder {
            @NotNull ProcessorRuleBuilder inputPredicate(@NotNull RuleTest inputPredicate);
            @NotNull ProcessorRuleBuilder inputPredicate(@NotNull Block block);
            @NotNull ProcessorRuleBuilder inputPredicate(@NotNull BlockState state);
            @NotNull ProcessorRuleBuilder inputPredicateRandom(@NotNull Block block, float chance);

            @NotNull ProcessorRuleBuilder inputPredicateRandom(@NotNull BlockState block, float chance);

            @NotNull ProcessorRuleBuilder locationPredicate(@NotNull RuleTest locPredicate);
            @NotNull ProcessorRuleBuilder locationPredicateRandom(@NotNull Block block, float chance);
            @NotNull ProcessorRuleBuilder locationPredicate(@NotNull Block block);
            @NotNull ProcessorRuleBuilder locationPredicate(@NotNull BlockState state);

            @NotNull ProcessorRuleBuilder locationPredicateRandom(@NotNull BlockState block, float chance);

            @NotNull ProcessorRuleBuilder positionPredicate(@NotNull PosRuleTest posPredicate);

            @NotNull ProcessorRuleBuilder blockEntityModifier(@NotNull RuleBlockEntityModifier blockEntityModifier);

            @NotNull ProcessorRuleBuilder outputState(@NotNull BlockState outputState);

            @NotNull ProcessorRuleBuilder outputState(@NotNull Block outputBlock);

            @NotNull RuleProcessorBuilder endProcessor();
            @NotNull ProcessorRuleBuilder startProcessor();
        }
    }
}
