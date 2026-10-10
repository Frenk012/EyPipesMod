package frenk.eypipes.mixin.client;

import frenk.eypipes.client.SmokingRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Carries the smoking state onto the humanoid render state.
 *
 * <p>The render state is deliberately detached from the entity, so the only way for the model to
 * know a pipe is being smoked is for that fact to travel with the state. See
 * {@link LivingEntityRendererMixin} for where it is filled in.
 */
@Mixin(HumanoidRenderState.class)
public class HumanoidRenderStateMixin implements SmokingRenderState {

    @Unique
    private int eypipes$smokingTicks = -1;

    @Override
    public int eypipes$getSmokingTicks() {
        return this.eypipes$smokingTicks;
    }

    @Override
    public void eypipes$setSmokingTicks(int ticks) {
        this.eypipes$smokingTicks = ticks;
    }
}
