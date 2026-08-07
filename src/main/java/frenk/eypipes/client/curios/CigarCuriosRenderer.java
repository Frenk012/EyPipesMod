package frenk.eypipes.client.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
 * Curios slot renderer for the CigarItem.
 * Renders the cigar in the wearer's mouth when equipped in a head slot.
 *
 * <p>See {@link PipeCuriosRenderer} for why the signature is version-specific.
 */
public class CigarCuriosRenderer implements ICurioRenderer {

    /** Put the cigar in the wearer's mouth. Only the head slot positions it. */
    private static void applySlotPose(PoseStack poseStack, HumanoidModel<?> humanoidModel, String slotId) {
        if ("head".equals(slotId) || "pipe_head".equals(slotId)) {
            humanoidModel.head.translateAndRotate(poseStack);

            // Apply cigar transformations (converted from model units to world units)
            poseStack.translate(0.0f, 0.6f / 16.0f, -4.75f / 16.0f);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
            poseStack.scale(0.7f, 0.7f, 0.7f);
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
