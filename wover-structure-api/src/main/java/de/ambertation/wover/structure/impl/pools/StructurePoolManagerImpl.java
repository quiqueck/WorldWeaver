package de.ambertation.wover.structure.impl.pools;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructurePoolManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<StructureTemplatePool>> BOOTSTRAP_TEMPLATE_POOLS =
            new EventImpl<>("BOOTSTRAP_TEMPLATE_POOLS");

    @Nullable
    public static Holder<StructureTemplatePool> getHolder(
            @Nullable HolderGetter<StructureTemplatePool> getter,
            @NotNull ResourceKey<StructureTemplatePool> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<StructureTemplatePool>> h = getter.get(key);
        return h.orElse(null);
    }

    static private boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.addBootstrap(
                Registries.TEMPLATE_POOL,
                StructurePoolManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstrapContext<StructureTemplatePool> context) {
        BOOTSTRAP_TEMPLATE_POOLS.emit(c -> c.bootstrap(context));
    }
}
