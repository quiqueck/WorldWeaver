package de.ambertation.wover.structure.impl.sets;

import de.ambertation.wover.core.api.registry.DatapackRegistryBuilder;
import de.ambertation.wover.events.api.types.OnBootstrapRegistry;
import de.ambertation.wover.events.impl.EventImpl;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.structure.StructureSet;

import java.util.Optional;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class StructureSetManagerImpl {
    public static final EventImpl<OnBootstrapRegistry<StructureSet>> BOOTSTRAP_STRUCTURE_SETS =
            new EventImpl<>("BOOTSTRAP_STRUCTURE_SETS");

    @Nullable
    public static Holder<StructureSet> getHolder(
            @Nullable HolderGetter<StructureSet> getter,
            @NotNull ResourceKey<StructureSet> key
    ) {
        if (getter == null) return null;

        final Optional<Holder.Reference<StructureSet>> h = getter.get(key);
        return h.orElse(null);
    }

    private static boolean didInit = false;

    @ApiStatus.Internal
    public static void initialize() {
        if (didInit) return;
        didInit = true;

        DatapackRegistryBuilder.addBootstrap(
                Registries.STRUCTURE_SET,
                StructureSetManagerImpl::onBootstrap
        );
    }

    private static void onBootstrap(BootstrapContext<StructureSet> context) {
        BOOTSTRAP_STRUCTURE_SETS.emit(c -> c.bootstrap(context));
    }
}
