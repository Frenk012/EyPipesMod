package frenk.eypipes.compat;

//? if <1.21.2
import net.minecraft.world.InteractionResultHolder;
//? if >=1.20.5 && <1.21.2
import net.minecraft.world.ItemInteractionResult;
//? if <1.20.5 || >=1.21.2
//import net.minecraft.world.InteractionResult;

import net.minecraft.world.item.ItemStack;

/**
 * Version seam for interaction results.
 *
 * <p>Minecraft 1.21.2 collapsed {@code InteractionResult}, {@code InteractionResultHolder} and
 * {@code ItemInteractionResult} into a single sealed {@code InteractionResult} hierarchy. The
 * constants and factory methods below are the only places EyPipes names those types, so porting
 * across that break means editing this one file rather than every block and item that returns a
 * result. The enclosing method signatures still change per version and are branched at their own
 * declaration sites.
 *
 * <p>The methods, in both variants, are:
 * <ul>
 *   <li>{@code itemSuccess} - succeeded, arm swings</li>
 *   <li>{@code itemFail} - failed, stop here</li>
 *   <li>{@code itemConsume} - consumed the click without swinging</li>
 *   <li>{@code itemSidedSuccess} - succeeded, swinging on the client and acting on the server</li>
 *   <li>{@code itemPassToBlock} - the held item did nothing, try the block's own interaction</li>
 *   <li>{@code useFail} / {@code useConsume} / {@code useSuccess} - the same three outcomes for
 *       using an item, where success may replace the held stack</li>
 * </ul>
 *
 * <p>Two mappings worth recording, because they are not one-to-one:
 * <ul>
 *   <li>{@code sidedSuccess(client)} used to mean SUCCESS on the client and CONSUME on the
 *       server. The unified {@code SUCCESS} carries {@code SwingSource.CLIENT} and already
 *       behaves that way on both sides, so the side argument is no longer needed.</li>
 *   <li>{@code PASS_TO_DEFAULT_BLOCK_INTERACTION} became {@code TRY_WITH_EMPTY_HAND}.</li>
 * </ul>
 *
 * <p>Before 1.20.5 there was no {@code ItemInteractionResult} either: blocks had a single
 * {@code use} method returning {@code InteractionResult}, where passing on to the block's own
 * interaction is plain {@code PASS}.
 *
 * <p>Note for editors: the inactive branch below is wrapped in a block comment by the
 * preprocessor, so it must not contain block comments of its own.
 */
public final class Interactions {

    private Interactions() {}

    //? if <1.20.5 {
    /*public static InteractionResult itemSuccess() {
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult itemFail() {
        return InteractionResult.FAIL;
    }

    public static InteractionResult itemConsume() {
        return InteractionResult.CONSUME;
    }

    public static InteractionResult itemSidedSuccess(boolean isClientSide) {
        return InteractionResult.sidedSuccess(isClientSide);
    }

    public static InteractionResult itemPassToBlock() {
        return InteractionResult.PASS;
    }
    *///?} elif <1.21.2 {
    public static ItemInteractionResult itemSuccess() {
        return ItemInteractionResult.SUCCESS;
    }

    public static ItemInteractionResult itemFail() {
        return ItemInteractionResult.FAIL;
    }

    public static ItemInteractionResult itemConsume() {
        return ItemInteractionResult.CONSUME;
    }

    public static ItemInteractionResult itemSidedSuccess(boolean isClientSide) {
        return ItemInteractionResult.sidedSuccess(isClientSide);
    }

    public static ItemInteractionResult itemPassToBlock() {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    //?}

    //? if <1.21.2 {
    public static InteractionResultHolder<ItemStack> useFail(ItemStack stack) {
        return InteractionResultHolder.fail(stack);
    }

    public static InteractionResultHolder<ItemStack> useConsume(ItemStack stack) {
        return InteractionResultHolder.consume(stack);
    }

    public static InteractionResultHolder<ItemStack> useSuccess(ItemStack stack) {
        return InteractionResultHolder.success(stack);
    }
    //?} else {
    /*public static InteractionResult itemSuccess() {
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult itemFail() {
        return InteractionResult.FAIL;
    }

    public static InteractionResult itemConsume() {
        return InteractionResult.CONSUME;
    }

    // The side argument is unused here: SUCCESS carries SwingSource.CLIENT.
    public static InteractionResult itemSidedSuccess(boolean isClientSide) {
        return InteractionResult.SUCCESS;
    }

    public static InteractionResult itemPassToBlock() {
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    public static InteractionResult useFail(ItemStack stack) {
        return InteractionResult.FAIL;
    }

    public static InteractionResult useConsume(ItemStack stack) {
        return InteractionResult.CONSUME;
    }

    public static InteractionResult useSuccess(ItemStack stack) {
        return InteractionResult.SUCCESS.heldItemTransformedTo(stack);
    }
    *///?}
}
