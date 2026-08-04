package de.ambertation.wover.structure.api.structures.nbt;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

/**
 * A single {@code .nbt} template that can be placed by a {@link RandomNbtStructure}, together with the
 * vertical offset applied to it. Added to a structure via
 * {@link de.ambertation.wover.structure.api.builders.RandomNbtBuilder#addElement(ResourceLocation, int, double)}.
 *
 * @param nbtLocation The location of the {@code .nbt} template, resolved by the active
 *                    {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager}
 * @param yOffset     The vertical offset (in blocks) applied to the found generation point before placing
 *                    this element
 */
public record RandomNbtStructureElement(ResourceLocation nbtLocation, int yOffset) {
    /**
     * The {@link Codec} used to (de)serialize a {@link RandomNbtStructureElement}.
     */
    public static final Codec<RandomNbtStructureElement> CODEC =
            RecordCodecBuilder.create((instance) ->
                    instance.group(
                                    ResourceLocation.CODEC
                                            .fieldOf("location")
                                            .forGetter((cfg) -> cfg.nbtLocation),
                                    Codec
                                            .INT
                                            .fieldOf("offset_y")
                                            .orElse(0)
                                            .forGetter((cfg) -> cfg.yOffset)
                            )
                            .apply(instance, RandomNbtStructureElement::new)
            );
}
