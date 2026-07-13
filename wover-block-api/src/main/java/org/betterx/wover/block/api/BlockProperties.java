package org.betterx.wover.block.api;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.NotNull;

/**
 * A collection of reusable {@link net.minecraft.world.level.block.state.properties.Property} constants
 * that are commonly needed by custom blocks but are not part of vanilla's
 * {@link BlockStateProperties}.
 */
public class BlockProperties {
    /**
     * A three-step vertical shape ({@link TripleShape#TOP}/{@link TripleShape#MIDDLE}/{@link TripleShape#BOTTOM}), stored under the property name {@code "shape"}.
     */
    public static final EnumProperty<TripleShape> TRIPLE_SHAPE = EnumProperty.create("shape", TripleShape.class);
    /**
     * A five-step vertical shape (see {@link PentaShape}), stored under the property name {@code "shape"}.
     */
    public static final EnumProperty<PentaShape> PENTA_SHAPE = EnumProperty.create("shape", PentaShape.class);

    /**
     * Boolean property named {@code "transition"}.
     */
    public static final BooleanProperty TRANSITION = BooleanProperty.create("transition");
    /**
     * Boolean property named {@code "has_light"}.
     */
    public static final BooleanProperty HAS_LIGHT = BooleanProperty.create("has_light");
    /**
     * Boolean property named {@code "is_floor"}.
     */
    public static final BooleanProperty IS_FLOOR = BooleanProperty.create("is_floor");
    /**
     * Boolean property named {@code "natural"}.
     */
    public static final BooleanProperty NATURAL = BooleanProperty.create("natural");
    /**
     * Boolean property named {@code "active"}.
     */
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
    /**
     * Boolean property named {@code "small"}.
     */
    public static final BooleanProperty SMALL = BooleanProperty.create("small");

    /**
     * Integer property named {@code "durability"}, ranging from 0 to 3, mirroring vanilla anvil damage stages.
     */
    public static final IntegerProperty DEFAULT_ANVIL_DURABILITY = IntegerProperty.create("durability", 0, 3);
    /**
     * Integer property named {@code "destruction"}, ranging from 0 to 2.
     */
    public static final IntegerProperty DESTRUCTION = IntegerProperty.create("destruction", 0, 2);
    /**
     * Integer property named {@code "rotation"}, ranging from 0 to 3.
     */
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 3);
    /**
     * Integer property named {@code "fullness"}, ranging from 0 to 3.
     */
    public static final IntegerProperty FULLNESS = IntegerProperty.create("fullness", 0, 3);
    /**
     * Integer property named {@code "color"}, ranging from 0 to 7.
     */
    public static final IntegerProperty COLOR = IntegerProperty.create("color", 0, 7);
    /**
     * Integer property named {@code "size"}, ranging from 0 to 7.
     */
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 0, 7);
    /**
     * Alias for {@link BlockStateProperties#AGE_3} (an age property ranging from 0 to 3).
     */
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    /**
     * Alias for {@link BlockStateProperties#AGE_2} (an age property ranging from 0 to 2), despite the name
     * suggesting three steps.
     */
    public static final IntegerProperty AGE_THREE = BlockStateProperties.AGE_2;
    /**
     * Boolean property named {@code "bottom"}.
     */
    public static final BooleanProperty BOTTOM = BooleanProperty.create("bottom");
    /**
     * Boolean property named {@code "top"}.
     */
    public static final BooleanProperty TOP = BooleanProperty.create("top");

    /**
     * A three-step vertical shape, e.g. for blocks that are built from a top, middle and bottom part.
     */
    public enum TripleShape implements StringRepresentable {
        TOP("top", 0), MIDDLE("middle", 1), BOTTOM("bottom", 2);

        private final String name;
        private final int index;

        TripleShape(String name, int index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }

        /**
         * Gets the numeric index of this shape (0 = {@link #TOP}, 1 = {@link #MIDDLE}, 2 = {@link #BOTTOM}).
         *
         * @return the index
         */
        public int getIndex() {
            return index;
        }

        /**
         * Gets the shape matching the given index, clamping out-of-range values to {@link #TOP}/{@link #BOTTOM}.
         *
         * @param index the index, as returned by {@link #getIndex()}
         * @return the matching shape
         */
        public static TripleShape fromIndex(int index) {
            return index > 1 ? BOTTOM : index == 1 ? MIDDLE : TOP;
        }
    }

    /**
     * A five-step vertical shape, e.g. for blocks that need finer-grained transitions than {@link TripleShape}.
     */
    public enum PentaShape implements StringRepresentable {
        BOTTOM("bottom"), PRE_BOTTOM("pre_bottom"), MIDDLE("middle"), PRE_TOP("pre_top"), TOP("top");

        private final String name;

        PentaShape(String name) {
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
