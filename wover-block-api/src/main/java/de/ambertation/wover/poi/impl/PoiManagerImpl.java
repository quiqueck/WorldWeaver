package de.ambertation.wover.poi.impl;

import de.ambertation.wover.events.api.WorldLifecycle;
import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.poi.api.PoiTypeExtension;
import de.ambertation.wover.poi.api.WoverPoiType;
import de.ambertation.wover.state.api.WorldState;
import de.ambertation.wover.tag.api.predefined.CommonPoiTags;

import net.minecraft.core.Holder;
import net.minecraft.core.LayeredRegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.ai.village.poi.PoiTypes;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.WorldData;

import java.util.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class PoiManagerImpl {
    public static WoverPoiType register(
            Identifier location,
            Set<BlockState> matchingStates,
            int maxTickets,
            int validRanges,
            @Nullable TagKey<Block> tag
    ) {
        ResourceKey<PoiType> key = ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, location);
        PoiType type = PoiTypes.register(
                BuiltInRegistries.POINT_OF_INTEREST_TYPE,
                key,
                matchingStates,
                maxTickets,
                validRanges
        );
        final var woverType = new WoverPoiType(key, type, matchingStates, maxTickets, validRanges);
        if (tag != null) woverType.setTag(tag);
        return woverType;
    }

    public static void setTag(ResourceKey<PoiType> type, TagKey<Block> tag) {
        var oHolder = BuiltInRegistries.POINT_OF_INTEREST_TYPE.get(type);
        if (oHolder.isPresent()) {
            setTag(oHolder.get().value(), tag);
            didAddTagFor(oHolder.get(), tag);
        }
    }

    private static void setTag(PoiType type, TagKey<Block> tag) {
        if ((Object) type instanceof PoiTypeExtension ext) {
            ext.wover_setTag(tag);
        }
    }

    @ApiStatus.Internal
    public static void registerAll() {
        PoiManagerImpl.setTag(PoiTypes.ARMORER, CommonPoiTags.ARMORER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.BUTCHER, CommonPoiTags.BUTCHER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.CARTOGRAPHER, CommonPoiTags.CARTOGRAPHER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.CLERIC, CommonPoiTags.CLERIC_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.FARMER, CommonPoiTags.FARMER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.FISHERMAN, CommonPoiTags.FISHERMAN_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.FLETCHER, CommonPoiTags.FLETCHER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.LEATHERWORKER, CommonPoiTags.LEATHERWORKER_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.LIBRARIAN, CommonPoiTags.LIBRARIAN_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.MASON, CommonPoiTags.MASON_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.SHEPHERD, CommonPoiTags.SHEPHERD_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.TOOLSMITH, CommonPoiTags.TOOLSMITH_WORKSTATION);
        PoiManagerImpl.setTag(PoiTypes.WEAPONSMITH, CommonPoiTags.WEAPONSMITH_WORKSTATION);

        PoiManagerImpl.setTag(PoiTypes.HOME, CommonPoiTags.HOME);
        PoiManagerImpl.setTag(PoiTypes.MEETING, CommonPoiTags.MEETING_PLACE);
        PoiManagerImpl.setTag(PoiTypes.BEEHIVE, CommonPoiTags.BEEHIVE);
        PoiManagerImpl.setTag(PoiTypes.BEE_NEST, CommonPoiTags.BEE_NEST);
        PoiManagerImpl.setTag(PoiTypes.NETHER_PORTAL, CommonPoiTags.NETHER_PORTAL);
        PoiManagerImpl.setTag(PoiTypes.LODESTONE, CommonPoiTags.LODESTONE);
        PoiManagerImpl.setTag(PoiTypes.LIGHTNING_ROD, CommonPoiTags.LIGHTNING_ROD);

        WorldLifecycle.BEFORE_CREATING_LEVELS.subscribe(
                PoiManagerImpl::finalizedWorldLoad,
                EventImpl.SYSTEM_PRIORITY - 1
        );
    }

    private static void finalizedWorldLoad(
            LevelStorageSource.LevelStorageAccess levelStorageAccess,
            PackRepository packRepository,
            LayeredRegistryAccess<RegistryLayer> registryLayerLayeredRegistryAccess,
            WorldData worldData
    ) {
        PoiManagerImpl.updateStates();
    }


    private static final List<Holder<PoiType>> TYPES_WITH_TAGS = new ArrayList<>(20);
    private static Map<BlockState, Holder<PoiType>> ORIGINAL_BLOCK_STATES = null;

    private static void didAddTagFor(Holder<PoiType> type, TagKey<Block> tag) {
        TYPES_WITH_TAGS.remove(type);
        if (tag != null) TYPES_WITH_TAGS.add(type);
    }


    @ApiStatus.Internal
    public static void updateStates() {
        if (ORIGINAL_BLOCK_STATES == null) {
            //We have not yet tainted the original states, so we will create a copy now
            ORIGINAL_BLOCK_STATES = new HashMap<>(PoiTypes.TYPE_BY_STATE);
        } else {
            //restore unaltered state
            PoiTypes.TYPE_BY_STATE.clear();
            PoiTypes.TYPE_BY_STATE.putAll(ORIGINAL_BLOCK_STATES);
        }

        for (Holder<PoiType> type : TYPES_WITH_TAGS) {
            if ((Object) type.value() instanceof PoiTypeExtension ex) {
                TagKey<Block> tag = ex.wover_getTag();
                if (tag != null) {
                    var registry = WorldState.registryAccess().lookupOrThrow(tag.registry());
                    for (var block : registry.getTagOrEmpty(tag)) {
                        for (var state : block.value().getStateDefinition().getPossibleStates()) {
                            if (state != null
                                    && state.hasProperty(BedBlock.PART)
                                    && state.getValue(BedBlock.PART) != BedPart.HEAD) {
                                continue;
                            }
                            PoiTypes.TYPE_BY_STATE.put(state, type);
                        }
                    }
                }
            }
        }
    }
}
