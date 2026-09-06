package net.njw.justtractor.client.screen;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.njw.justtractor.menu.TractorInventoryMenu;

public final class TractorInventoryScreen extends AbstractContainerScreen<TractorInventoryMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/generic_54.png");
    private static final int BASE_WIDTH = 176;
    private static final int TOP_HEIGHT = 71;
    private static final int BOTTOM_TEXTURE_Y = 126;
    private static final int BOTTOM_HEIGHT = 96;
    private static final int RIGHT_CAP_WIDTH = 4;
    private static final int STRETCH_SOURCE_X = 169;
    private static final int STRETCH_WIDTH = 3;
    private static final int STRETCH_REPEAT = 7;
    private static final int EXTRA_WIDTH = STRETCH_WIDTH * STRETCH_REPEAT;
    private static final int SLOT_TEXTURE_X = 7;
    private static final int SLOT_TEXTURE_Y = 17;
    private static final int SLOT_SIZE = 18;
    private static final int ATTACHMENT_SLOT_BACKGROUND_X = 174;
    private static final int ATTACHMENT_SLOT_BACKGROUND_Y = 26;

    public TractorInventoryScreen(TractorInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, BASE_WIDTH + EXTRA_WIDTH, TOP_HEIGHT + BOTTOM_HEIGHT);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        blitExtendedSection(graphics, this.leftPos, this.topPos, 0, TOP_HEIGHT);
        blitExtendedSection(graphics, this.leftPos, this.topPos + TOP_HEIGHT, BOTTOM_TEXTURE_Y, BOTTOM_HEIGHT);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos + ATTACHMENT_SLOT_BACKGROUND_X, this.topPos + ATTACHMENT_SLOT_BACKGROUND_Y, SLOT_TEXTURE_X, SLOT_TEXTURE_Y, SLOT_SIZE, SLOT_SIZE, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, 0xFF404040);
        graphics.text(this.font, this.playerInventoryTitle, 8, 75, 0xFF404040);
    }

    private void blitExtendedSection(GuiGraphicsExtractor graphics, int x, int y, int textureY, int height) {
        int bodyWidth = BASE_WIDTH - RIGHT_CAP_WIDTH;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x, y, 0, textureY, bodyWidth, height, 256, 256);

        for (int i = 0; i < STRETCH_REPEAT; i++) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x + bodyWidth + i * STRETCH_WIDTH, y, STRETCH_SOURCE_X, textureY, STRETCH_WIDTH, height, 256, 256);
        }

        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, x + bodyWidth + EXTRA_WIDTH, y, BASE_WIDTH - RIGHT_CAP_WIDTH, textureY, RIGHT_CAP_WIDTH, height, 256, 256);
    }
}
