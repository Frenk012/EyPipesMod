package frenk.eypipes.compat;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Version seam for drawing a loose {@link ItemStack} from a block entity renderer.
 *
 * <p>How an item is drawn outside the inventory changed twice across the versions EyPipes targets:
 * Minecraft 1.21.4 replaced {@code ItemRenderer#renderStatic} with {@code ItemModelResolver} plus
 * {@code ItemStackRenderState}, and 1.21.9 turned rendering into a submit phase. The drying rack,
 * pipe rack and cutting board renderers all want the same thing regardless — "draw this stack at
 * the current pose" — so they call this instead of naming the mechanism.
 *
 * <p>Current implementation targets Minecraft 1.21.1.
 */
@OnlyIn(Dist.CLIENT)
public final class ItemStackRenderHelper {

    private ItemStackRenderHelper() {}

    /**
     * Draw a stack at the current position on the pose stack.
     *
     * @param context the display context the stack should be posed for
     * @param level   the level the stack is being drawn in, used for model context
     */
    public static void render(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
            MultiBufferSource buffer, Level level, int packedLight, int packedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, context, packedLight, packedOverlay, poseStack, buffer, level, 0);
    }
}
