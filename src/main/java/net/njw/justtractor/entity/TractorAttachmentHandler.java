package net.njw.justtractor.entity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.njw.justtractor.item.ModItems;

public final class TractorAttachmentHandler {
    private TractorAttachmentHandler() {}

    public static void apply(TractorEntity tractor, ServerLevel level) {
        if (!(tractor.getControllingPassenger() instanceof ServerPlayer player)) return;

        ItemStack attachment = tractor.getAttachmentInventory().getItem(0);
        if (attachment.isEmpty()) return;

        if (attachment.getItem() == ModItems.HARVESTER_ATTACHMENT.get()) {
            TractorHarvester.harvestCrops(tractor, level);
        } else if (attachment.getItem() == ModItems.TILLER_ATTACHMENT.get()) {
            TractorTiller.till(tractor, level, player);
        } else if (attachment.getItem() == ModItems.PLANTER_ATTACHMENT.get()) {
            TractorPlanter.plant(tractor, level, player);
        }
    }
}