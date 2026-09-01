package net.njw.justtractor.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.njw.justtractor.entity.TractorEntity;
import net.njw.justtractor.menu.TractorUpgradeCosts;
import net.njw.justtractor.menu.TractorUpgradeMenu;

public final class TractorUpgradeScreen extends AbstractContainerScreen<TractorUpgradeMenu> {
    private Button fortuneButton;
    private Button speedButton;

    public TractorUpgradeScreen(TractorUpgradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 240, 132);
    }

    @Override
    protected void init() {
        super.init();

        this.fortuneButton = this.addRenderableWidget(Button.builder(Component.translatable("upgrade.njw_just_tractor.button"), button -> sendButton(TractorUpgradeMenu.BUTTON_FORTUNE)).bounds(this.leftPos + 166, this.topPos + 43, 58, 20).build());
        this.speedButton = this.addRenderableWidget(Button.builder(Component.translatable("upgrade.njw_just_tractor.button"), button -> sendButton(TractorUpgradeMenu.BUTTON_SPEED)).bounds(this.leftPos + 166, this.topPos + 91, 58, 20).build());

        updateButtons();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtons();
    }

    private void sendButton(int buttonId) {
        if (this.minecraft != null && this.minecraft.gameMode != null) {
            this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, buttonId);
        }
    }

    private void updateButtons() {
        if (this.minecraft == null || this.minecraft.player == null) return;

        int fortuneLevel = this.menu.getFortuneLevel();
        int speedLevel = this.menu.getSpeedLevel();

        if (fortuneLevel >= TractorEntity.MAX_FORTUNE_LEVEL) {
            this.fortuneButton.active = false;
            this.fortuneButton.setMessage(Component.translatable("upgrade.njw_just_tractor.max"));
        } else {
            TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getFortuneCost(fortuneLevel + 1);
            this.fortuneButton.active = TractorUpgradeCosts.canAfford(this.minecraft.player, cost);
            this.fortuneButton.setMessage(Component.translatable("upgrade.njw_just_tractor.button"));
        }

        if (speedLevel >= TractorEntity.MAX_SPEED_LEVEL) {
            this.speedButton.active = false;
            this.speedButton.setMessage(Component.translatable("upgrade.njw_just_tractor.max"));
        } else {
            TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getSpeedCost(speedLevel + 1);
            this.speedButton.active = TractorUpgradeCosts.canAfford(this.minecraft.player, cost);
            this.speedButton.setMessage(Component.translatable("upgrade.njw_just_tractor.button"));
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xE0202020);
        graphics.outline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF808080);

        graphics.fill(this.leftPos + 12, this.topPos + 32, this.leftPos + 228, this.topPos + 73, 0xFF303030);
        graphics.outline(this.leftPos + 12, this.topPos + 32, 216, 41, 0xFF606060);

        graphics.fill(this.leftPos + 12, this.topPos + 80, this.leftPos + 228, this.topPos + 121, 0xFF303030);
        graphics.outline(this.leftPos + 12, this.topPos + 80, 216, 41, 0xFF606060);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.centeredText(this.font, this.title, this.imageWidth / 2, 12, 0xFFFFFFFF);

        drawFortune(graphics);
        drawSpeed(graphics);
    }

    private void drawFortune(GuiGraphicsExtractor graphics) {
        int level = this.menu.getFortuneLevel();

        graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.fortune"), 22, 40, 0xFFFFFFFF);
        graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.level", level, TractorEntity.MAX_FORTUNE_LEVEL), 22, 56, 0xFFB0B0B0);

        if (level >= TractorEntity.MAX_FORTUNE_LEVEL) {
            graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.maximum_level"), 88, 48, 0xFF80FF80);
            return;
        }

        TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getFortuneCost(level + 1);
        drawCost(graphics, cost, 88, 40);
    }

    private void drawSpeed(GuiGraphicsExtractor graphics) {
        int level = this.menu.getSpeedLevel();

        graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.speed"), 22, 88, 0xFFFFFFFF);
        graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.level", level, TractorEntity.MAX_SPEED_LEVEL), 22, 104, 0xFFB0B0B0);

        if (level >= TractorEntity.MAX_SPEED_LEVEL) {
            graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.maximum_level"), 88, 96, 0xFF80FF80);
            return;
        }

        TractorUpgradeCosts.Cost cost = TractorUpgradeCosts.getSpeedCost(level + 1);
        drawCost(graphics, cost, 88, 88);
    }

    private void drawCost(GuiGraphicsExtractor graphics, TractorUpgradeCosts.Cost cost, int x, int y) {
        if (this.minecraft == null || this.minecraft.player == null) return;

        ItemStack stack = new ItemStack(cost.item());
        int owned = TractorUpgradeCosts.count(this.minecraft.player, cost);
        boolean creative = this.minecraft.player.getAbilities().instabuild;
        boolean enough = creative || owned >= cost.count();

        graphics.item(stack, x, y);
        graphics.text(this.font, Component.literal("×" + cost.count()), x + 20, y, 0xFFFFFFFF);

        if (creative) {
            graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.creative"), x + 20, y + 12, 0xFF80FF80);
        } else {
            graphics.text(this.font, Component.translatable("upgrade.njw_just_tractor.amount", owned, cost.count()), x + 20, y + 12, enough ? 0xFF80FF80 : 0xFFFF6060);
        }
    }
}