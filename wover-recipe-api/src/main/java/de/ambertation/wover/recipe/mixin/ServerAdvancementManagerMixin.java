package de.ambertation.wover.recipe.mixin;

import de.ambertation.wover.entrypoint.LibWoverRecipe;
import de.ambertation.wover.recipe.impl.RecipeRuntimeProviderImpl;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementTree;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.ServerAdvancementManager;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The advancement analogue of {@link RecipeManagerMixin}, and the reason runtime-contributed recipes finally
 * unlock themselves in the recipe book.
 * <p>
 * 26.3 turned advancements into a datapack registry with the same lifecycle recipes got:
 * {@code ServerAdvancementManager} is no longer a reload listener, it is built once per reload from a
 * {@link HolderLookup.Provider} in its constructor, from {@code registries.lookupOrThrow(Registries.ADVANCEMENT)}.
 * So the injection point that {@link RecipeManagerMixin} uses for recipes exists here verbatim, and the
 * {@code minecraft:recipe_unlocked} advancement that every recipe builder produces can be registered instead
 * of thrown away (which is what happened up to and including 26.1 - a long-standing bug, not a limitation).
 * <p>
 * The tail of the constructor is the right place: {@code advancements} has been built and the
 * {@link AdvancementTree} has been assembled and positioned, but nothing has consumed the manager yet -
 * {@code ReloadableServerResources} only stores it, and every {@code PlayerAdvancements} is (re)built from it
 * afterwards, so a player sees our advancements exactly as it sees datapack ones.
 * <p>
 * Advancements whose id a datapack already claimed are skipped rather than replaced. That deliberately
 * differs from the recipe side (where a contributor may replace a datapack recipe): an advancement is a node
 * in a tree that vanilla has already wired up with parents, children and positions, and swapping one out
 * underneath that structure is not the same cheap operation as replacing a map entry.
 */
@Mixin(ServerAdvancementManager.class)
public class ServerAdvancementManagerMixin {
    // Both fields are final in 26.3; `advancements` is an ImmutableMap that has to be rebuilt to be extended,
    // hence @Mutable. The tree is mutated in place through its own addAll().
    @Shadow
    @Final
    @Mutable
    private Map<Identifier, AdvancementHolder> advancements;

    @Shadow
    @Final
    private AdvancementTree tree;

    @Inject(method = "<init>", at = @At("TAIL"))
    void wover_addRuntimeRecipeAdvancements(HolderLookup.Provider registries, CallbackInfo ci) {
        final List<AdvancementHolder> contributed =
                RecipeRuntimeProviderImpl.takeContributedAdvancements(registries);
        if (contributed.isEmpty()) return;

        final Map<Identifier, AdvancementHolder> merged = new LinkedHashMap<>(this.advancements);
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
