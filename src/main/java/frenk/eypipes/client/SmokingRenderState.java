package frenk.eypipes.client;

/**
 * Extra state EyPipes attaches to a humanoid render state.
 *
 * <p>From 1.21.9 a model is posed from a render state rather than from the entity, and that
 * state carries no {@code ItemStack}, so the humanoid mixin cannot tell a pipe from any other
 * item being used. A mixin adds this to {@code HumanoidRenderState} and a second one fills it in
 * while the state is extracted, which is the only point where the entity is still in hand.
 */
public interface SmokingRenderState {

    /** Ticks the entity has been smoking, or -1 when it is not holding a lit pipe or cigar. */
    int eypipes$getSmokingTicks();

    void eypipes$setSmokingTicks(int ticks);
}
