package frenk.eypipes.client.layer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import frenk.eypipes.client.renderer.BasePipeRenderer;
import frenk.eypipes.item.PipeItem;
import frenk.eypipes.registries.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Render layer that adds a glowing/emissive burning tobacco effect to pipes.
 * The effect is visible while smoking and for 2 seconds (40 ticks) after stopping.
 * Renders animated glowing embers in the pipe bowl area.
 * Only renders on the specific pipe being smoked, not all pipes.
 */
public class BurningTobaccoLayer extends GeoRenderLayer<PipeItem> {

    // White texture for solid color rendering
    private static final ResourceLocation WHITE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

    // Track afterglow per ItemStack (using identity hash to track specific stacks)
    // Key: System.identityHashCode of ItemStack, Value: AfterglowData
    private static final Map<Integer, AfterglowData> AFTERGLOW_DATA = new ConcurrentHashMap<>();

    // Duration of afterglow effect in ticks (2 seconds = 40 ticks)
    private static final int AFTERGLOW_TICKS = 40;

    // Animation ticker for flickering effect
    private static float animationTicker = 0;

    // Helper class to store afterglow data
    private static class AfterglowData {
        final long stopTime;
        final ItemStack stackRef;

        AfterglowData(long stopTime, ItemStack stackRef) {
            this.stopTime = stopTime;
            this.stackRef = stackRef;
        }
    }

    public BurningTobaccoLayer(GeoRenderer<PipeItem> renderer) {
        super(renderer);
    }

    /**
     * Called when a player starts smoking a specific pipe.
     */
    public static void onStartSmoking(Player player, ItemStack pipeStack) {
        if (player != null && pipeStack != null) {
            // Remove any existing afterglow for this stack
            AFTERGLOW_DATA.remove(System.identityHashCode(pipeStack));
        }
    }

    /**
     * Called when a player stops smoking - starts the afterglow timer for that specific pipe.
     */
    public static void onStopSmoking(Player player, ItemStack pipeStack, long gameTime) {
        if (player != null && pipeStack != null) {
            int stackId = System.identityHashCode(pipeStack);
            AFTERGLOW_DATA.put(stackId, new AfterglowData(gameTime, pipeStack));
        }
    }

    /**
     * Check if afterglow should be rendered for a specific ItemStack.
     */
    private static boolean hasAfterglow(ItemStack stack, long currentGameTime) {
        int stackId = System.identityHashCode(stack);
        AfterglowData data = AFTERGLOW_DATA.get(stackId);
        if (data == null) return false;

        // Check if the reference is still the same stack
        if (data.stackRef != stack) {
            AFTERGLOW_DATA.remove(stackId);
            return false;
        }

        // Check if within afterglow period
        long elapsed = currentGameTime - data.stopTime;
        if (elapsed >= AFTERGLOW_TICKS) {
            AFTERGLOW_DATA.remove(stackId);
            return false;
        }

        return true;
    }

    /**
     * Get afterglow intensity for a specific ItemStack.
     */
    private static float getAfterglowIntensity(ItemStack stack, long currentGameTime) {
        int stackId = System.identityHashCode(stack);
        AfterglowData data = AFTERGLOW_DATA.get(stackId);
        if (data == null || data.stackRef != stack) return 0f;

        long elapsed = currentGameTime - data.stopTime;
        if (elapsed >= AFTERGLOW_TICKS) return 0f;

        return 1.0f - ((float) elapsed / AFTERGLOW_TICKS);
    }

    @Override
    public void render(PoseStack poseStack, PipeItem animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource,
                       VertexConsumer buffer, float partialTick,
                       int packedLight, int packedOverlay) {

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        // Get the current ItemStack being rendered from BasePipeRenderer
        ItemStack currentStack = BasePipeRenderer.getCurrentRenderingStack();
        if (currentStack == null || currentStack.isEmpty()) return;

        long gameTime = player.level().getGameTime();

        // Check if THIS specific pipe is being smoked
        boolean isBeingSmoked = BasePipeRenderer.isCurrentStackBeingSmoked();

        // Check if THIS specific pipe has afterglow
        boolean hasAfterglowEffect = hasAfterglow(currentStack, gameTime);

        // Only render if this specific pipe is smoking or has afterglow
        if (!isBeingSmoked && !hasAfterglowEffect) {
            return;
        }

        // Calculate intensity
        float intensity;
        if (isBeingSmoked) {
            intensity = 1.0f;
        } else {
            intensity = getAfterglowIntensity(currentStack, gameTime);
        }

        if (intensity < 0.01f) return;

        // Update animation ticker for flickering
        animationTicker += partialTick * 0.15f;
        if (animationTicker > 1000f) animationTicker = 0f;

        // Determine bowl position based on pipe type
        // Corn cob pipe has a taller bowl (Y=8 vs Y=7.5 for other pipes)
        float bowlY = 7.5f;  // Default for most pipes
        float bowlZ = 2.0f;

        // Check for pipes with taller bowls (smokeparticles at Y=8 instead of Y=7.5)
        // These pipes need ember at ~8.1 (8.5 - 0.4 = 8.1) to be visible above bowl rim
        if (currentStack.getItem() == ModItems.CORN_COB_PIPE.get() ||
            currentStack.getItem() == ModItems.CALABASH_PIPE.get() ||
            currentStack.getItem() == ModItems.CLAY_PIPE.get()) {
            bowlY = 8.5f;  // Higher bowl position
        }

        // Render glowing embers in the bowl area
        renderBowlEmbers(poseStack, bufferSource, intensity, partialTick, bowlY, bowlZ);
    }

