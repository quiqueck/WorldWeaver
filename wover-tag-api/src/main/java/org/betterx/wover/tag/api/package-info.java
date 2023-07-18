/**
 * Tag Managment API
 * <p>
 * The {@link org.betterx.wover.tag.api.TagManager} is the main entry point for the API. It provides access
 * to {@link org.betterx.wover.tag.api.TagRegistry}s that manage typical tag types. It also allows you to create
 * custom {@link org.betterx.wover.tag.api.TagRegistry}s.
 * <p>
 * A {@link org.betterx.wover.tag.api.TagRegistry} is primarily used to create {@link net.minecraft.tags.TagKey}s.
 * This example will add a new Block-Tag to the mods Namespace with {@code C} beeing an Instance of the Mod's
 * {@link org.betterx.wover.core.api.ModCore}:
 * <pre class="java">TagKey&lt;Block> myTag = TagManager.BLOCKS.makeTag(C, "mytag");</pre>
 * <h2>Populating Tags in a Datagenerator</h2>
 * You can either add Elements to a Tag in the datagenerator or at runtime. However, it is recommended to use the
 * datagenerator whenever possible.
 * <h3>Using a {@code WoverTagProvider}</h3>
 * Adding an Element to the previously generated Tag could look like this:
 * <pre class="java"> public class BlockTagProvider extends WoverTagProvider.ForBlocks {
 *      public BlockTagProvider(FabricDataOutput output, CompletableFuture&lt;HolderLookup.Provider&gt; registriesFuture) {
 *          super(output, registriesFuture);
 *      }
 *
 *      protected void prepareTags(TagBootstrapContext&lt;Block&gt; ctx) {
 *         ctx.add(myTag, Blocks.DIRT);
 *      }
 * } </pre>
 * You would also need to register this TagProvider with a Pack:
 * <pre class="java"> public class MyDatagen extends WoverDataGenEntryPoint {
 *     &#64;Override
 *     protected void onInitializeProviders(PackBuilder globalPack) {
 *         globalPack.callOnInitializeDatapack((gen, pack, id) -> pack.addProvider(BlockTagProvider::new));
 *     }
 *
 *     &#64;Override
 *     protected ModCore modCore() {
 *         return /&#42; ModCore Instance &#42;/;
 *     }
 * } </pre>
 * <h3>DataProvider Interface</h3>
 * If Elements registered to the builtin Blocks or Items Registry implement certain Interfaces, they will
 * be automatically added during the Datapack generation. For a general start, you may want to look at
 * {@link org.betterx.wover.tag.api.ItemTagDataProvider} and {@link org.betterx.wover.tag.api.BlockTagDataProvider}.
 * <h2>Populating Tags at Runtime</h2>
 * You can also add Elements to a Tag at runtime, however, this is not recommended. At runtime you need
 * to subscribe to the {@link org.betterx.wover.tag.api.TagRegistry#bootstrapEvent()} to add elements to a Tag.
 * This event is called, whenever the list of available Tags is loaded from a Datapack. This event is
 * <b>not</b> emitted when running in a Datagenerator:
 * <pre class="java"> TagManager.BLOCKS.bootstrapEvent().subscribe(ctx -> {
 *    ctx.add(myTag, Blocks.DIRT);
 * } </pre>
 */
package org.betterx.wover.tag.api;