package frenk.eypipes.mixin.client;

import frenk.eypipes.client.SmokingRenderState;
import frenk.eypipes.item.CigarItem;
import frenk.eypipes.item.PipeItem;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Records, while the entity is still available, whether it is smoking.
 *
 * <p>This is the last point where the entity and its render state are both in scope; by the time
 * the model is posed only the state remains. Without this the arm animation cannot happen at all
 * on 1.21.9 and later.
 */
@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    private void eypipes$recordSmoking(LivingEntity entity, LivingEntityRenderState renderState,
            float partialTick, CallbackInfo ci) {
        if (!(renderState instanceof SmokingRenderState smokingState)) {
            return;
        }

        ItemStack mainHand = entity.getItemInHand(InteractionHand.MAIN_HAND);
        boolean smoking = entity.isUsingItem()
                && (mainHand.getItem() instanceof PipeItem || mainHand.getItem() instanceof CigarItem);

        smokingState.eypipes$setSmokingTicks(smoking ? entity.getTicksUsingItem() : -1);
    }
}
