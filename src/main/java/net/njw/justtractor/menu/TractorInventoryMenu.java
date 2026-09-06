package net.njw.justtractor.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.njw.justtractor.entity.TractorEntity;
import net.njw.justtractor.item.TractorAttachmentItem;

public final class TractorInventoryMenu extends AbstractContainerMenu {
    public static final int CARGO_SIZE = 27;
    public static final int ATTACHMENT_SIZE = 1;
    public static final int CARGO_SLOT_START = 0;
    public static final int ATTACHMENT_SLOT = CARGO_SIZE;
    public static final int TRACTOR_SLOT_COUNT = CARGO_SIZE + ATTACHMENT_SIZE;

    private final TractorEntity tractor;

    public TractorInventoryMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, new SimpleContainer(CARGO_SIZE), new SimpleContainer(ATTACHMENT_SIZE), null);
    }

    public TractorInventoryMenu(int containerId, Inventory playerInventory, TractorEntity tractor) {
        this(containerId, playerInventory, tractor.getInventory(), tractor.getAttachmentInventory(), tractor);
    }

    private TractorInventoryMenu(int containerId, Inventory playerInventory, Container cargo, Container attachment, TractorEntity tractor) {
        super(ModMenus.TRACTOR_INVENTORY.get(), containerId);
        this.tractor = tractor;
        checkContainerSize(cargo, CARGO_SIZE);
        checkContainerSize(attachment, ATTACHMENT_SIZE);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(cargo, column + row * 9, 8 + column * 18, 18 + row * 18));
            }
        }

        this.addSlot(new Slot(attachment, 0, 180, 18) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof TractorAttachmentItem;
            }
        });

        this.addStandardInventorySlots(playerInventory, 8, 85);
    }

    @Override
    public boolean stillValid(Player player) {
        return this.tractor == null || !this.tractor.isRemoved() && this.tractor.getControllingPassenger() == player;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();

        if (slotIndex < TRACTOR_SLOT_COUNT) {
            if (!this.moveItemStackTo(stack, TRACTOR_SLOT_COUNT, this.slots.size(), true)) return ItemStack.EMPTY;
        } else if (stack.getItem() instanceof TractorAttachmentItem) {
            if (!this.moveItemStackTo(stack, ATTACHMENT_SLOT, ATTACHMENT_SLOT + 1, false) && !this.moveItemStackTo(stack, CARGO_SLOT_START, CARGO_SIZE, false)) return ItemStack.EMPTY;
        } else if (!this.moveItemStackTo(stack, CARGO_SLOT_START, CARGO_SIZE, false)) {
            return ItemStack.EMPTY;
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }
}
