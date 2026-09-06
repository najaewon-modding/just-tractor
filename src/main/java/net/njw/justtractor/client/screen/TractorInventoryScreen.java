package net.njw.justtractor.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.njw.justtractor.item.ModItems;
import net.njw.justtractor.menu.TractorInventoryMenu;

public final class TractorInventoryScreen extends AbstractContainerScreen<TractorInventoryMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final Component ATTACHMENT_TITLE = Component.translatable("container.njw_just_tractor.attachment");
    private static final int BASE_WIDTH = 176;
    private static final int TOP_HEIGHT = 71;
    private static final int BOTTOM_TEXTURE_Y = 126;
    private static final int BOTTOM_HEIGHT = 96;
    private static final int RIGHT_CAP_WIDTH = 4;
    private static final int STRETCH_SOURCE_X = 169;
    private static final int STRETCH_WIDTH = 3;
    private static final int STRETCH_REPEAT = 24;
    private static final int EXTRA_WIDTH = STRETCH_WIDTH * STRETCH_REPEAT;
    private static final int SLOT_TEXTURE_X = 7;
    private static final int SLOT_TEXTURE_Y = 17;
    private static final int SLOT_SIZE = 18;
    private static final int PANEL_X = 180;
    private static final int PANEL_Y = 17;
    private static final int PANEL_WIDTH = 60;
    private static final int PANEL_HEIGHT = 143;
    private static final int PANEL_PADDING = 6;
    private static final int DESCRIPTION_WIDTH = PANEL_WIDTH - PANEL_PADDING * 2;
    private static final int ATTACHMENT_SLOT_BACKGROUND_X = 200;
    private static final int ATTACHMENT_SLOT_BACKGROUND_Y = 30;

    public TractorInventoryScreen(TractorInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, BASE_WIDTH + EXTRA_WIDTH, TOP_HEIGHT + BOTTOM_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        blitExtendedSection(graphics, this.leftPos, this.topPos, 0, TOP_HEIGHT);
        blitExtendedSection(graphics, this.leftPos, this.topPos + TOP_HEIGHT, BOTTOM_TEXTURE_Y, BOTTOM_HEIGHT);
        drawAttachmentPanel(graphics);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, 0xFF404040, false);
        graphics.text(this.font, this.playerInventoryTitle, 8, 75, 0xFF404040, false);
        int attachmentTitleX = PANEL_X + (PANEL_WIDTH - this.font.width(ATTACHMENT_TITLE)) / 2;
        graphics.text(this.font, ATTACHMENT_TITLE, attachmentTitleX, 6, 0xFF404040, false);
        drawAttachmentDescription(graphics);
    }

    private void blitExtendedSection(GuiGraphicsExtractor graphics, int x, int y, int textureY, int height) {
        int bodyWidth = BASE_WIDTH - RIGHT_CAP_WIDTH;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x, y, 0, textureY, bodyWidth, height, 256, 256);

        for (int i = 0; i < STRETCH_REPEAT; i++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x + bodyWidth + i * STRETCH_WIDTH, y, STRETCH_SOURCE_X, textureY, STRETCH_WIDTH, height, 256, 256);
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x + bodyWidth + EXTRA_WIDTH, y, BASE_WIDTH - RIGHT_CAP_WIDTH, textureY, RIGHT_CAP_WIDTH, height, 256, 256);
    }

    private void drawAttachmentPanel(GuiGraphicsExtractor graphics) {
        int left = this.leftPos + PANEL_X;
        int top = this.topPos + PANEL_Y;
        graphics.fill(left, top, left + PANEL_WIDTH, top + PANEL_HEIGHT, 0x10000000);
        graphics.outline(left, top, PANEL_WIDTH, PANEL_HEIGHT, 0xFF8B8B8B);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos + ATTACHMENT_SLOT_BACKGROUND_X, this.topPos + ATTACHMENT_SLOT_BACKGROUND_Y, SLOT_TEXTURE_X, SLOT_TEXTURE_Y, SLOT_SIZE, SLOT_SIZE, 256, 256);
        graphics.fill(left + PANEL_PADDING, top + 42, left + PANEL_WIDTH - PANEL_PADDING, top + 43, 0xFFB0B0B0);
        graphics.fill(left + PANEL_PADDING, top + PANEL_HEIGHT - 9, left + 14, top + PANEL_HEIGHT - 8, 0xFFB0B0B0);
        graphics.fill(left + PANEL_WIDTH - 14, top + PANEL_HEIGHT - 9, left + PANEL_WIDTH - PANEL_PADDING, top + PANEL_HEIGHT - 8, 0xFFB0B0B0);
    }

    private void drawAttachmentDescription(GuiGraphicsExtractor graphics) {
        ItemStack stack = this.menu.getSlot(TractorInventoryMenu.ATTACHMENT_SLOT).getItem();
        Component description = getAttachmentDescription(stack);
        if (description == null) return;
        int y = PANEL_Y + 50;
        for (var line : this.font.split(description, DESCRIPTION_WIDTH)) {
            graphics.text(this.font, line, PANEL_X + PANEL_PADDING, y, 0xFF606060, false);
            y += 10;
        }
    }

    private static Component getAttachmentDescription(ItemStack stack) {
        if (stack.getItem() == ModItems.HARVESTER_ATTACHMENT.get()) return Component.translatable("attachment.njw_just_tractor.harvester.description");
        if (stack.getItem() == ModItems.TILLER_ATTACHMENT.get()) return Component.translatable("attachment.njw_just_tractor.tiller.description");
        if (stack.getItem() == ModItems.PLANTER_ATTACHMENT.get()) return Component.translatable("attachment.njw_just_tractor.planter.description");
        return null;
    }
}
