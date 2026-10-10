package frenk.eypipes.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import frenk.eypipes.client.model.EyPipesGeoModel;
import frenk.eypipes.item.CigarItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;
//? if <1.21.9 {
import net.minecraft.client.renderer.MultiBufferSource;
//?} else {
/*import net.minecraft.client.renderer.state.CameraRenderState;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.constant.dataticket.DataTicket;
import software.bernie.geckolib.renderer.base.GeoRenderState;
*///?}

/**
 * GeckoLib item renderer for the CigarItem.
 * Handles custom first-person positioning during use.
 *
 * <p>Like the pipe renderer, from 1.21.9 the "is this cigar being smoked" test happens during
 * render-state capture rather than while drawing, because those are no longer the same moment.
 */
public class CigarItemRenderer extends GeoItemRenderer<CigarItem> {

    //? if >=1.21.9 {
    /*private static final DataTicket<Boolean> IN_USE =
            DataTicket.create("eypipes:cigar_in_use", Boolean.class);
    *///?}

    public CigarItemRenderer() {
        super(new EyPipesGeoModel<>("cigar"));
    }

    /** Whether the local player is currently smoking this exact cigar in first person. */
    private static boolean isHeldAndLit(ItemStack stack, ItemDisplayContext transformType) {
        if (transformType != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                && transformType != ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            return false;
        }
        Player player = Minecraft.getInstance().player;
        return player != null && player.isUsingItem() && player.getUseItem() == stack;
    }

    /** Lower the cigar slightly while it is being smoked. */
    private static void applyInUsePose(PoseStack poseStack) {
        poseStack.translate(0.0f, -0.5f, -0.15f);
    }

    //? if <1.21.9 {
    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay) {

        if (isHeldAndLit(stack, transformType)) {
            poseStack.pushPose();
            applyInUsePose(poseStack);
            super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
            poseStack.popPose();
            return;
        }

        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
    }
    //?} else {
    /*@Override
    public void addRenderData(CigarItem animatable, RenderData relatedObject, GeoRenderState renderState,
            float partialTick) {
        super.addRenderData(animatable, relatedObject, renderState, partialTick);

        renderState.addGeckolibData(IN_USE,
                isHeldAndLit(relatedObject.itemStack(), relatedObject.renderPerspective()));
    }

    @Override
    public void adjustRenderPose(GeoRenderState renderState, PoseStack poseStack, BakedGeoModel model,
            CameraRenderState cameraState) {
        super.adjustRenderPose(renderState, poseStack, model, cameraState);

        if (renderState.getOrDefaultGeckolibData(IN_USE, false)) {
            applyInUsePose(poseStack);
        }
    }
    *///?}
}
