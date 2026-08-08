package de.ambertation.wover.item.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

/**
 * Specialized configuration class for creating tool items with tool-specific properties.
 * This class extends {@link ItemDefinition} to provide additional methods for configuring
 * tool materials, attack damage, attack speed, and tool-specific behaviors.
 *
 * <p>Tool items in Minecraft have specialized functionality including:</p>
 * <ul>
 *   <li>Specific tool materials that determine durability and mining speed</li>
 *   <li>Attack damage and speed values for combat</li>
 *   <li>Block breaking effectiveness based on tool type</li>
 *   <li>Enchantability and repair material compatibility</li>
 * </ul>
 *
 * <p>This class provides convenience methods for creating common tool types like
 * pickaxes, axes, hoes, shovels, and swords, as well as a generic tool method
 * for custom tool behaviors.</p>
 *
 * @param <I> The type of tool item being created, must extend {@link Item}
 * @author Quiqueck
 * @since 21.6.0
 */
public class ToolItemDefinition<I extends Item> extends ItemDefinition<I, ToolItemDefinition<I>> {
    /**
     * Factory interface for creating tool items from configuration objects.
     * Extends the base ItemFactory to work specifically with ToolItemDefinition.
     *
     * @param <I> The type of tool item to create
     */
    public interface ItemFactory<I extends Item> extends ItemDefinition.ItemFactory<I, ToolItemDefinition<I>> {
    }

    /**
     * Creates a new tool item configuration.
     *
     * @param registry    The item registry to use for registration
     * @param toolName    The name identifier for the tool item
     * @param itemFactory The factory used to create the tool item instance
     */
    protected ToolItemDefinition(
            ItemRegistry registry,
            String toolName,
            ItemFactory<I> itemFactory
    ) {
        super(registry, toolName, itemFactory);
    }

    /**
     * Called before the tool item is built to allow for any final configuration.
     * Currently empty but can be overridden by subclasses for custom setup logic.
     */
    @Override
    protected void beforeBuild() {

    }

    /**
     * Called before the tool item is registered to allow for any final modifications.
     * This default implementation returns the item unchanged, but subclasses can override
     * this method to perform custom post-creation setup before registration.
     *
     * @param item The built tool item instance
     * @return The tool item instance (potentially modified) that should be registered
     */
    @Override
    protected I beforeRegister(I item) {
        return item;
    }

