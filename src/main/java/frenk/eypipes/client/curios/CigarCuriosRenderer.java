package frenk.eypipes.client.curios;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
 * Curios slot renderer for the CigarItem.
 * Renders the cigar when equipped in a Curios slot (head).
 * Ported from Trinkets API to Curios API for NeoForge 1.21.1.
 */
public class CigarCuriosRenderer implements ICurioRenderer {

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

            if ("head".equals(slotId) || "pipe_head".equals(slotId)) {
                // Transform to head position for head slot
                humanoidModel.head.translateAndRotate(poseStack);

                // Apply cigar transformations (converted from model units to world units)
                poseStack.translate(0.0f, 0.6f / 16.0f, -4.75f / 16.0f);
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.scale(0.7f, 0.7f, 0.7f);
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
