/**
 * Java API for creating and registering {@link net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList}s
 * — the block-swap rules applied to a template pool element when it is placed.
 * <p>
 * {@link de.ambertation.wover.structure.api.processors.StructureProcessorManager} creates a
 * {@link de.ambertation.wover.structure.api.processors.StructureProcessorKey} — a wrapper around a
 * {@link net.minecraft.resources.ResourceKey} for the processor list. Call
 * {@link de.ambertation.wover.structure.api.processors.StructureProcessorKey#bootstrap(net.minecraft.data.worldgen.BootstrapContext)}
 * to obtain a {@link de.ambertation.wover.structure.api.processors.StructureProcessorBuilder} and register the
 * list:
 * <pre class="java"> public static final StructureProcessorKey MY_PROCESSOR = StructureKeys.processor(MyMod.C.id("my_processor"));
 *
 * // in a datagen BootstrapContext&lt;StructureProcessorList&gt;:
 * MY_PROCESSOR.bootstrap(context)
 *             .startRule()
 *             .startProcessor()
 *             .inputPredicateRandom(Blocks.RED_GLAZED_TERRACOTTA, 0.33f)
 *             .outputState(Blocks.RED_CONCRETE)
 *             .endProcessor()
 *             .endRule()
 *             .register();</pre>
 */
package de.ambertation.wover.structure.api.processors;
