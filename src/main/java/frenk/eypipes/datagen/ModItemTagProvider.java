package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

/**
 * Item tag provider for EyPipes items.
 * Generates custom tags for mod items.
 */
public class ModItemTagProvider extends ItemTagsProvider {

    // Custom mod tags
    public static final TagKey<Item> ERBAPIPA = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "erbapipa"));
    public static final TagKey<Item> SMOKING_ITEMS = ItemTags.create(
            ResourceLocation.fromNamespaceAndPath(EyPipes.MOD_ID, "smoking_items"));

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, EyPipes.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Custom erbapipa tag
        tag(ERBAPIPA)
                .add(ModItems.ERBAPIPA.get())
                .add(ModItems.ERBAPIPA_DRIED.get())
                .add(ModItems.ERBAPIPA_CUTTED.get());

        // Smoking items tag
        tag(SMOKING_ITEMS)
                .add(ModItems.PIPE.get())
                .add(ModItems.CIGAR.get());

        // Seeds for villager trading
        tag(ItemTags.VILLAGER_PLANTABLE_SEEDS)
                .add(ModItems.ERBAPIPA_SEEDS.get());
    }
}
