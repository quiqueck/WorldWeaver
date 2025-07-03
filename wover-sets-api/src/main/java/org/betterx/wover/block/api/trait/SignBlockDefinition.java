package org.betterx.wover.block.api.trait;

import org.betterx.wover.block.api.BlockDefinition;
import org.betterx.wover.block.api.BlockRegistry;
import org.betterx.wover.block.api.client.trait.BlockModelTrait;
import org.betterx.wover.block.api.trait.behaviour.LootTableTrait;
import org.betterx.wover.item.api.BlockItemDefinition;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;

import org.jetbrains.annotations.Nullable;

public class SignBlockDefinition extends BlockDefinition<SignBlock, SignBlockDefinition> {
    public interface SignItemFactory {
        SignItem create(SignBlock signBlock, SignBlock wallSignBlock, Item.Properties properties);
    }

    public record SignType(
            SignBlock signBlock,
            SignBlock wallSignBlock,
            BlockItem signItem
    ) {
    }

    protected final BlockFactory<SignBlock, WallSignBlockDefinition> wallSignFactory;
    protected final SignItemFactory itemFactory;
    protected final String wallSignName;
    protected SignBlock wallSignBlock;

    public SignBlockDefinition(
            BlockRegistry registry,
            String signName,
            String wallSignName,
            BlockFactory<SignBlock, SignBlockDefinition> signFactory,
            BlockFactory<SignBlock, WallSignBlockDefinition> wallSignFactory,
            SignItemFactory itemFactory
    ) {
        super(registry, signName, signFactory);
        this.wallSignFactory = wallSignFactory;
        this.wallSignName = wallSignName;
        this.itemFactory = itemFactory;
        this.noBlockItem();
    }

    @Override
    protected void beforeBuild() {

    }

    @Override
    protected SignBlock beforeRegister(SignBlock block) {
        return block;
    }


    @Override
    protected SignBlock afterRegister(SignBlock signBlock) {
        this.wallSignBlock = buildAndRegisterWallSignBlock(signBlock);

        return super.afterRegister(signBlock);
    }

    @Override
    protected @Nullable BlockItemDefinition<?, ?> getBlockItemDefinition(SignBlock signBlock) {
        return new BlockItemDefinition<>(
                this, definition -> itemFactory.create(
                signBlock,
                wallSignBlock,
                definition.getProperties()
        )
        )
                .stacksTo(16)
                .addTags(this.itemTags);
    }

    public SignType buildAndRegisterSign() {
        var signBlock = buildAndRegister();
        return new SignType(signBlock, wallSignBlock, blockItem);
    }

    protected SignBlock buildAndRegisterWallSignBlock(Block signBlock) {
        var wallSignDefinition = new WallSignBlockDefinition(
                registry,
                wallSignName, wallSignFactory, signBlock
        )
                .overrideLootTable(signBlock.getLootTable())
                .overrideDescription(signBlock.getDescriptionId())
                .noBlockItem();

        if (traits != null) {
            for (var trait : traits) {
                if (trait instanceof BlockModelTrait) continue;
                if (trait instanceof BlockRecipeTrait) continue;
                if (trait instanceof LootTableTrait) continue;
                
                wallSignDefinition.addTrait(trait);
            }
        }

        return wallSignDefinition.buildAndRegister();
    }
}
