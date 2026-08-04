package de.ambertation.wover.potions.impl;

import de.ambertation.wover.events.impl.EventImpl;
import de.ambertation.wover.potions.api.OnBootstrapPotions;

public class PotionManagerImpl {
    public static final EventImpl<OnBootstrapPotions> BOOTSTRAP_POTIONS =
            new EventImpl<>("BOOTSTRAP_POTIONS");
}
