plugins {
    id("net.neoforged.moddev.legacyforge")
    id("neoforge-mutex")
}

// Forge 1.20.1 through ModDevGradle's legacy plugin: same Mojang mappings and Parchment names
// as the NeoForge builds, with reobfuscation to SRG names for the released jar.

val mc = sc.current.version
val modId = property("mod.id") as String

version = "${property("mod.version")}+$mc-forge"
group = property("mod.group") as String
base.archivesName = modId

val generatedResources: File = rootProject.file("src/generated/${sc.current.project}")
val runDirectory: File = rootProject.file("run/${sc.current.project}")

sourceSets.named("main") {
    resources.srcDir(generatedResources)

    java.exclude(
        // Epic Fight 20.x has a different animation API from 21.x; the integration is
        // compiled in the 1.21.1 jar only for now. EpicFightCompat stays as the no-op seam.
        "frenk/eypipes/integration/epicfight/EpicFightAnimations.java",
        // 1.21.9+ only: these carry the smoking flag onto the humanoid render state.
        "frenk/eypipes/mixin/client/HumanoidRenderStateMixin.java",
        "frenk/eypipes/mixin/client/LivingEntityRendererMixin.java",
        // Minecraft's model generators replace these from 1.21.4; Forge still has the old ones.
        "frenk/eypipes/datagen/ModModelProvider.java",
    )
    resources.exclude("META-INF/neoforge.mods.toml")
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_17
    sourceCompatibility = JavaVersion.VERSION_17
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

repositories {
    mavenLocal()
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
        name = "GeckoLib"
        content { includeGroup("software.bernie.geckolib") }
    }
    maven("https://maven.theillusivec4.top/") { name = "Curios" }
    maven("https://maven.shedaniel.me/") { name = "REI" }
    maven("https://maven.architectury.dev/") { name = "Architectury" }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
        content { includeGroup("dev.emi") }
    }
    maven("https://maven.blamejared.com") {
        name = "JEI"
        content { includeGroup("mezz.jei") }
    }
}

dependencies {
    modImplementation("software.bernie.geckolib:geckolib-forge-$mc:${property("deps.geckolib")}")

    modCompileOnly("top.theillusivec4.curios:curios-forge:${property("deps.curios")}:api")
    modRuntimeOnly("top.theillusivec4.curios:curios-forge:${property("deps.curios")}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-forge:${property("deps.rei")}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-forge:${property("deps.rei")}")

    modCompileOnly("mezz.jei:jei-$mc-common-api:${property("deps.jei")}")
    modCompileOnly("mezz.jei:jei-$mc-forge-api:${property("deps.jei")}")

    modCompileOnly("dev.emi:emi-forge:${property("deps.emi")}:api")

    // Forge 1.20.1 ships Mixin but not its annotation processor, which writes the refmap
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

legacyForge {
    version = property("deps.forge") as String

    parchment {
        mappingsVersion = property("deps.parchment") as String
        minecraftVersion = mc
    }

    mods {
        register(modId) {
            sourceSet(sourceSets.main.get())
        }
    }

    runs {
        register("client") {
            client()
            gameDirectory = runDirectory
        }

        register("server") {
            server()
            gameDirectory = runDirectory
            programArgument("--nogui")
        }

        register("data") {
            data()
            gameDirectory = runDirectory
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", generatedResources.absolutePath,
                "--existing", rootProject.file("src/main/resources").absolutePath,
            )
        }
    }
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

tasks {
    processResources {
        exclude(".cache/**")

        fun MutableMap<String, String>.register(placeholder: String, property: String) {
            val value: String = sc.properties[property]
            inputs.property(placeholder, value)
            put(placeholder, value)
        }

        val props = buildMap {
            register("mod_id", "mod.id")
            register("mod_name", "mod.name")
            register("mod_version", "mod.version")
            register("mod_license", "mod.license")
            register("mod_authors", "mod.authors")
            register("mod_description", "mod.description")
            register("minecraft_version_range", "mod.mc_compat")
            register("forge_version_range", "deps.forge_range")
            register("loader_version_range", "deps.loader_range")
            register("geckolib_version_range", "deps.geckolib_range")
            register("curios_version_range", "deps.curios_range")
            register("rei_version_range", "deps.rei_range")
            register("jei_version_range", "deps.jei_range")
        }

        filesMatching("META-INF/mods.toml") { expand(props) }

        val mixinClients = "\"client.BipedModelMixin\""
        inputs.property("mixinClients", mixinClients)
        filesMatching("eypipes.mixins.json") {
            filter { line ->
                line.replace("MIXIN_CLIENT_LIST", mixinClients)
                    .replace("\"JAVA_21\"", "\"JAVA_17\"")
                    .replace("\"mixins\": [],", "\"mixins\": [],\n  \"refmap\": \"$modId.refmap.json\",")
            }
        }
    }

    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds the mod jar and collects it into build/libs/<mod version>/"
        from(named<Jar>("reobfJar").flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
    }
}
