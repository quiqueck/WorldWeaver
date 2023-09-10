package org.betterx.wover.datagen.api;

import org.betterx.wover.core.api.ModCore;
import org.betterx.wover.datagen.impl.WoverDataGenEntryPointImpl;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;

import java.util.LinkedList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus;

/**
 * A simplified entrypoint for fabric data generators.
 * <p>
 * This class provides some abstractions compared to vanilla fabric data generators. Those abstractions are:
 * <ul>
 *     <li><b>Registry Abstraction</b>: Datagenerators of the register (or bootstrap) a
 *     set of elements into a Minecraft {@link net.minecraft.core.Registry}. Those
 *     elements should then get serialized to the Datapack. Think of Structures, Features and
 *     Biomes. All those are added to a specific registry and can (at runtime) be loaded from
 *     a Datapack.
 * <p>
 *     By implementing {@link WoverFullRegistryProvider} you can easily bootstrap and serialize
 *     all Elements from a registry. With {@link WoverRegistryContentProvider} you
 *     can serialize only those elements that were generated during the Instances
 *     bootstrap phase. The later class is especially usefull when creating
 *     additional Datapacks, as you can have multiple Instances of a
 *     {@link WoverRegistryContentProvider} for the same Registry, that provide only a select
 *     subset of elements to a given Datapack (instead of writing all elements in
 *     the Namespace of the mod as {@link WoverFullRegistryProvider} would do.
 * <p>
 *     You should add your custom Registry Providers by overriding {@link #onInitializeProviders(PackBuilder)}
 *     and calling {@link PackBuilder#addRegistryProvider(PackBuilder.RegistryFactory)} on the global pack
 *     for each of them.</li>
 *     <li><b>Datapack Abstraction</b>: Mods can ship with multiple (optional) Data or ResourcePacks. By
 *     overriding {@link #onInitializeProviders(PackBuilder)} you can create custom Datapacks using the
 *     {@link #addDatapack(ResourceLocation)} Method.
 * <p>
 *     Every Datapack (like the global one) can have a set of {@link WoverRegistryProvider}s that will
 *     serialize the content to the Datapack. You can also specify a
 *     {@link org.betterx.wover.datagen.api.PackBuilder.DatapackBootstrap} that will
 *     be executed once the Datapack was preapred in order to add additional providers to the Datapack.
 * </li>
 * </ul>
 * <h2>Example</h2>
 * In the following example we create some data that will be included in the global
 * Datapack of the mod, and some values that will be included in an optional Datapack, that
 * is also included in your mod.
 * <p>
 * First, we need to register the optional Datapack with Fabric. In order to do that, we
 * use {@link ModCore#addDatapack(String, ResourcePackActivationType)} in the Mod's main
 * Entrypoint {@code MyMod}:
 * <pre class="java"> public class MyMod implements ModInitializer {
 *     public static final ModCore C = ModCore.create("my-mod");
 *
 *     public static final ResourceLocation OPTIONAL_PACK = C.addDatapack(
 *             "optional-pack",
 *             ResourcePackActivationType.NORMAL
 *     );
 *     /*...*&#47;
 * }</pre>
 * This is needed in the main Entrypoint for fabric to recognize and automatically
 * provide the Datapack. It will also create a unique {@link ResourceLocation}
 * we can use to reference the Datapack.
 * <p>
 * Next, we create a new class that extends {@link WoverDataGenEntryPoint}. This will
 * be responsible for creating the global and optional Datapack and providing the content
 * for each of them:
 * <pre class="java"> public class MyModDatagen extends WoverDataGenEntryPoint {
 *     &#64;Override
 *     protected ModCore modCore() {
 *         return MyMod.C;
 *     }
 *
 *     private void onInitializeOptionalDatapack(
 *             FabricDataGenerator fabricDataGenerator,
 *             FabricDataGenerator.Pack pack,
 *             ResourceLocation location
 *     ) {
 *         // Nothing to do here, we only use content from a registry provider
 *     }
 *
 *     &#64;Override
 *      protected void onInitializeProviders(PackBuilder globalPackBuilder) {
 *         this.addDatapack(MyMod.OPTIONAL_PACK)
 *                 .onInitializeDatapack(this::onInitializeOptionalDatapack)
 *                 .addRegistryProvider(AddonSurfaceRuleProvider::new);
 *
 *         globalPackBuilder
 *                 .addRegistryProvider(SurfaceRuleProvider::new)
 *     }
 * }</pre>
 * This example first tells the Datagenerator, that it should provide our optional
 * Datapack we registered in the main Entrypoint. It also adds two Registry Providers
 * for the SurfaceRule Registry. The first one ({@code SurfaceRuleProvider}) will
 * serialize content to the global datapack, while the second one ({@code AddonSurfaceRuleProvider})
 * will serialize content to the optional Datapack.
 * <p>
 * {@code AddonSurfaceRuleProvider} will look like this
 * <pre class="java"> public class AddonSurfaceRuleProvider extends WoverRegistryContentProvider&lt;AssignedSurfaceRule> {
 *     public static final ResourceKey&lt;AssignedSurfaceRule> TEST_MEADOW
 *             = SurfaceRuleRegistry.createKey(WoverSurfaceTestMod.C.id("test-meadow"));
 *
 *     public AddonSurfaceRuleProvider(ModCore modCore) {
 *         super(modCore, "Additional Surface Rules", SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
 *     }
 *
 *     &#64;Override
 *     protected void bootstrap(BootstapContext&lt;AssignedSurfaceRule> ctx) {
 *         SurfaceRuleBuilder
 *                 .start()
 *                 .biome(Biomes.MEADOW)
 *                 .surface(Blocks.LIME_CONCRETE.defaultBlockState())
 *                 .steep(Blocks.ORANGE_CONCRETE.defaultBlockState(), 3)
 *                 .register(ctx, TEST_MEADOW);
 *     }
 * }</pre>
 * The {@link WoverRegistryContentProvider#bootstrap(BootstapContext)} Method does build the
 * Surface Rules that will then be added to the optional Datapack.
 * <p>
 * The {@code SurfaceRuleProvider} will be responsible for creating the content that will
 * be added to the global Datapack, and looks like this:
 * <pre class="java"> public class SurfaceRuleProvider extends WoverRegistryContentProvider&lt;AssignedSurfaceRule> {
 *     public static final ResourceKey&lt;AssignedSurfaceRule> TEST_PLAINS
 *             = SurfaceRuleRegistry.createKey(MyMod.C.id("test-plains"));
 *
 *     public SurfaceRuleProvider(ModCore modCore) {
 *         super(modCore, "Test Surface Rules", SurfaceRuleRegistry.SURFACE_RULES_REGISTRY);
 *     }
 *
 *     &#64;Override
 *     protected void bootstrap(BootstapContext&lt;AssignedSurfaceRule> ctx) {
 *         SurfaceRuleBuilder
 *                 .start()
 *                 .biome(Biomes.PLAINS)
 *                 .surface(Blocks.ACACIA_PLANKS.defaultBlockState())
 *                 .register(ctx, TEST_PLAINS, 1001);
 *     }
 * }</pre>
 */
