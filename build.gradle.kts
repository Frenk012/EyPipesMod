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
val generatedResources: File = rootProject.file("src/generated/$mc")

sourceSets.named("main") {
    resources.srcDir(generatedResources)

    // EMI and Epic Fight have no release past 1.21.1, so their integrations cannot even
    // compile on later versions. They are dropped from those jars entirely rather than
    // guarded at runtime.
    if (mc != "1.21.1") {
        java.exclude(
            "frenk/eypipes/integration/emi/**",
            // EpicFightCompat itself stays: common code calls it, and Stonecutter empties
            // its bodies here. Only the class that touches the Epic Fight API is dropped.
            "frenk/eypipes/integration/epicfight/EpicFightAnimations.java",
        )
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
            gameDirectory = file("../../run/")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("server") {
            server()
            gameDirectory = file("../../run/")
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
        }

        register("data") {
            data()
            gameDirectory = file("../../run/")
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", generatedResources.absolutePath,
                "--existing", rootProject.file("src/main/resources").absolutePath,
            )
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

        filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
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
        into(rootProject.layout.buildDirectory.dir("libs/${property("mod.version")}"))
    }
}
