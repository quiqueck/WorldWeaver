/**
 * Java API for registering and querying Minecraft Points of Interest (POI) - the mechanism villagers and
 * other AI use to find work stations, homes, beds, etc.
 * <p>
 * Register new POI types with {@link org.betterx.wover.poi.api.PoiManager#register}, which returns a
 * {@link org.betterx.wover.poi.api.WoverPoiType} usable to tag or locate instances of the type at runtime.
 * POI types themselves are registered in code (not datapack-driven), but can be associated with a block
 * tag via {@link org.betterx.wover.poi.api.WoverPoiType#setTag} so datapacks can extend which block states
 * match, using {@link org.betterx.wover.poi.api.PoiTypeExtension} under the hood.
 */
package org.betterx.wover.poi.api;
