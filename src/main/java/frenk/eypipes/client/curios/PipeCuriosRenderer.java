package frenk.eypipes.client.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;
//? if <1.21.9 {
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
//?} else {
/*import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
*///?}

/**
 * Curios slot renderer for the PipeItem.
 * Renders the pipe when equipped in a Curios slot (head or chest).
 * Ported from Trinkets API to Curios API for NeoForge 1.21.1.
 *
 * <p>Curios 13 renders through the submit phase, and Minecraft's entity render-state rework
 * means {@code RenderLayerParent} is parameterised by the render state rather than the entity.
 * Both change the method signature; the placement maths below is untouched.
 */
public class PipeCuriosRenderer implements ICurioRenderer {

    /** Position the pipe on the wearer according to which Curios slot holds it. */
    private static void applySlotPose(PoseStack poseStack, HumanoidModel<?> humanoidModel, String slotId) {
        if ("chest".equals(slotId) || "pipe_chest".equals(slotId)) {
            // Transform to right hand position for chest slot
            humanoidModel.rightArm.translateAndRotate(poseStack);

            float offsetX = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewX.get();
            float offsetY = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewY.get();
            float offsetZ = (float) (double) EyPipesConfig.CLIENT.particleOffsetThirdViewZ.get();

            poseStack.translate(offsetX, offsetY, offsetZ);
            poseStack.mulPose(Axis.XP.rotationDegrees(25));  // Rotate to hold properly
            poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // Rotate to hold properly
            poseStack.mulPose(Axis.YP.rotationDegrees(330)); // Adjust orientation
            poseStack.scale(0.9f, 0.9f, 0.9f);
        } else {
            // Default head position for other slots (like pipe_head)
            humanoidModel.head.translateAndRotate(poseStack);

            // Apply custom transformations for head trinket slot positioning
            poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // Rotation: Z-axis rotation
            poseStack.mulPose(Axis.YP.rotationDegrees(350)); // Rotation: Y-axis tilt
            poseStack.scale(0.9f, 0.9f, 0.9f);
        }
    }

    //? if <1.21.9 {
    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent, MultiBufferSource bufferSource,
            int packedLight, float limbSwing, float limbSwingAmount, float partialTick,
            float ageInTicks, float netHeadYaw, float headPitch) {

        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        poseStack.pushPose();
        applySlotPose(poseStack, humanoidModel, slotContext.identifier());
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED,
                packedLight, OverlayTexture.NO_OVERLAY, poseStack, bufferSource,
                slotContext.entity().level(), 0);
        poseStack.popPose();
    }
    //?} else {
    /*@Override
    public <S extends LivingEntityRenderState, M extends EntityModel<? super S>> void render(
            ItemStack stack, SlotContext slotContext, PoseStack poseStack,
            SubmitNodeCollector collector, int packedLight, S renderState,
            RenderLayerParent<S, M> renderLayerParent, EntityRendererProvider.Context context,
            float yRotation, float xRotation) {

        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?> humanoidModel)) {
            return;
        }

        poseStack.pushPose();
        applySlotPose(poseStack, humanoidModel, slotContext.identifier());

        ItemStackRenderState itemState = new ItemStackRenderState();
        Minecraft.getInstance().getItemModelResolver().updateForTopItem(itemState, stack,
                ItemDisplayContext.FIXED, slotContext.entity().level(), null, 0);
        itemState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);

        poseStack.popPose();
    }
    *///?}
}
