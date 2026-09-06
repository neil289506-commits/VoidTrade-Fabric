package com.neil.voidtrade.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * VoidTrade — reverses MC-50614's interaction-range check.
 *
 * ERA B variant: Minecraft 1.21.11 through 26.2 (confirmed via direct
 * source diff across every version in this range — see project notes).
 *
 * Two things changed vs. the 1.21.4-1.21.10 (Era A) variant, confirmed by
 * diffing the actual decompiled sources for every version in between:
 *
 *   1. AbstractVillager's package moved from
 *      net.minecraft.world.entity.npc to
 *      net.minecraft.world.entity.npc.villager (as of 1.21.11).
 *
 *   2. Player#canInteractWithEntity(Entity, double) was renamed to
 *      Player#isWithinEntityInteractionRange(Entity, double) (as of
 *      1.21.11). The method body is otherwise identical (same
 *      isRemoved() short-circuit, same distance-squared comparison) —
 *      confirmed by diffing Player.java before/after the rename, so this
 *      is a pure rename, not a logic change.
 *
 * stillValid() itself, from 1.21.11 through 26.2, is unchanged:
 *   public boolean stillValid(final Player player) {
 *       return this.getTradingPlayer() == player
 *           && this.isAlive()
 *           && player.isWithinEntityInteractionRange(this, 4.0);
 *   }
 *
 * As in the Era A mixin, only the interaction-range call is redirected to
 * always return true. getTradingPlayer() and isAlive() are untouched, so
 * villager death still closes the trade screen and another player still
 * can't hijack an open trade.
 */
@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {

	@Redirect(
		method = "stillValid",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/player/Player;isWithinEntityInteractionRange(Lnet/minecraft/world/entity/Entity;D)Z"
		)
	)
	private boolean voidtrade$ignoreTradeInteractionRange(Player player, Entity entity, double range) {
		return true;
	}
}
