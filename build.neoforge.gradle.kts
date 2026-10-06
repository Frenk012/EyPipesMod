plugins {
    id("net.neoforged.moddev")
    id("neoforge-mutex")
}

val mc = sc.current.version
val modId = property("mod.id") as String

version = "${property("mod.version")}+$mc"
group = property("mod.group") as String
base.archivesName = modId

// Datagen output is version-specific (model and recipe formats change across the range),
// so each Minecraft version keeps its own generated tree, committed alongside the sources.
val generatedResources: File = rootProject.file("src/generated/${sc.current.project}")

// Each version needs its own game directory: the dev mods folder holds builds of Curios,
// JEI and the rest that only load on one Minecraft version.
val runDirectory: File = rootProject.file("run/${sc.current.project}")

sourceSets.named("main") {
    resources.srcDir(generatedResources)
    // Forge 1.20.1's metadata; this build ships neoforge.mods.toml
    resources.exclude("META-INF/mods.toml")

    // EMI and Epic Fight have no release past 1.21.1, so their integrations cannot even
    // compile on later versions. They are dropped from those jars entirely rather than
    // guarded at runtime.
    if (mc != "1.21.1") {
        java.exclude(
            "frenk/eypipes/integration/emi/**",
            // EpicFightCompat itself stays: common code calls it, and Stonecutter empties
            // its bodies here. Only the class that touches the Epic Fight API is dropped.
            "frenk/eypipes/integration/epicfight/EpicFightAnimations.java",
            // NeoForge deleted BlockStateProvider and ItemModelProvider; ModModelProvider
            // replaces both against Minecraft's own model generators.
            "frenk/eypipes/datagen/ModBlockStateProvider.java",
            "frenk/eypipes/datagen/ModItemModelProvider.java",
        )
    }

    // From 1.21.2 the client is no longer sent whole recipes, only RecipePropertySet and the
    // stonecutter list, so a recipe viewer cannot enumerate this mod's drying, fermenting and
    // cutting board recipes the way it used to. Restoring them needs either RecipeDisplay
    // support on the recipes or a sync packet of our own - a design decision, not a port - so
    // the JEI and REI integrations are left out of this target until that is made.
    if (mc == "1.21.1" || mc == "1.21.5") {
        // These carry the smoking flag onto a render state that does not exist before 1.21.9.
        java.exclude(
            "frenk/eypipes/mixin/client/HumanoidRenderStateMixin.java",
            "frenk/eypipes/mixin/client/LivingEntityRendererMixin.java",
        )
    }

    if (mc != "1.21.1" && mc != "1.21.5") {
        java.exclude(
            "frenk/eypipes/integration/jei/**",
            "frenk/eypipes/integration/rei/**",
        )
        // The service file names the REI plugin class, which is no longer compiled here.
        resources.exclude("META-INF/services/me.shedaniel.rei.api.client.plugins.REIClientPlugin")
    } else {
        java.exclude("frenk/eypipes/datagen/ModModelProvider.java")
    }
}

java {
    withSourcesJar()
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
    toolchain.languageVersion = JavaLanguageVersion.of(21)
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
    maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
    maven("https://cursemaven.com") {
        name = "CurseMaven"
        content { includeGroup("curse.maven") }
    }
    maven("https://maven.blamejared.com") {
        name = "JEI"
        content { includeGroup("mezz.jei") }
    }
}

// Dependencies present at dev runtime but not compiled against
val localRuntime = configurations.create("localRuntime")
configurations.named("runtimeClasspath") { extendsFrom(localRuntime) }

dependencies {
    // GeckoLib and JEI encode the Minecraft version in the artifact id
    implementation("software.bernie.geckolib:geckolib-neoforge-$mc:${property("deps.geckolib")}")

    compileOnly("top.theillusivec4.curios:curios-neoforge:${property("deps.curios")}:api")
    localRuntime("top.theillusivec4.curios:curios-neoforge:${property("deps.curios")}")

    compileOnly("me.shedaniel:RoughlyEnoughItems-api-neoforge:${property("deps.rei")}")
    compileOnly("me.shedaniel:RoughlyEnoughItems-neoforge:${property("deps.rei")}")

    compileOnly("mezz.jei:jei-$mc-common-api:${property("deps.jei")}")
    compileOnly("mezz.jei:jei-$mc-neoforge-api:${property("deps.jei")}")

    // Neither EMI nor Epic Fight ever released past 1.21.1, so their integrations exist
    // only in that jar. Epic Fight is a local jar dropped into libs/ for development.
    if (mc == "1.21.1") {
        compileOnly("curse.maven:emi-580555:${property("deps.emi_curse_file")}")
        compileOnly(fileTree("../../libs") { include("epicfight*.jar") })
    }
}

