package frenk.eypipes.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Knife item used for cutting dried herbs on the Cutting Board.
 * Has durability and loses 1 durability per cut.
 */
public class KnifeItem extends Item {

    public KnifeItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
