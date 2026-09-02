package net.njw.justtractor.item;

import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justtractor.JustTractor;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(JustTractor.MODID);

    public static final DeferredItem<TractorItem> TRACTOR = ITEMS.registerItem(
            "tractor",
            TractorItem::new,
            properties -> properties.stacksTo(1)
    );

    private ModItems() {}
}