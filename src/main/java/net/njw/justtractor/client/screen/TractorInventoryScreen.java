package net.njw.justtractor.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.njw.justtractor.menu.TractorInventoryMenu;

public final class TractorInventoryScreen extends AbstractContainerScreen<TractorInventoryMenu> {
    private static final int CARGO_X = 8;
    private static final int CARGO_Y = 32;
    private static final int ATTACHMENT_X = 199;
    private static final int ATTACHMENT_Y = 50;
    private static final int PLAYER_X = 8;
    private static final int PLAYER_Y = 140;

    public TractorInventoryScreen(TractorInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, 250, 222);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);

        graphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xE0202020);
        graphics.outline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF808080);

        graphics.fill(this.leftPos + 6, this.topPos + 27, this.leftPos + 172, this.topPos + 88, 0xFF303030);
        graphics.outline(this.leftPos + 6, this.topPos + 27, 166, 61, 0xFF606060);

        graphics.fill(this.leftPos + 180, this.topPos + 27, this.leftPos + 242, this.topPos + 88, 0xFF303030);
        graphics.outline(this.leftPos + 180, this.topPos + 27, 62, 61, 0xFF606060);

        graphics.fill(this.leftPos + 6, this.topPos + 133, this.leftPos + 172, this.topPos + 218, 0xFF303030);
        graphics.outline(this.leftPos + 6, this.topPos + 133, 166, 85, 0xFF606060);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlot(graphics, CARGO_X + column * 18, CARGO_Y + row * 18);
                drawSlot(graphics, PLAYER_X + column * 18, PLAYER_Y + row * 18);
            }
        }

        drawSlot(graphics, ATTACHMENT_X, ATTACHMENT_Y);

        for (int column = 0; column < 9; column++) {
            drawSlot(graphics, PLAYER_X + column * 18, PLAYER_Y + 58);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.centeredText(this.font, this.title, this.imageWidth / 2, 10, 0xFFFFFFFF);
        graphics.centeredText(this.font, Component.translatable("container.njw_just_tractor.attachment"), 208, 34, 0xFFFFFFFF);
        graphics.text(this.font, this.playerInventoryTitle, 8, 122, 0xFFFFFFFF);
    }

    private void drawSlot(GuiGraphicsExtractor graphics, int x, int y) {
        int left = this.leftPos + x - 1;
        int top = this.topPos + y - 1;
        graphics.fill(left, top, left + 18, top + 18, 0xFF404040);
        graphics.outline(left, top, 18, 18, 0xFF808080);
    }
}
