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

    public static final DeferredItem<TractorAttachmentItem> HARVESTER_ATTACHMENT = ITEMS.registerItem(
            "harvester_attachment",
            TractorAttachmentItem::new,
            properties -> properties.stacksTo(1)
    );

    public static final DeferredItem<TractorAttachmentItem> TILLER_ATTACHMENT = ITEMS.registerItem(
            "tiller_attachment",
            TractorAttachmentItem::new,
            properties -> properties.stacksTo(1)
    );

    public static final DeferredItem<TractorAttachmentItem> PLANTER_ATTACHMENT = ITEMS.registerItem(
            "planter_attachment",
            TractorAttachmentItem::new,
            properties -> properties.stacksTo(1)
    );

    private ModItems() {}
}
