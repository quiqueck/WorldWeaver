package org.betterx.wover.structure.api.pools;

import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import org.jetbrains.annotations.NotNull;

public class StructurePoolBuilder {
    @NotNull
    private final ResourceKey<StructureTemplatePool> key;
    @NotNull
    private final BootstapContext<StructureTemplatePool> context;

    StructurePoolBuilder(
            @NotNull ResourceKey<StructureTemplatePool> key,
            @NotNull BootstapContext<StructureTemplatePool> context
    ) {
        this.key = key;
        this.context = context;
    }

    /**
     * Registers the {@link StructureTemplatePool} with the currently active
     * {@link net.minecraft.data.worldgen.BootstapContext}.
     * <p>
     * Will fail if either the key of this Feature or the {@link net.minecraft.data.worldgen.BootstapContext}
     * are null.
     *
     * @return the holder
     */
    public Holder<StructureTemplatePool> register() {
        return context.register(key, build());
    }

    /**
     * Creates an unnamed {@link Holder} for this {@link StructurePoolBuilder}.
     * <p>
     * This method is usefull, if you want to create an anonymous {@link StructureTemplatePool}
     * that is directly inlined
     *
     * @return the holder
     */
    public Holder<StructureTemplatePool> directHolder() {
        return Holder.direct(build());
    }

    private StructureTemplatePool build() {
        return null;
    }
}
