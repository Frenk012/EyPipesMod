package frenk.eypipes.client.renderer;

import frenk.eypipes.client.model.PipeModel;
import frenk.eypipes.item.PipeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * GeckoLib 4 item renderer for the PipeItem.
 * Handles custom first-person positioning during use.
 * Ported from GeckoLib 3's GeoItemRenderer to GeckoLib 4's GeoItemRenderer.
 */
public class PipeItemRenderer extends GeoItemRenderer<PipeItem> {

    public PipeItemRenderer() {
        super(new PipeModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        // Apply custom first-person positioning when the pipe is being used
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {

            Player player = Minecraft.getInstance().player;

            if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
                poseStack.pushPose();
                // Keep pipe stable during use (no lowering)
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
                return;
            }
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
