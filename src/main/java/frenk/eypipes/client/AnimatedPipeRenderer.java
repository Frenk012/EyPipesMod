package frenk.eypipes.client;

import frenk.eypipes.item.PipeItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

/**
 * GeoItemRenderer for the PipeItem using GeckoLib 3.x
 * Enhanced with custom first-person positioning
 */
public class AnimatedPipeRenderer extends GeoItemRenderer<PipeItem> {
    
    public AnimatedPipeRenderer() {
        super(new AnimatedPipeModel());
    }
    
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight, int packedOverlay) {
        if (transformType == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND || transformType == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            
            if (player != null && player.isUsingItem() && player.getActiveItem() == stack) {
                matrixStack.push();
                // Removed translation to prevent lowering during use
                super.render(stack, transformType, matrixStack, buffer, packedLight, packedOverlay);
                matrixStack.pop();
                return;
            }
        }
        
        super.render(stack, transformType, matrixStack, buffer, packedLight, packedOverlay);
    }
}