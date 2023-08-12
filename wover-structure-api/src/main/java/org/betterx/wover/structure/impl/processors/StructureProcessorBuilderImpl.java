package org.betterx.wover.structure.impl.processors;

import org.betterx.wover.structure.api.processors.StructureProcessorBuilder;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifier;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public class StructureProcessorBuilderImpl implements StructureProcessorBuilder {
    @NotNull
    private final ResourceKey<StructureProcessorList> key;
    @NotNull
    private final BootstapContext<StructureProcessorList> context;

    private final List<StructureProcessor> list = new LinkedList<>();

    public StructureProcessorBuilderImpl(
            @NotNull ResourceKey<StructureProcessorList> key,
            @NotNull BootstapContext<StructureProcessorList> context
    ) {
        this.key = key;
        this.context = context;
    }

    @Override
    public StructureProcessorBuilder add(@NotNull StructureProcessor processor) {
        list.add(processor);
        return this;
    }

    @Override
    public RuleProcessorBuilder startRule() {
        return new RuleProcessorBuilderImpl();
    }

    @Override
    public Holder<StructureProcessorList> register() {
        return context.register(key, build());
    }

    @Override
    public Holder<StructureProcessorList> directHolder() {
        return Holder.direct(build());
    }


    private StructureProcessorList build() {
        return new StructureProcessorList(list);
    }

    public class RuleProcessorBuilderImpl implements RuleProcessorBuilder {
        private RuleProcessorBuilderImpl() {

        }

        private List<ProcessorRule> rules = new LinkedList<>();

        @Override
        public @NotNull RuleProcessorBuilder add(@NotNull ProcessorRule rule) {
            rules.add(rule);
            return this;
        }

        @Override
        public @NotNull ProcessorRuleBuilder startProcessor() {
            return new ProcessorRuleBuilderImpl();
        }

        public @NotNull StructureProcessorBuilder endRule() {
            return StructureProcessorBuilderImpl.this.add(new RuleProcessor(rules));
        }

        public @NotNull RuleProcessorBuilder startRule() {
            return endRule().startRule();
        }

        public class ProcessorRuleBuilderImpl implements ProcessorRuleBuilder {
            @NotNull
            private RuleTest inputPredicate;
            @NotNull
            private RuleTest locPredicate;
            @NotNull
            private PosRuleTest posPredicate;
            private BlockState outputState;
            @NotNull
            private RuleBlockEntityModifier blockEntityModifier;

            private ProcessorRuleBuilderImpl() {
                inputPredicate = AlwaysTrueTest.INSTANCE;
                locPredicate = AlwaysTrueTest.INSTANCE;
                posPredicate = PosAlwaysTrueTest.INSTANCE;
                blockEntityModifier = ProcessorRule.DEFAULT_BLOCK_ENTITY_MODIFIER;
            }

            @Override
            public @NotNull ProcessorRuleBuilder inputPredicate(@NotNull RuleTest inputPredicate) {
                this.inputPredicate = inputPredicate;
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder inputPredicate(@NotNull Block block) {
                this.inputPredicate = new BlockMatchTest(block);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder inputPredicate(@NotNull BlockState block) {
                this.inputPredicate = new BlockStateMatchTest(block);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder inputPredicateRandom(@NotNull Block block, float chance) {
                this.inputPredicate = new RandomBlockMatchTest(block, chance);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder inputPredicateRandom(@NotNull BlockState block, float chance) {
                this.inputPredicate = new RandomBlockStateMatchTest(block, chance);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder locationPredicate(@NotNull RuleTest locPredicate) {
                this.locPredicate = locPredicate;
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder locationPredicateRandom(@NotNull Block block, float chance) {
                this.locPredicate = new RandomBlockMatchTest(block, chance);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder locationPredicateRandom(@NotNull BlockState block, float chance) {
                this.locPredicate = new RandomBlockStateMatchTest(block, chance);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder locationPredicate(@NotNull Block block) {
                this.locPredicate = new BlockMatchTest(block);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder locationPredicate(@NotNull BlockState block) {
                this.locPredicate = new BlockStateMatchTest(block);
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder positionPredicate(@NotNull PosRuleTest posPredicate) {
                this.posPredicate = posPredicate;
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder blockEntityModifier(@NotNull RuleBlockEntityModifier blockEntityModifier) {
                this.blockEntityModifier = blockEntityModifier;
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder outputState(@NotNull BlockState outputState) {
                this.outputState = outputState;
                return this;
            }

            @Override
            public @NotNull ProcessorRuleBuilder outputState(@NotNull Block outputBlock) {
                this.outputState = outputBlock.defaultBlockState();
                return this;
            }

            @Override
            public @NotNull RuleProcessorBuilder endProcessor() {
                if (outputState == null) {
                    throw new IllegalStateException("Output stet must be set");
                }

                return RuleProcessorBuilderImpl.this.add(
                        new ProcessorRule(inputPredicate, locPredicate, posPredicate, outputState, blockEntityModifier)
                );
            }

            @Override
            public @NotNull ProcessorRuleBuilder startProcessor() {
                return endProcessor().startProcessor();
            }
        }
    }
}
