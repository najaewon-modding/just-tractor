package net.njw.justtractor.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.njw.justtractor.entity.TractorEntity;

public final class TractorUpgradeMenu extends AbstractContainerMenu {
    public static final int BUTTON_FORTUNE = 0;
    public static final int BUTTON_SPEED = 1;

    private static final int DATA_FORTUNE = 0;
    private static final int DATA_SPEED = 1;
    private static final int DATA_COUNT = 2;

    private final TractorEntity tractor;
    private final ContainerData data;

    public TractorUpgradeMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, new SimpleContainerData(DATA_COUNT));
    }

    public TractorUpgradeMenu(int containerId, Inventory playerInventory, TractorEntity tractor) {
        this(containerId, playerInventory, tractor, createData(tractor));
    }

    private TractorUpgradeMenu(int containerId, Inventory playerInventory, TractorEntity tractor, ContainerData data) {
        super(ModMenus.TRACTOR_UPGRADE.get(), containerId);
        this.tractor = tractor;
        this.data = data;
        checkContainerDataCount(data, DATA_COUNT);
        this.addDataSlots(data);
    }

    private static ContainerData createData(TractorEntity tractor) {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_FORTUNE -> tractor.getFortuneLevel();
                    case DATA_SPEED -> tractor.getSpeedLevel();
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case DATA_FORTUNE -> tractor.setFortuneLevel(value);
                    case DATA_SPEED -> tractor.setSpeedLevel(value);
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
            }
        };
    }

    public int getFortuneLevel() {
        return this.data.get(DATA_FORTUNE);
    }

    public int getSpeedLevel() {
        return this.data.get(DATA_SPEED);
    }

    @Override
    public boolean clickMenuButton(Player player, int buttonId) {
        if (this.tractor == null || !this.stillValid(player)) return false;

        if (buttonId == BUTTON_FORTUNE) {
            int currentLevel = this.tractor.getFortuneLevel();

            if (currentLevel >= TractorEntity.MAX_FORTUNE_LEVEL) return false;

            TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getFortuneCost(currentLevel + 1);

            if (!TractorUpgradeCosts.consume(player, cost)) return false;

            this.tractor.setFortuneLevel(currentLevel + 1);
            this.broadcastChanges();
            return true;
        }

        if (buttonId == BUTTON_SPEED) {
            int currentLevel = this.tractor.getSpeedLevel();

            if (currentLevel >= TractorEntity.MAX_SPEED_LEVEL) return false;

            TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getSpeedCost(currentLevel + 1);

            if (!TractorUpgradeCosts.consume(player, cost)) return false;

            this.tractor.setSpeedLevel(currentLevel + 1);
            this.broadcastChanges();
            return true;
        }

        return false;
    }

    @Override
    public boolean stillValid(Player player) {
        if (this.tractor == null) return true;
        if (this.tractor.isRemoved() || this.tractor.isVehicle() || player.isPassenger()) return false;
        return player.distanceToSqr(this.tractor.getX(), this.tractor.getY(), this.tractor.getZ()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }
}