neoForge {
    version = property("deps.neo_loader") as String

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
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("server") {
            server()
            gameDirectory = runDirectory
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        // GatherDataEvent was split in two at 1.21.2 and so was the run that fires it.
        val dataArguments = listOf(
            "--mod", modId,
            "--all",
            "--output", generatedResources.absolutePath,
            "--existing", rootProject.file("src/main/resources").absolutePath,
        )

        if (mc == "1.21.1") {
            register("data") {
                data()
                gameDirectory = runDirectory
                programArguments.addAll(dataArguments)
            }
        } else {
            register("clientData") {
                clientData()
                gameDirectory = runDirectory
                programArguments.addAll(dataArguments)
            }

            // No serverData run: every provider is registered on the client half, because
            // both halves write to the same directory and each prunes what it did not
            // generate. A server run here would delete the client run's output.
        }
    }
}

tasks {
    processResources {
        // The datagen hash cache lives inside the generated tree but must not ship
        exclude(".cache/**")

        // Note: do not name this `put`. MutableMap.put(String, String) is an exact member
        // match and would silently win over the extension, storing the property name as
        // the value and expanding placeholders to nonsense.
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
            register("neo_version_range", "deps.neo_range")
            register("loader_version_range", "deps.loader_range")
            register("geckolib_version_range", "deps.geckolib_range")
            register("curios_version_range", "deps.curios_range")
            register("rei_version_range", "deps.rei_range")
            register("jei_version_range", "deps.jei_range")
        }

        // Only declare the recipe viewers where their integrations are actually compiled in.
        // An optional dependency still carries its version range, so leaving them in would
        // block a player running an older JEI for an integration this jar does not have.
        val recipeViewerDependencies = if (mc == "1.21.1" || mc == "1.21.5") {
            listOf(
                "[[dependencies.$modId]]",
                "modId = \"roughlyenoughitems\"",
                "type = \"optional\"",
                "versionRange = \"" + sc.properties.get<String>("deps.rei_range") + "\"",
                "ordering = \"AFTER\"",
                "side = \"CLIENT\"",
                "",
                "[[dependencies.$modId]]",
                "modId = \"jei\"",
                "type = \"optional\"",
                "versionRange = \"" + sc.properties.get<String>("deps.jei_range") + "\"",
                "ordering = \"AFTER\"",
                "side = \"CLIENT\"",
            ).joinToString(System.lineSeparator())
        } else {
            ""
        }
        inputs.property("recipeViewerDependencies", recipeViewerDependencies)

        filesMatching("META-INF/neoforge.mods.toml") {
            filter { line -> line.replace("RECIPE_VIEWER_DEPENDENCIES", recipeViewerDependencies) }
            expand(props)
        }

        // GeckoLib 4 scans assets/<ns>/geo and assets/<ns>/animations; GeckoLib 5 scans
        // assets/<ns>/geckolib/models and assets/<ns>/geckolib/animations. The sources keep one
        // copy in the old layout and the newer jars get them relocated, rather than carrying the
        // same models twice in the repository.
        if (mc != "1.21.1" && mc != "1.21.5") {
            // Held as locals so the copy actions do not capture the build script itself,
            // which the configuration cache cannot serialize.
            val modelsFrom = "assets/$modId/geo/"
            val modelsTo = "assets/$modId/geckolib/models/"
            val animationsFrom = "assets/$modId/animations/"
            val animationsTo = "assets/$modId/geckolib/animations/"

            filesMatching("$modelsFrom**") { path = path.replace(modelsFrom, modelsTo) }
            filesMatching("$animationsFrom**") { path = path.replace(animationsFrom, animationsTo) }
        }

        // The humanoid mixin targets setupAnim(LivingEntity, ...), which 1.21.9 replaced with
        // a render-state overload carrying no ItemStack. Until the smoking flag is threaded
        // into that state, the mixin is left out of those jars rather than failing to apply.
        val mixinClients = if (mc == "1.21.1" || mc == "1.21.5") {
            "\"client.BipedModelMixin\""
        } else {
            "\"client.BipedModelMixin\", \"client.HumanoidRenderStateMixin\", \"client.LivingEntityRendererMixin\""
        }
        inputs.property("mixinClients", mixinClients)
        filesMatching("eypipes.mixins.json") {
            filter { line -> line.replace("MIXIN_CLIENT_LIST", mixinClients) }
        }
    }

    // ModDevGradle's task graph does not know about Stonecutter, so without this it can
    // build Minecraft artifacts before the version-specific sources have been generated.
    named("createMinecraftArtifacts") {
        dependsOn("stonecutterGenerate")
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        // While porting, seeing the whole error list beats javac's default first 100
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds the mod jar and collects it into build/libs/<mod version>/"
        from(jar.flatMap { it.archiveFile }, named<Jar>("sourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
    }
}
