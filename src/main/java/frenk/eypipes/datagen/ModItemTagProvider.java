package frenk.eypipes.datagen;

import frenk.eypipes.EyPipes;
import frenk.eypipes.registries.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
//? if <1.21.2 {
import net.minecraft.data.tags.ItemTagsProvider;
//?} else
/*import net.neoforged.neoforge.common.data.ItemTagsProvider;*/
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
//? if <1.21.2
import net.minecraft.world.level.block.Block;
//? if <1.21.2
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

    // These tags are all built by hand, none are copied from block tags, so the newer provider
    // needs neither the block-tag lookup nor the deleted ExistingFileHelper.
    //? if <1.21.2 {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, EyPipes.MOD_ID, existingFileHelper);
    }
    //?} else {
    /*public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, EyPipes.MOD_ID);
    }
    *///?}

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
