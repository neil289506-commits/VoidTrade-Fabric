package com.neil.voidtrade;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VoidTrade
 *
 * Purpose: reverse Minecraft bug MC-50614 ("Villager trading window is not
 * closed when villager leaves interaction range"), restoring the pre-fix
 * behavior for Minecraft 1.21.4.
 *
 * This mod does NOT reintroduce the bug's absence of a death check, and does
 * NOT touch any other 1.21.4 villager/merchant behavior. The only change is
 * in {@link com.neil.voidtrade.mixin.AbstractVillagerMixin}, which redirects
 * the interaction-range check added in 1.21.4's
 * AbstractVillager#stillValid(Player) so it always passes, leaving the
 * getTradingPlayer()/isAlive() checks untouched.
 *
 * All actual logic lives in the mixin — this class only exists as the mod's
 * entrypoint and does no runtime work itself.
 */
public class VoidTrade implements ModInitializer {
	public static final String MOD_ID = "voidtrade";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("[VoidTrade] MC-50614 interaction-range check reversed — villager trading windows will no longer close due to distance.");
	}
}
