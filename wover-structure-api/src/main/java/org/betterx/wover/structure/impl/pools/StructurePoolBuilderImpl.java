package org.betterx.wover.structure.impl.pools;

import org.betterx.wover.structure.api.pools.StructurePoolBuilder;
import org.betterx.wover.structure.api.pools.StructurePoolKey;
import org.betterx.wover.structure.api.processors.StructureProcessorKey;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.data.worldgen.ProcessorLists;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructurePoolBuilderImpl implements StructurePoolBuilder {
    @NotNull
    private final ResourceKey<StructureTemplatePool> key;
    @NotNull
    private final BootstapContext<StructureTemplatePool> context;

    @Nullable
    private Holder<StructureTemplatePool> terminator;
    @NotNull
    private StructureTemplatePool.Projection projection;

    private List<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements = new LinkedList<>();

    public StructurePoolBuilderImpl(
            @NotNull ResourceKey<StructureTemplatePool> key,
            @NotNull BootstapContext<StructureTemplatePool> context
    ) {
        this.key = key;
        this.context = context;

        this.projection = StructureTemplatePool.Projection.RIGID;
    }

    @Override
    @NotNull
    public Holder<StructureTemplatePool> register() {
        return context.register(key, build());
    }

    @Override
    @NotNull
    public Holder<StructureTemplatePool> directHolder() {
        return Holder.direct(build());
    }

    @Override
    @NotNull
    public StructurePoolBuilder projection(@NotNull StructureTemplatePool.Projection projection) {
        this.projection = projection;
        return this;
    }

    @Override
    @NotNull
    public ElementBuilder startSingle(@NotNull ResourceLocation nbtLocation) {
        return new SinglePoolElementImpl(nbtLocation);
    }

    @Override
    @NotNull
    public ElementBuilder startSingleEnd(@NotNull ResourceLocation nbtLocation) {
        return new SingleEndPoolElementImpl(nbtLocation);
    }

    @Override
    @NotNull
    public ElementBuilder startLegacySingle(@NotNull ResourceLocation nbtLocation) {
        return new LegacyPoolElementImpl(nbtLocation);
    }

    @Override
    @NotNull
    public StructurePoolBuilder add(
            @NotNull Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> element,
            int weight
    ) {
        return add(Pair.of(element, weight));
    }

    @NotNull
    private StructurePoolBuilder add(@NotNull Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer> element) {
        elements.add(element);
        return this;
    }

    @Override
    @NotNull
    public StructurePoolBuilder terminator(@NotNull Holder<StructureTemplatePool> terminator) {
        this.terminator = terminator;
        return this;
    }

    @Override
    @NotNull
    public StructurePoolBuilder terminator(@NotNull ResourceKey<StructureTemplatePool> terminator) {
        this.terminator = context.lookup(Registries.TEMPLATE_POOL).getOrThrow(terminator);
        return this;
    }

    @Override
    @NotNull
    public StructurePoolBuilder terminator(@NotNull StructurePoolKey terminator) {
        return terminator(terminator.key);
    }

    @Override
    @NotNull
    public StructurePoolBuilder emptyTerminator() {
        return terminator(Pools.EMPTY);
    }

    private StructureTemplatePool build() {
        if (terminator == null) emptyTerminator();

        return new StructureTemplatePool(
                terminator,
                ImmutableList.copyOf(elements),
                projection
        );
    }

    public class SinglePoolElementImpl implements ElementBuilder {
        protected final ResourceLocation nbtLocation;
        protected Holder<StructureProcessorList> processor;
        private int weight;

        SinglePoolElementImpl(ResourceLocation nbtLocation) {
            this.nbtLocation = nbtLocation;
            this.weight = 1;
        }

        @Override
        public @NotNull ElementBuilder processor(@NotNull Holder<StructureProcessorList> processor) {
            this.processor = processor;
            return this;
        }

        @Override
        public @NotNull ElementBuilder processor(@NotNull ResourceKey<StructureProcessorList> processor) {
            this.processor = context.lookup(Registries.PROCESSOR_LIST).getOrThrow(processor);
            return this;
        }

        @Override
        public @NotNull ElementBuilder processor(@NotNull StructureProcessorKey processor) {
            return processor(processor.key);
        }

        @Override
        public @NotNull ElementBuilder emptyProcessor() {
            return processor(ProcessorLists.EMPTY);
        }

        @Override
        public @NotNull ElementBuilder weight(int weight) {
            this.weight = weight;
            return this;
        }

        @Override
        public @NotNull StructurePoolBuilder endElement() {
            if (nbtLocation == null) {
                throw new IllegalStateException("Location for a pool entry must be set before pushing it to the pool.");
            }

            // If processor is null, it will be replaced with an empty processor.
            if (processor == null) {

                emptyProcessor();
            }

            return StructurePoolBuilderImpl.this.add(Pair.of(create(), weight));
        }

        protected Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> create() {
            return projection -> new SinglePoolElement(Either.left(nbtLocation), processor, projection);
        }
    }

    public class LegacyPoolElementImpl extends SinglePoolElementImpl {
        LegacyPoolElementImpl(ResourceLocation nbtLocation) {
            super(nbtLocation);
        }

        protected Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> create() {
            return projection -> new LegacySinglePoolElement(Either.left(nbtLocation), processor, projection);
        }
    }

    public class SingleEndPoolElementImpl extends SinglePoolElementImpl {
        SingleEndPoolElementImpl(ResourceLocation nbtLocation) {
            super(nbtLocation);
        }

        protected Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> create() {
            return projection -> new SingleEndPoolElement(Either.left(nbtLocation), processor, projection);
        }
    }
}
