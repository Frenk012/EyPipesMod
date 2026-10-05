package frenk.eypipes.particle;

/**
 * Marks smoke spawned in front of the local player's first-person camera. Smoke created
 * while {@link #capturing} is set follows the player's position and yaw, so it stays at
 * the cigar or pipe on screen instead of being left behind when the player turns.
 * Client thread only.
 */
public final class FirstPersonSmoke {
    static boolean capturing = false;

    private FirstPersonSmoke() {}

    public static void run(Runnable spawner) {
        capturing = true;
        try {
            spawner.run();
        } finally {
            capturing = false;
        }
    }
}