public abstract class WoverDataGenEntryPoint implements DataGeneratorEntrypoint {
    /**
     * Creates a new {@link WoverDataGenEntryPoint}.
     */
    protected WoverDataGenEntryPoint() {
    }

    private List<PackBuilder> builders = null;
    private PackBuilder globalBuilder = null;

    /**
     * Creates a new {@link PackBuilder} for an additional Datapack.
     *
     * @param location The {@link ResourceLocation} of the Datapack
     * @return The new {@link PackBuilder} for the Datapack
     */
    protected PackBuilder addDatapack(ResourceLocation location) {
        PackBuilder res = new PackBuilder(modCore(), location);
        builders.add(res);
        return res;
    }

    /**
     * Called when the Datagenerator is initialized. This is the place where you should
     * register your Registry Providers to the {@code globalPack} or add {@link PackBuilder}s
     * for additional Datapacks.
     *
     * @param globalPack The {@link PackBuilder} for the global Datapack
     * @see #addDatapack(ResourceLocation)
     */
    protected abstract void onInitializeProviders(PackBuilder globalPack);

    /**
     * Returns the {@link ModCore} instance that is resposible for the
     * Datagenerator. This is used to get the namespace and the logger of
     * the mod.
     *
     * @return The {@link ModCore} instance
     */
    protected abstract ModCore modCore();

