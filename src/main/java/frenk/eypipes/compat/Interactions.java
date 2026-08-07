package frenk.eypipes.compat;

import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;

/**
 * Version seam for interaction results.
 *
 * <p>Minecraft 1.21.2 collapsed {@code InteractionResult}, {@code InteractionResultHolder} and
 * {@code ItemInteractionResult} into a single sealed {@code InteractionResult} hierarchy. The
 * constants and factory methods below are the only places EyPipes names those types, so porting
 * across that break means editing this one file rather than every block and item that returns a
 * result. The enclosing method signatures still change per version and must be branched at the
 * declaration site.
 *
 * <p>Current implementation targets Minecraft 1.21.1.
 */
public final class Interactions {

    private Interactions() {}

    // ---- Block#useItemOn results ----

    /** The interaction succeeded and the arm should swing on both sides. */
    public static ItemInteractionResult itemSuccess() {
        return ItemInteractionResult.SUCCESS;
    }

    /** The interaction failed; nothing further should be tried. */
    public static ItemInteractionResult itemFail() {
        return ItemInteractionResult.FAIL;
    }

    /** The interaction consumed the click without swinging the arm. */
    public static ItemInteractionResult itemConsume() {
        return ItemInteractionResult.CONSUME;
    }

    /** Succeeded, swinging the arm on the client and acting on the server. */
    public static ItemInteractionResult itemSidedSuccess(boolean isClientSide) {
        return ItemInteractionResult.sidedSuccess(isClientSide);
    }

    /** The held item did nothing here; fall through to the block's own interaction. */
    public static ItemInteractionResult itemPassToBlock() {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // ---- Item#use results ----

    /** Using the item failed; the held stack is unchanged. */
    public static InteractionResultHolder<ItemStack> useFail(ItemStack stack) {
        return InteractionResultHolder.fail(stack);
    }

    /** Using the item consumed the action without swinging the arm. */
    public static InteractionResultHolder<ItemStack> useConsume(ItemStack stack) {
        return InteractionResultHolder.consume(stack);
    }

    /** Using the item succeeded and the held stack may have been replaced. */
    public static InteractionResultHolder<ItemStack> useSuccess(ItemStack stack) {
        return InteractionResultHolder.success(stack);
    }
}
