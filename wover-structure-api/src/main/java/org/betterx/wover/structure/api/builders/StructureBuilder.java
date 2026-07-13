package org.betterx.wover.structure.api.builders;

import net.minecraft.world.level.levelgen.structure.Structure;

/**
 * A generic {@link BaseStructureBuilder} for a custom {@link Structure} subclass that does not need any
 * builder methods beyond the ones from {@link BaseStructureBuilder} (i.e. it has no additional settings to
 * configure). Created via {@link org.betterx.wover.structure.api.StructureKey.Simple#bootstrap}.
 *
 * @param <S> The {@link Structure} type
 */
public interface StructureBuilder<S extends Structure> extends BaseStructureBuilder<S, StructureBuilder<S>> {
}
