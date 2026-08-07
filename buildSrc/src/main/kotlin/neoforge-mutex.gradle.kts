import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

// createMinecraftArtifacts decompiles and recompiles Minecraft. With one subproject per
// Minecraft version and parallel builds enabled, several of those can start at once and
// thrash the machine. This limits them to one at a time.
interface NeoForgeMutex : BuildService<BuildServiceParameters.None>

val mutex = gradle.sharedServices.registerIfAbsent("createMinecraftArtifactsMutex", NeoForgeMutex::class.java) {
    maxParallelUsages.set(1)
}

tasks.named { it == "createMinecraftArtifacts" }.configureEach {
    usesService(mutex)
}
