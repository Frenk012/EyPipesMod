plugins {
    id("fabric-loom")
}

// Fabric through Loom with Mojang mappings and Parchment names, like the (Neo)Forge builds,
// so the shared sources need no mapping-specific code.

val mc = sc.current.version
val modId = property("mod.id") as String

version = "${property("mod.version")}+$mc-fabric"
group = property("mod.group") as String
base.archivesName = modId

val javaVersion = if (sc.current.parsed >= "1.20.5") JavaVersion.VERSION_21 else JavaVersion.VERSION_17

// Fabric reuses the data the NeoForge build of the same Minecraft version generates (1.20.1
// gets the converted copy), so there is no Fabric datagen to keep in step.
val generatedResources: File = rootProject.file("src/generated/$mc")

sourceSets.named("main") {
    resources.srcDir(generatedResources)

    java.exclude(
        // Epic Fight is Forge/NeoForge only. EpicFightCompat stays as the no-op seam.
        "frenk/eypipes/integration/epicfight/EpicFightAnimations.java",
        // (Neo)Forge event subscribers; EyPipesFabric and EyPipesFabricClient replace them
        "frenk/eypipes/EyPipesServerEvents.java",
        "frenk/eypipes/EyPipesClient.java",
        "frenk/eypipes/EyPipesClientEvents.java",
        // Curios is Forge/NeoForge only; Trinkets takes its place (client/trinkets)
        "frenk/eypipes/client/curios/**",
        // Data generators use the (Neo)Forge data APIs and run on the NeoForge build
        "frenk/eypipes/datagen/**",
        // 1.21.9+ only: these carry the smoking flag onto the humanoid render state
        "frenk/eypipes/mixin/client/HumanoidRenderStateMixin.java",
        "frenk/eypipes/mixin/client/LivingEntityRendererMixin.java",
    )
    resources.exclude(
        "META-INF/mods.toml",
        "META-INF/neoforge.mods.toml",
        "META-INF/services/**",
        "pack.mcmeta",
        "data/curios/**",
    )
}

java {
    withSourcesJar()
    targetCompatibility = javaVersion
    sourceCompatibility = javaVersion
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion.majorVersion)
}

repositories {
    mavenLocal()
    maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/") {
        name = "GeckoLib"
        content {
            includeGroup("software.bernie.geckolib")
            // Molang library GeckoLib 4 on 1.20.1 depends on
            includeGroup("com.eliotlash.mclib")
        }
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
        content { includeGroup("dev.emi") }
    }
    maven("https://maven.ladysnake.org/releases") {
        name = "Ladysnake"
        content {
            includeGroupByRegex("org\\.ladysnake.*")
            // Cardinal Components 5.x, which Trinkets 3.7 pulls in on 1.20.1
            includeGroupByRegex("dev\\.onyxstudios.*")
        }
    }
    maven("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/") {
        name = "Fuzs"
        content { includeGroup("fuzs.forgeconfigapiport") }
    }
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
        content { includeGroup("maven.modrinth") }
    }
    maven("https://maven.shedaniel.me/") { name = "REI" }
    maven("https://maven.architectury.dev/") { name = "Architectury" }
    maven("https://maven.blamejared.com") {
        name = "JEI"
        content { includeGroup("mezz.jei") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$mc")
    @Suppress("UnstableApiUsage")
    mappings(loom.layered {
        officialMojangMappings()
        parchment("org.parchmentmc.data:parchment-$mc:${property("deps.parchment")}@zip")
    })

    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")

    modImplementation("software.bernie.geckolib:geckolib-fabric-$mc:${property("deps.geckolib")}")
    if (sc.current.parsed >= "1.20.5") {
        modImplementation("dev.emi:trinkets:${property("deps.trinkets")}")
    } else {
        // The 3.7.2 jar on the Terraformers maven is not the released one: its mixins keep
        // unmapped names and fail to apply. Modrinth serves the jar players actually run, but
        // without a POM, so the Cardinal Components it bundles are listed by hand.
        modImplementation("maven.modrinth:trinkets:${property("deps.trinkets")}")
        modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-base:${property("deps.cardinal_components")}")
        modImplementation("dev.onyxstudios.cardinal-components-api:cardinal-components-entity:${property("deps.cardinal_components")}")
    }
    // (Neo)Forge's config API on Fabric, so EyPipesConfig is shared as is
    modImplementation("fuzs.forgeconfigapiport:forgeconfigapiport-fabric:${property("deps.forgeconfigapiport")}")

    modCompileOnly("me.shedaniel:RoughlyEnoughItems-api-fabric:${property("deps.rei")}")
    modCompileOnly("me.shedaniel:RoughlyEnoughItems-fabric:${property("deps.rei")}")
    modCompileOnly("mezz.jei:jei-$mc-common-api:${property("deps.jei")}")
    modCompileOnly("mezz.jei:jei-$mc-fabric-api:${property("deps.jei")}")
    modCompileOnly("dev.emi:emi-fabric:${property("deps.emi")}:api")
}

loom {
    runConfigs.all {
        runDir = "../../run/${sc.current.project}"
        ideConfigGenerated(true)
    }
    runConfigs.named("server") {
        programArgs("--nogui")
    }
}

tasks {
    processResources {
        // The datagen hash cache lives inside the generated tree but must not ship
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
            register("minecraft_dependency", "deps.minecraft_dependency")
            register("fabric_loader_dependency", "deps.fabric_loader_dependency")
            register("geckolib_dependency", "deps.geckolib_dependency")
            register("trinkets_dependency", "deps.trinkets_dependency")
            register("forgeconfigapiport_dependency", "deps.forgeconfigapiport_dependency")
        }
        val javaDependency = ">=${javaVersion.majorVersion}"
        inputs.property("java_dependency", javaDependency)

        // Hand-written tags use the 1.21 folder name; 1.20.1 still reads the plural one
        if (sc.current.parsed < "1.21") {
            filesMatching("data/*/tags/item/**") { path = path.replace("/tags/item/", "/tags/items/") }
        }

        filesMatching("fabric.mod.json") { expand(props + ("java_dependency" to javaDependency)) }

        // Held as locals so the filter does not capture the build script itself, which the
        // configuration cache cannot serialize.
        val mixinClients = "\"client.BipedModelMixin\""
        val mixinJava = "\"JAVA_${javaVersion.majorVersion}\""
        inputs.property("mixinClients", mixinClients)
        inputs.property("mixinJava", mixinJava)
        filesMatching("eypipes.mixins.json") {
            filter { line ->
                line.replace("MIXIN_CLIENT_LIST", mixinClients).replace("\"JAVA_21\"", mixinJava)
            }
        }
    }

    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xmaxerrs", "2000"))
    }

    register<Copy>("buildAndCollect") {
        group = "build"
        description = "Builds the mod jar and collects it into build/libs/<mod version>/"
        from(named<Jar>("remapJar").flatMap { it.archiveFile }, named<Jar>("remapSourcesJar").flatMap { it.archiveFile })
        into(rootProject.layout.buildDirectory.dir("libs/${project.property("mod.version")}"))
    }
}
