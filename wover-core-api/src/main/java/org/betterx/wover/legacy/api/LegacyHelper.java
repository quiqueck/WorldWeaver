package org.betterx.wover.legacy.api;

import org.betterx.wover.core.api.ModCore;

/**
 * Helpers to ease migration from BCLib/"Worlds Together" (the predecessors of WorldWeaver) to WorldWeaver.
 * <p>
 * {@link #WORLDS_TOGETHER_CORE} and {@link #BCLIB_CORE} identify those legacy mods
 */
public class LegacyHelper {
    /**
     * {@link ModCore} identifying the legacy "Worlds Together" mod.
     */
    public static final ModCore WORLDS_TOGETHER_CORE = ModCore.create("worlds_together");
    /**
     * {@link ModCore} identifying the legacy BCLib mod.
     */
    public static final ModCore BCLIB_CORE = ModCore.create("bclib");
}
