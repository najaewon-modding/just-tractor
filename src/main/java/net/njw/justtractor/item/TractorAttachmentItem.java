package net.njw.justtractor.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class TractorAttachmentItem extends Item {
    public TractorAttachmentItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