    /**
     * Render glowing ember effect in the pipe bowl.
     * Creates animated flickering embers using vertex-based rendering.
     * @param bowlY Y position based on pipe type
     * @param bowlZ Z position of the bowl
     */
    private void renderBowlEmbers(PoseStack poseStack, MultiBufferSource bufferSource,
                                   float intensity, float partialTick, float bowlY, float bowlZ) {
        poseStack.pushPose();

        // Position at the top of the bowl using the smokeparticles bone position
        // GeckoLib uses 1/16 scale for model units
        float scale = 1f / 16f;
        // Offset slightly down from the locator to be inside the bowl
        poseStack.translate(0, (bowlY - 0.4f) * scale, bowlZ * scale);

        // Get the pose for vertex rendering
        PoseStack.Pose pose = poseStack.last();
        Matrix4f posMatrix = pose.pose();

        // Use emissive render type for maximum brightness glow effect
        VertexConsumer vertexConsumer = bufferSource.getBuffer(
                RenderType.entityTranslucentEmissive(WHITE_TEXTURE));

        // Calculate ember animation
        float time = animationTicker;
        float flicker1 = (float) (0.7f + 0.3f * Math.sin(time * 3.0f));
        float flicker2 = (float) (0.8f + 0.2f * Math.sin(time * 4.5f + 1.5f));
        float flicker3 = (float) (0.6f + 0.4f * Math.sin(time * 2.0f + 3.0f));

        // Bowl is 2 units wide, so radius is 1 unit
        float bowlRadius = 1.2f * scale;

        // Central bright ember (hottest part) - VERY BRIGHT
        renderEmberQuad(posMatrix, pose, vertexConsumer,
                0, 0, bowlRadius * 0.8f,
                intensity * flicker1 * 1.5f, 1.0f, 0.7f, 0.2f);

        // Surrounding embers in a ring pattern - BRIGHTER
        int emberCount = 6;
        for (int i = 0; i < emberCount; i++) {
            float angle = (float) Math.toRadians((360f / emberCount) * i + time * 8);
            float dist = bowlRadius * 0.5f;
            float x = (float) Math.cos(angle) * dist;
            float z = (float) Math.sin(angle) * dist;
            float emberFlicker = (i % 2 == 0) ? flicker2 : flicker3;

            // Bright orange-red ember colors
            float r = 1.0f;
            float g = 0.5f + 0.3f * emberFlicker;
            float b = 0.1f + 0.15f * (1 - emberFlicker);

            renderEmberQuad(posMatrix, pose, vertexConsumer,
                    x, z, bowlRadius * 0.4f,
                    intensity * emberFlicker * 1.3f, r, g, b);
        }

        // Inner glow halo - MUCH BRIGHTER
        renderEmberQuad(posMatrix, pose, vertexConsumer,
                0, 0, bowlRadius * 1.2f,
                intensity * 0.9f * flicker1, 1.0f, 0.55f, 0.15f);

        // Outer glow - BRIGHTER
        renderEmberQuad(posMatrix, pose, vertexConsumer,
                0, 0, bowlRadius * 1.6f,
                intensity * 0.6f * flicker2, 1.0f, 0.4f, 0.08f);

        poseStack.popPose();
    }

    /**
     * Render a single ember quad at the given position.
     */
    private void renderEmberQuad(Matrix4f posMatrix, PoseStack.Pose pose,
                                  VertexConsumer vertexConsumer,
                                  float x, float z, float size,
                                  float alpha, float r, float g, float b) {
        if (alpha < 0.01f) return;

        int alphaInt = Math.min(255, (int) (alpha * 255));
        int redInt = (int) (r * 255);
        int greenInt = (int) (g * 255);
        int blueInt = (int) (b * 255);

        float halfSize = size / 2;

        // Full brightness for emissive effect
        int light = 15728880;

        // Render a flat quad facing up (looking into the bowl)
        vertexConsumer.addVertex(posMatrix, x - halfSize, 0, z - halfSize)
                .setColor(redInt, greenInt, blueInt, alphaInt)
                .setUv(0, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(posMatrix, x + halfSize, 0, z - halfSize)
                .setColor(redInt, greenInt, blueInt, alphaInt)
                .setUv(1, 0)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(posMatrix, x + halfSize, 0, z + halfSize)
                .setColor(redInt, greenInt, blueInt, alphaInt)
                .setUv(1, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);

        vertexConsumer.addVertex(posMatrix, x - halfSize, 0, z + halfSize)
                .setColor(redInt, greenInt, blueInt, alphaInt)
                .setUv(0, 1)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0, 1, 0);
    }

    /**
     * Clean up old entries from the tracking map.
     */
    public static void cleanupOldEntries(long currentGameTime) {
        AFTERGLOW_DATA.entrySet().removeIf(entry -> {
            AfterglowData data = entry.getValue();
            if (data == null) return true;
            return (currentGameTime - data.stopTime) > AFTERGLOW_TICKS * 2;
        });
    }
}
