package frenk.eypipes.client.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import frenk.eypipes.config.EyPipesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

/**
 * Curios slot renderer for the PipeItem.
 * Renders the pipe when equipped in a Curios slot (head or chest).
 * Ported from Trinkets API to Curios API for NeoForge 1.21.1.
 */
public class PipeCuriosRenderer implements ICurioRenderer {

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
            ItemStack stack,
            SlotContext slotContext,
            PoseStack poseStack,
            RenderLayerParent<T, M> renderLayerParent,
            MultiBufferSource bufferSource,
            int packedLight,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch) {

        LivingEntity entity = slotContext.entity();
        M model = renderLayerParent.getModel();

        if (model instanceof HumanoidModel<?> humanoidModel) {
            poseStack.pushPose();

            String slotId = slotContext.identifier();

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

            // Render the item
            Minecraft.getInstance().getItemRenderer().renderStatic(
                    stack,
                    ItemDisplayContext.FIXED,
                    packedLight,
                    net.minecraft.client.renderer.texture.OverlayTexture.NO_OVERLAY,
                    poseStack,
                    bufferSource,
                    entity.level(),
                    0
            );

            poseStack.popPose();
        }
    }
}
