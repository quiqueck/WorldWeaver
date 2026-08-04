package de.ambertation.wover.tag.api.predefined;

import de.ambertation.wover.tag.api.TagManager;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import org.jetbrains.annotations.ApiStatus;

/**
 * Point of interest tags.
 * <p>
 * Point of interest tags are used to identify blocks that are used as workstations or other important locations
 * (like the position of a nether portal, or villager meeting place) in a world.
 * <p>
 * By Adding a block to a workstation tag, you can make it so that villagers will recognize that block as a
 * workstation for their profession.
 */
public class CommonPoiTags {
    /**
     * {@code wover:poi/workstation/armorer}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a armorer villager.
     */
    public static final TagKey<Block> ARMORER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/armorer");

    /**
     * {@code wover:poi/workstation/butcher}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a butcher villager.
     */
    public static final TagKey<Block> BUTCHER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/butcher");

    /**
     * {@code wover:poi/workstation/cartographer}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a cartographer villager.
     */
    public static final TagKey<Block> CARTOGRAPHER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/cartographer");

    /**
     * {@code wover:poi/workstation/cleric}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a cleric villager.
     */
    public static final TagKey<Block> CLERIC_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/cleric");

    /**
     * {@code wover:poi/workstation/farmer}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a farmer villager.
     */
    public static final TagKey<Block> FARMER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/farmer");

    /**
     * {@code wover:poi/workstation/fisherman}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a fisherman villager.
     */
    public static final TagKey<Block> FISHERMAN_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/fisherman");

    /**
     * {@code wover:poi/workstation/fletcher}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a fletcher villager.
     */
    public static final TagKey<Block> FLETCHER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/fletcher");

    /**
     * {@code wover:poi/workstation/leatherworker}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a leatherworker villager.
     */
    public static final TagKey<Block> LEATHERWORKER_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/leatherworker");

    /**
     * {@code wover:poi/workstation/librarian}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a librarian villager.
     */
    public static final TagKey<Block> LIBRARIAN_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/librarian");

    /**
     * {@code wover:poi/workstation/mason}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a mason villager.
     */
    public static final TagKey<Block> MASON_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/mason");

    /**
     * {@code wover:poi/workstation/shepherd}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a shepherd villager.
     */
    public static final TagKey<Block> SHEPHERD_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/shepherd");

    /**
     * {@code wover:poi/workstation/toolsmith}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a toolsmith villager.
     */
    public static final TagKey<Block> TOOLSMITH_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/toolsmith");

    /**
     * {@code wover:poi/workstation/weaponsmith}
     * <p>
     * This tag is used to identify blocks that can be used as a workstation for a weaponsmith villager.
     */
    public static final TagKey<Block> WEAPONSMITH_WORKSTATION = TagManager.BLOCKS.makeWorldWeaverTag("poi/workstation/weaponsmith");

    /**
     * {@code wover:poi/home}
     * <p>
     * This tag is used to identify blocks that are homes.
     */
    public static final TagKey<Block> HOME = TagManager.BLOCKS.makeWorldWeaverTag("poi/home");

    /**
     * {@code wover:poi/meeting}
     * <p>
     * This tag is used to identify blocks that are meeting places.
     */
    public static final TagKey<Block> MEETING_PLACE = TagManager.BLOCKS.makeWorldWeaverTag("poi/meeting");

    /**
     * {@code wover:poi/beehive}
     * <p>
     * This tag is used to identify blocks that are beehive.
     */
    public static final TagKey<Block> BEEHIVE = TagManager.BLOCKS.makeWorldWeaverTag("poi/beehive");

    /**
     * {@code wover:poi/bee_nest}
     * <p>
     * This tag is used to identify blocks that are bee nests.
     */
    public static final TagKey<Block> BEE_NEST = TagManager.BLOCKS.makeWorldWeaverTag("poi/bee_nest");

    /**
     * {@code wover:poi/nether_portal}
     * <p>
     * This tag is used to identify blocks that are nether portals.
     */
    public static final TagKey<Block> NETHER_PORTAL = TagManager.BLOCKS.makeWorldWeaverTag("poi/nether_portal");

    /**
     * {@code wover:poi/lodestone}
     * <p>
     * This tag is used to identify blocks that are lodestones.
     */
    public static final TagKey<Block> LODESTONE = TagManager.BLOCKS.makeWorldWeaverTag("poi/lodestone");

    /**
     * {@code wover:poi/lightning_rod}
     * <p>
     * This tag is used to identify blocks that are lightning rods.
     */
    public static final TagKey<Block> LIGHTNING_ROD = TagManager.BLOCKS.makeWorldWeaverTag("poi/lightning_rod");

    /**
     * Called internally to ensure that the tags are created.
     */
    @ApiStatus.Internal
    public static void ensureStaticallyLoaded() {
        // NO-OP
    }

    private CommonPoiTags() {
    }
}
