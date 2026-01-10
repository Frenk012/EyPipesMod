package frenk.eypipes.client;

import frenk.eypipes.item.custom.AnimatedCigar;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer;

/**
 * GeoItemRenderer for the AnimatedCigar using GeckoLib 3.x
 * Enhanced with custom first-person positioning
 */
public class AnimatedCigarRenderer extends GeoItemRenderer<AnimatedCigar> {
    
    public AnimatedCigarRenderer() {
        super(new AnimatedCigarModel());
    }
    
    @Override
    public void render(ItemStack stack, ModelTransformation.Mode transformType, MatrixStack matrixStack, VertexConsumerProvider buffer, int packedLight, int packedOverlay) {
        // Apply custom first-person positioning when the cigar is being used
        if (transformType == ModelTransformation.Mode.FIRST_PERSON_RIGHT_HAND || transformType == ModelTransformation.Mode.FIRST_PERSON_LEFT_HAND) {
            MinecraftClient client = MinecraftClient.getInstance();
            PlayerEntity player = client.player;
            
            if (player != null && player.isUsingItem() && player.getActiveItem() == stack) {
                matrixStack.push();
                // Lower the cigar by 0.5f when being used in first person
                matrixStack.translate(0.0f, -0.5f, -0.15f);
                super.render(stack, transformType, matrixStack, buffer, packedLight, packedOverlay);
                matrixStack.pop();
                return;
            }
        }
        
        // Default rendering for all other cases
        super.render(stack, transformType, matrixStack, buffer, packedLight, packedOverlay);
    }
}