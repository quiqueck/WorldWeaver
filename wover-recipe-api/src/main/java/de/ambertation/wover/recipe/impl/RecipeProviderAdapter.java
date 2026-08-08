package de.ambertation.wover.recipe.impl;

import com.mojang.serialization.Lifecycle;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BootstrapRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;

import java.util.function.Consumer;
import java.util.stream.Stream;
import org.jetbrains.annotations.ApiStatus;

/**
 * Builds the vanilla {@link RecipeProvider} that {@link de.ambertation.wover.recipe.api.RecipeBuilder.Context}
 * exposes, on top of a {@link RecipeOutput}.
 * <p>
 * <b>Why this exists at all.</b> 26.3 stopped constructing {@code RecipeProvider} from a {@code RecipeOutput}
 * and now builds it from the {@code BootstrapContext<Recipe<?>>} + {@code BootstrapContext<Advancement>} pair
 * of the two datapack registries a recipe writes into. It still derives a {@code RecipeOutput} of its own from
 * that pair (see {@code RecipeProvider$1}) and exposes it as its {@code protected output} field, because every
 * vanilla recipe builder's {@code save(...)} takes a {@code RecipeOutput} - so {@code RecipeOutput} is not a
 * legacy shape in 26.3, it is the sink half of the current one.
 * <p>
 * On the <b>datagen</b> path nothing here is used: {@link de.ambertation.wover.datagen.api.provider.WoverRecipeProvider}
 * gets the real contexts from Fabric and reuses the provider's own {@code output}. This class only serves the
 * <b>runtime</b> path ({@code RecipeBuilder#BOOTSTRAP_RECIPES}), where there is no datapack writer and the
 * output is a {@link RuntimeRecipeOutput}. Building a real {@code RecipeProvider} there is what makes
 * {@code Context#has(...)} / {@code Context#tag(...)} and vanilla's convenience recipe helpers usable outside
 * of datagen.
 */
@ApiStatus.Internal
public final class RecipeProviderAdapter {
    private RecipeProviderAdapter() {
    }

    /**
     * The advancement sink for a plain {@link RecipeOutput} that is not a {@link RuntimeRecipeOutput}.
     * <p>
     * This is a property of the vanilla interface, not a shortcut: {@code RecipeOutput} carries advancements
     * only as the third argument of {@code accept(key, recipe, advancement)}, i.e. always attached to a
     * recipe. An advancement registered <em>directly</em> on {@code RecipeProvider#advancementOutput} (which
     * only vanilla's own providers do, and only for advancements that belong to no recipe) therefore has
     * nowhere to go. Recipe unlock advancements never take this route - they arrive through
     * {@code accept(...)} and are kept.
     */
    public static final Consumer<AdvancementHolder> NO_ADVANCEMENT_CHANNEL = advancement -> {
    };

    /**
     * Builds a {@link RecipeProvider} that generates nothing on its own, but writes everything registered
     * through it into {@code output}.
     *
     * @param output          the output recipes registered through the provider are forwarded to
     * @param advancementSink where advancements registered on {@code RecipeProvider#advancementOutput} go;
     *                        use {@link #NO_ADVANCEMENT_CHANNEL} when the output has no advancement channel
     * @return a provider with an empty {@code buildRecipes()}
     */
    public static RecipeProvider providerFor(RecipeOutput output, Consumer<AdvancementHolder> advancementSink) {
        return new RecipeProvider(recipeContext(output), advancementContext(output, advancementSink)) {
            @Override
            public void buildRecipes() {
            }
        };
    }

    /**
     * Wraps a {@link RecipeOutput} as the {@code BootstrapContext<Recipe<?>>} a {@link RecipeProvider} needs.
     * Registration is forwarded to {@code accept(key, recipe, null)} - a recipe registered this way carries no
     * unlock advancement, exactly as in vanilla, where {@code BootstrapContext#register} has no advancement
     * parameter either. The returned holder comes from the output's own {@code minecraft:recipe} lookup.
     *
     * @param output the output to forward to
     * @return the bootstrap context
     */
    public static BootstrapContext<Recipe<?>> recipeContext(RecipeOutput output) {
        return new BootstrapContext<>() {
            @Override
            public Holder.Reference<Recipe<?>> register(ResourceKey<Recipe<?>> key, Recipe<?> recipe) {
                output.accept(key, recipe, null);
                return output.lookup(Registries.RECIPE).getOrThrow(key);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return output.lookup(key);
            }

            // New in 26.3 (BootstrapContextAccess). A pure forwarder: whatever the output can enumerate,
            // the provider built on top of it can enumerate too.
            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
                return output.listContextElements(key);
            }
        };
    }

    /**
     * The advancement half: registrations are handed to {@code advancementSink} and answered with a
     * stand-alone {@link Holder.Reference} from a scratch {@link BootstrapRegistry}, because the world's
     * {@code minecraft:advancement} registry is frozen by the time this runs. Nothing dereferences that
     * holder - {@code AdvancementHolder} is keyed by {@link net.minecraft.resources.Identifier}, not by
     * holder, all the way into {@code ServerAdvancementManager}.
     *
     * @param output          the output whose registry lookups are reused
     * @param advancementSink where the registered advancements go
     * @return the bootstrap context
     */
    public static BootstrapContext<Advancement> advancementContext(
            RecipeOutput output,
            Consumer<AdvancementHolder> advancementSink
    ) {
        return new BootstrapContext<>() {
            private final BootstrapRegistry<Advancement> pending =
                    new BootstrapRegistry<>(Registries.ADVANCEMENT, Lifecycle.stable());

            @Override
            public Holder.Reference<Advancement> register(ResourceKey<Advancement> key, Advancement advancement) {
                advancementSink.accept(new AdvancementHolder(key.identifier(), advancement));
                return pending.getOrThrow(key);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> key) {
                return output.lookup(key);
            }

            @Override
            public <S> Stream<Holder.Reference<S>> listContextElements(ResourceKey<? extends Registry<? extends S>> key) {
                return output.listContextElements(key);
            }
        };
    }
}
