package com.neil.voidtrade.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * VoidTrade — reverses MC-50614's interaction-range check.
 *
 * Confirmed via direct 1.21.3 vs 1.21.4 source diff (official Mojang
 * mappings):
 *
 * 1.21.3: AbstractVillager had no stillValid(Player) override; the check
 * used by MerchantMenu was Merchant#getTradingPlayer() == player only.
 *
 * 1.21.4 added, in AbstractVillager:
 *   {@literal @}Override
 *   public boolean stillValid(Player player) {
 *       return this.getTradingPlayer() == player
 *           && this.isAlive()
 *           && player.canInteractWithEntity(this, 4.0);
 *   }
 *
 * Player#canInteractWithEntity(Entity, double) (confirmed 1.21.4 source):
 *   public boolean canInteractWithEntity(Entity entity, double d) {
 *       return entity.isRemoved() ? false : this.canInteractWithEntity(entity.getBoundingBox(), d);
 *   }
 * — i.e. an isRemoved() short-circuit plus a distance check against an
 * interaction-range AABB.
 *
 * This mixin redirects only that one call so it always returns true. It
 * does not touch getTradingPlayer() or isAlive(), so:
 *   - a villager leaving the 4-block interaction range no longer closes the
 *     trading screen (MC-50614's pre-fix behavior, restored)
 *   - a villager dying still closes it (isAlive() is untouched)
 *   - a different player can't hijack the trade (getTradingPlayer() == player
 *     is untouched)
 *
 * Merchant#stillValid(Player), MerchantMenu#stillValid(Player), and
 * ClientSideMerchant#stillValid(Player) are all left completely alone —
 * MerchantMenu just delegates to this method, so redirecting here is
 * sufficient and avoids duplicating logic in multiple places.
 *
 * @Redirect (rather than @Overwrite or a cancelling @Inject) is used because
 * it only intercepts this single method call inside the vanilla method body;
 * every other line of stillValid() keeps executing exactly as Mojang wrote
 * it, which is the smallest possible change and the least likely to conflict
 * with other mods that also touch AbstractVillager or MerchantMenu.
 */
@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

	@Redirect(
		method = "stillValid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;canInteractWithEntity(Lnet/minecraft/world/entity/Entity;D)Z"
		)
	)
	private boolean voidtrade$ignoreTradeInteractionRange(Player player, Entity entity, double range) {
		return true;
	}
}