    private void initialize() {
        synchronized (this) {
            if (builders == null) {
                this.builders = new LinkedList<>();
                this.globalBuilder = addDatapack(null);

                onInitializeProviders(this.globalBuilder);
            }
        }
    }

    private FabricDataGenerator.Pack createBuiltinDatapack(
            FabricDataGenerator generator,
            ResourceLocation location
    ) {
        FabricDataGenerator.Pack pack = generator.createBuiltinResourcePack(location);

        //add a pack description
        pack.addProvider(
                (FabricDataOutput packOutput) -> PackMetadataGenerator.forFeaturePack(
                        packOutput,
                        Component.translatable("pack." + location.getNamespace() + "." + location.getPath() + ".description")
                )
        );

        return pack;
    }

    /**
     * Manages the creation of the Datapacks. You should override
     * {@link #onInitializeDataGenerator(FabricDataGenerator)} in order to register custom Providers
     *
     * @param fabricDataGenerator The {@link FabricDataGenerator} instance
     */
    @ApiStatus.Internal
    @Override
    public final void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        initialize();
        globalBuilder.pack = fabricDataGenerator.createPack();

        builders
                .stream()
                .filter(p -> p.location != null)
                .forEach(p -> p.pack = createBuiltinDatapack(fabricDataGenerator, p.location));

        for (PackBuilder builder : builders) {
            if (builder.location == null) {
                //call the custom providers for the global Datapack
                addDefaultGlobalProviders(builder);
            }

            //run other providers
            builder.providerFactories
                    .stream()
                    .forEach(provider -> {
                        builder.pack.addProvider(provider::getProvider);
                        addMultiProviders(builder, provider);
                    });

            //call the custom bootstrap method
            if (builder.datapackBootstrap != null) {
                builder.datapackBootstrap.bootstrap(fabricDataGenerator, builder.pack, builder.location);
            }
        }
    }

    private static void addMultiProviders(PackBuilder builder, Object provider) {
        if (provider instanceof WoverDataProvider.Secondary<?> wpp) {
            builder.pack.addProvider(wpp::getSecondaryProvider);
        }
        if (provider instanceof WoverDataProvider.Tertiary<?> wpp) {
            builder.pack.addProvider(wpp::getTertiaryProvider);
        }
    }

    private void addDefaultGlobalProviders(PackBuilder globalPack) {
        WoverDataGenEntryPointImpl.addDefaultGlobalProviders(globalPack);
    }

    /**
     * Called when the Registry set is built in {@link #buildRegistry(RegistrySetBuilder)}.
     * This is the place where you can add custom Registry boostrap methods to the Datagenerator.
     *
     * @param registryBuilder The {@link RegistrySetBuilder} instance
     */
    protected void onBuildRegistry(RegistrySetBuilder registryBuilder) {
    }

    /**
     * Adds all Registry Providers to the Datagenerator. If you need to add
     * custom Registry Providers, you can override
     * {@link #onBuildRegistry(RegistrySetBuilder)}.
     *
     * @param registryBuilder a {@link RegistrySetBuilder} instance
     */
    @ApiStatus.Internal
    @Override
    public final void buildRegistry(RegistrySetBuilder registryBuilder) {
        DataGeneratorEntrypoint.super.buildRegistry(registryBuilder);
        initialize();

        for (PackBuilder builder : builders) {
            builder.registryProviders()
                   .forEach(provider -> provider.buildRegistry(registryBuilder));
        }

        onBuildRegistry(registryBuilder);
    }

    /**
     * Register an automatic provider that is automatically added to all global packs
     * in the {@link #onInitializeDataGenerator(FabricDataGenerator)} method.
     *
     * @param providerFactory The {@link PackBuilder.ProviderFactory} to register
     * @param <T>             The type of the provider
     */
    public static <T extends DataProvider> void registerAutoProvider(PackBuilder.ProviderFactory<T> providerFactory) {
        WoverDataGenEntryPointImpl.registerAutoProvider(providerFactory);
    }
}
