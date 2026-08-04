package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import com.google.common.collect.ImmutableMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The advancement analogue of {@link RecipeManagerMixin}, and the reason runtime-contributed recipes unlock
 * themselves in the recipe book.
 * <p>
 * Every vanilla recipe builder hands a {@code minecraft:recipe_unlocked} {@link AdvancementHolder} to
 * {@code RecipeOutput#accept}. On the runtime path that holder used to be dropped, so a recipe contributed
 * through {@code RecipeBuilder.BOOTSTRAP_RECIPES} never appeared in a player's recipe book unless it was
 * handed out with {@code /recipe give}. {@code RecipeRuntimeProviderImpl} now keeps those holders, and this
 * mixin puts them where vanilla puts the ones it read from datapacks.
 * <p>
 * The tail of {@code apply} is the right place: {@code advancements} has been rebuilt and the
 * {@link AdvancementTree} has been assembled and positioned, but nothing has consumed the manager yet - every
 * {@code PlayerAdvancements} is (re)built from it afterwards, so a player sees our advancements exactly as it
 * sees datapack ones. Positioning is not disturbed either: a recipe advancement carries no
 * {@code DisplayInfo}, and {@code TreeNodePosition} only walks nodes that have one.
 * <p>
 * Both this manager and {@code RecipeManager} are reload listeners of the same reload, and
 * {@code ReloadableServerResources#listeners()} lists the recipes first, so by the time this runs the recipe
 * half of the contribution has already been made. The hand-off is additionally keyed by identity of the
 * {@link HolderLookup.Provider} both managers were constructed with, so it fails closed rather than applying
 * a stale contribution.
 * <p>
 * Advancements whose id a datapack already claimed are skipped rather than replaced. That deliberately
 * differs from the recipe side (where a contributor may replace a datapack recipe): an advancement is a node
 * in a tree that vanilla has already wired up with parents, children and positions, and swapping one out
 * underneath that structure is not the same cheap operation as replacing a map entry.
 */
@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
    // `advancements` is the ImmutableMap apply() just built; it has to be rebuilt to be extended. The tree is
    // mutated in place through its own addAll().
    @Shadow
    private Map<ResourceLocation, AdvancementHolder> advancements;

    @Shadow
    private AdvancementTree tree;

    @Shadow
    @Final
    private HolderLookup.Provider registries;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V",
            at = @At("TAIL"))
    void wover_addRuntimeRecipeAdvancements(
            Map<ResourceLocation, Advancement> loaded,
            ResourceManager resourceManager,
            ProfilerFiller profilerFiller,
            CallbackInfo ci
    ) {
        final List<AdvancementHolder> contributed =
                RecipeRuntimeProviderImpl.takeContributedAdvancements(this.registries);
        if (contributed.isEmpty()) return;

        final Map<ResourceLocation, AdvancementHolder> merged = new LinkedHashMap<>(this.advancements);
        final List<AdvancementHolder> added = new ArrayList<>(contributed.size());
        for (AdvancementHolder holder : contributed) {
            if (merged.putIfAbsent(holder.id(), holder) == null) added.add(holder);
        }

        if (added.isEmpty()) return;

        this.advancements = ImmutableMap.copyOf(merged);
        this.tree.addAll(added);

        LibWoverRecipe.C.LOG.info("Added {} recipe unlock advancements", added.size());
    }
}
