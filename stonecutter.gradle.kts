plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.143" apply false
    id("net.neoforged.moddev.legacyforge") version "2.0.143" apply false
    id("fabric-loom") version "1.17.21" apply false
}

stonecutter active "1.21.1-neoforge"

stonecutter parameters {
    val loader = current.project.substringAfterLast('-')

    // Makes loader-specific sections of stonecutter.properties.toml apply
    properties {
        tags(current.version, loader)
    }

    // `//? if forge {`, `//? if neoforge {` and `//? if fabric {` in the sources
    constants {
        match(loader, "fabric", "forge", "neoforge")
    }

    // Forge and NeoForge share most of their API under different package names. Renaming the
    // imports here keeps one copy of the code instead of an `if` around every import.
    replacements {
        string(loader == "forge") {
            // Every pair is a distinct prefix in both directions, so switching back is exact.
            replace("net.neoforged.api.distmarker.", "net.minecraftforge.api.distmarker.")
            replace("net.neoforged.bus.api.", "net.minecraftforge.eventbus.api.")
            replace("net.neoforged.fml.", "net.minecraftforge.fml.")
            replace("net.neoforged.neoforge.client.", "net.minecraftforge.client.")
            replace("net.neoforged.neoforge.common.", "net.minecraftforge.common.")
            replace("net.neoforged.neoforge.data.", "net.minecraftforge.data.")
            replace("net.neoforged.neoforge.event.", "net.minecraftforge.event.")
            replace("net.neoforged.neoforge.registries.", "net.minecraftforge.registries.")
            replace("ModConfigSpec", "ForgeConfigSpec")
        }

        // GeckoLib 4 moved its core classes out of the "core" package from the 1.20.5 builds on
        string(current.parsed < "1.20.5") {
            replace("software.bernie.geckolib.animatable.instance.", "software.bernie.geckolib.core.animatable.instance.")
            replace("software.bernie.geckolib.animatable.GeoAnimatable;", "software.bernie.geckolib.core.animatable.GeoAnimatable;")
            replace("software.bernie.geckolib.animation.", "software.bernie.geckolib.core.animation.")
        }

        // Signatures that only lost or changed a parameter at 1.20.5
        string(current.parsed < "1.20.5") {
            replace("TooltipContext context", "net.minecraft.world.level.Level context")
            replace("getUseDuration(ItemStack stack, LivingEntity entity)", "getUseDuration(ItemStack stack)")
        }

        // 1.21 made the ResourceLocation constructor private in favour of factory methods
        string(current.parsed < "1.21") {
            replace("ResourceLocation.fromNamespaceAndPath(", "new ResourceLocation(")
        }
    }
}
