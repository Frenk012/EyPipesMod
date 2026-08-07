package frenk.eypipes.compat;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/**
 * Version seam for item use cooldowns.
 *
 * <p>Cooldowns were keyed by {@code Item} up to 1.21.1 and by {@code ItemStack} from 1.21.5,
 * where the stack's cooldown-group component decides what shares a cooldown with what. Passing
 * the stack works for both, since the item can always be recovered from it.
 */
public final class Cooldowns {

    private Cooldowns() {}

    /** Put this player's stack on cooldown for {@code ticks}. */
    public static void add(Player player, ItemStack stack, int ticks) {
        //? if <1.21.5 {
        player.getCooldowns().addCooldown(stack.getItem(), ticks);
        //?} else
        /*player.getCooldowns().addCooldown(stack, ticks);*/
    }
}