    /**
     * Configures this item as a custom tool with specific material and block effectiveness.
     * This is the most flexible tool configuration method, allowing you to specify exactly
     * which blocks this tool can effectively break.
     *
     * @param material            The tool material defining durability, speed, and enchantability
     * @param effectiveBlocks     Tag containing blocks this tool can effectively break
     * @param baseDamage          The base attack damage when used as a weapon
     * @param attackSpeed         The attack speed modifier (4.0 is baseline, higher is faster)
     * @param blockingDisableTime Time in seconds that blocking is disabled after attacking
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> tool(
            ToolMaterial material,
            TagKey<Block> effectiveBlocks,
            float baseDamage,
            float attackSpeed,
            float blockingDisableTime
    ) {
        propertySetters.add((properties) -> properties.tool(
                material,
                effectiveBlocks,
                baseDamage,
                attackSpeed,
                blockingDisableTime
        ));
        return this;
    }

    /**
     * Configures this item as a pickaxe with the specified material and combat stats.
     * Pickaxes are effective against stone, ores, and other hard materials.
     *
     * @param material    The tool material defining durability, mining speed, and enchantability
     * @param baseDamage  The base attack damage when used as a weapon
     * @param attackSpeed The attack speed modifier (4.0 is baseline, higher is faster)
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> pickaxe(ToolMaterial material, float baseDamage, float attackSpeed) {
        propertySetters.add((properties) -> properties.pickaxe(material, baseDamage, attackSpeed));
        return this;
    }

    /**
     * Configures this item as an axe with the specified material and combat stats.
     * Axes are effective against wood-based blocks and have special interactions with logs.
     *
     * @param material    The tool material defining durability, chopping speed, and enchantability
     * @param baseDamage  The base attack damage when used as a weapon
     * @param attackSpeed The attack speed modifier (4.0 is baseline, higher is faster)
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> axe(ToolMaterial material, float baseDamage, float attackSpeed) {
        propertySetters.add((properties) -> properties.axe(material, baseDamage, attackSpeed));
        return this;
    }

    /**
     * Configures this item as a hoe with the specified material and combat stats.
     * Hoes are used for tilling dirt and farmland, and have special crop-related interactions.
     *
     * @param material    The tool material defining durability, tilling effectiveness, and enchantability
     * @param baseDamage  The base attack damage when used as a weapon
     * @param attackSpeed The attack speed modifier (4.0 is baseline, higher is faster)
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> hoe(ToolMaterial material, float baseDamage, float attackSpeed) {
        propertySetters.add((properties) -> properties.hoe(material, baseDamage, attackSpeed));
        return this;
    }

    /**
     * Configures this item as a shovel with the specified material and combat stats.
     * Shovels are effective against soft materials like dirt, sand, gravel, and snow.
     *
     * @param material    The tool material defining durability, digging speed, and enchantability
     * @param baseDamage  The base attack damage when used as a weapon
     * @param attackSpeed The attack speed modifier (4.0 is baseline, higher is faster)
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> shovel(ToolMaterial material, float baseDamage, float attackSpeed) {
        propertySetters.add((properties) -> properties.shovel(material, baseDamage, attackSpeed));
        return this;
    }

    /**
     * Configures this item as a sword with the specified material and combat stats.
     * Swords are primarily weapons with high attack damage and can cut through certain blocks.
     *
     * @param material    The tool material defining durability, attack effectiveness, and enchantability
     * @param baseDamage  The base attack damage when used as a weapon
     * @param attackSpeed The attack speed modifier (4.0 is baseline, higher is faster)
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> sword(ToolMaterial material, float baseDamage, float attackSpeed) {
        propertySetters.add((properties) -> properties.sword(material, baseDamage, attackSpeed));
        return this;
    }

    /**
     * Configures this item as a spear with the specified material and thrust-attack tuning.
     * Spears are charge-and-thrust weapons: their attack damage attribute comes solely from
     * {@code material}'s attack damage bonus, while {@code damageMultiplier} instead scales the
     * damage of a fully charged thrust. See {@link net.minecraft.world.item.Item.Properties#spear}
     * for the exact semantics of each tuning parameter.
     *
     * @param material           The tool material defining durability, enchantability, and repair compatibility
     * @param attackDuration     Seconds of swing animation; also drives the attack speed attribute (1/duration)
     * @param damageMultiplier   Damage multiplier applied to a fully charged thrust
     * @param delay              Seconds before the charge begins accumulating after starting to use the item
     * @param dismountTime       Seconds of attacker speed required to dismount the target
     * @param dismountThreshold  Attacker speed (blocks/tick) required to dismount the target
     * @param knockbackTime      Seconds of attacker speed required for the bonus knockback effect
     * @param knockbackThreshold Attacker speed (blocks/tick) required for the bonus knockback effect
     * @param damageTime         Seconds of relative speed required for the full damage multiplier
     * @param damageThreshold    Relative speed (blocks/tick) required for the full damage multiplier
     * @return This configuration instance for method chaining
     */
    public ToolItemDefinition<I> spear(
            ToolMaterial material,
            float attackDuration,
            float damageMultiplier,
            float delay,
            float dismountTime,
            float dismountThreshold,
            float knockbackTime,
            float knockbackThreshold,
            float damageTime,
            float damageThreshold
    ) {
        propertySetters.add((properties) -> properties.spear(
                material,
                attackDuration,
                damageMultiplier,
                delay,
                dismountTime,
                dismountThreshold,
                knockbackTime,
                knockbackThreshold,
                damageTime,
                damageThreshold
        ));
        return this;
    }
}
