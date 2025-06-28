package org.betterx.wover.recipe.impl;

import org.betterx.wover.events.impl.EventImpl;
import org.betterx.wover.recipe.api.OnBootstrapRecipes;
import org.betterx.wover.state.api.WorldState;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.RecipeType;

import com.google.common.collect.Multimap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeRuntimeProviderImpl {
    public static final EventImpl<OnBootstrapRecipes> BOOTSTRAP_RECIPES =
            new EventImpl<>("BOOTSTRAP_RECIPES");

    public record LoadedRecipes(Multimap<RecipeType<?>, RecipeHolder<?>> byType,
                                Map<ResourceKey<Recipe<?>>, RecipeHolder<?>> byName) {
    }


    @ApiStatus.Internal
    public static RecipeMap loadedRecipes(
            RecipeMap loaded
    ) {
        final boolean[] didInit = {false};
        List<RecipeHolder<?>> recipeHolders = new LinkedList<>();

        RecipeOutput context = new RecipeOutput() {
            @Override
            public void accept(
                    ResourceKey<Recipe<?>> resourceKey,
                    Recipe<?> recipe,
                    @Nullable AdvancementHolder advancementHolder
            ) {
                if (!didInit[0]) {
                    recipeHolders.addAll(loaded.values());
                    didInit[0] = true;
                }
                RecipeHolder<?> recipeHolder = new RecipeHolder<>(resourceKey, recipe);
                recipeHolders.add(recipeHolder);
            }

            @Override
            @SuppressWarnings("removal")
            public Advancement.@NotNull Builder advancement() {
                return Advancement.Builder
                        .recipeAdvancement()
                        .parent(net.minecraft.data.recipes.RecipeBuilder.ROOT_RECIPE_ADVANCEMENT);
            }

            @Override
            public void includeRootAdvancement() {
                //this is created by vanilla code, so we don't need to do anything here
            }

        };

        var items = WorldState.registryAccess().lookupOrThrow(Registries.ITEM);
        var provider = new RecipeProvider(WorldState.registryAccess(), context) {
            @Override
            public void buildRecipes() {

            }
        };
        BOOTSTRAP_RECIPES.emit(c -> c.bootstrap(items, provider, context));

        if (!didInit[0]) return loaded;
        return RecipeMap.create(recipeHolders);
    }
}
