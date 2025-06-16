package org.betterx.wover.item.api;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

public class ToolItemConfig<I extends Item> extends ItemConfig<I, ToolItemConfig<I>> {
    public interface ItemFactory<I extends Item> extends ItemConfig.ItemFactory<I, ToolItemConfig<I>> {
    }

    protected ToolItemConfig(
            ItemRegistry registry,
            String name,
            ItemConfig.ItemFactory<I, ToolItemConfig<I>> itemFactory
    ) {
        super(registry, name, itemFactory);
    }

    @Override
    protected void beforeBuild() {

    }

    public ToolItemConfig<I> tool(
            ToolMaterial toolMaterial,
            TagKey<Block> tagKey,
            float attackDamage,
            float attackSpeed,
            float disableBlockingForSeconds
    ) {
        this.properties.tool(toolMaterial, tagKey, attackDamage, attackSpeed, disableBlockingForSeconds);
        return this;
    }

    public ToolItemConfig<I> pickaxe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
        this.properties.pickaxe(toolMaterial, attackDamage, attackSpeed);
        return this;
    }

    public ToolItemConfig<I> axe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
        this.properties.axe(toolMaterial, attackDamage, attackSpeed);
        return this;
    }

    public ToolItemConfig<I> hoe(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
        this.properties.hoe(toolMaterial, attackDamage, attackSpeed);
        return this;
    }

    public ToolItemConfig<I> shovel(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
        this.properties.shovel(toolMaterial, attackDamage, attackSpeed);
        return this;
    }

    public ToolItemConfig<I> sword(ToolMaterial toolMaterial, float attackDamage, float attackSpeed) {
        this.properties.sword(toolMaterial, attackDamage, attackSpeed);
        return this;
    }
}
