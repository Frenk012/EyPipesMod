package frenk.eypipes.client.renderer;

import frenk.eypipes.client.model.CigarModel;
import frenk.eypipes.item.CigarItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

/**
 * GeckoLib 4 item renderer for the CigarItem.
 * Handles custom first-person positioning during use.
 * Ported from GeckoLib 3's GeoItemRenderer to GeckoLib 4's GeoItemRenderer.
 */
public class CigarItemRenderer extends GeoItemRenderer<CigarItem> {

    public CigarItemRenderer() {
        super(new CigarModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            net.minecraft.client.renderer.MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        // Apply custom first-person positioning when the cigar is being used
        if (transformType == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND ||
            transformType == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {

            Player player = Minecraft.getInstance().player;

            if (player != null && player.isUsingItem() && player.getUseItem() == stack) {
                poseStack.pushPose();
                // Lower the cigar slightly when being used in first person
                poseStack.translate(0.0f, -0.5f, -0.15f);
                super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
                poseStack.popPose();
                return;
            }
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
}
