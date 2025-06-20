package frenk.eypipes;

import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EyPipesTags {
    public static class Items {
        public static final TagKey<Item> ERBAPIPA = createCommonTag("crops/erbapipa");
        public static final TagKey<Item> ERBAPIPA_SEEDS = createCommonTag("seeds/erbapipa");
        public static final TagKey<Item> ERBAPIPA_DRIED = createCommonTag("erbapipa_dried");

        private static TagKey<Item> createCommonTag(String path) {
            return TagKey.of(Registry.ITEM_KEY, new Identifier("c", path));
        }
    }
}
