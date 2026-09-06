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
    private static final int VANILLA_WIDTH = 176;
    private static final int CONTAINER_HEIGHT = 71;
    private static final int PLAYER_SECTION_HEIGHT = 96;
    private static final int EXTENSION_SOURCE_X = 148;
    private static final int EXTENSION_X = 176;
    private static final int EXTENSION_WIDTH = 28;
    private static final int EXTENSION_HEIGHT = 35;

    public TractorInventoryScreen(TractorInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, VANILLA_WIDTH + EXTENSION_WIDTH, 168);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos, this.topPos, 0, 0, VANILLA_WIDTH, CONTAINER_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos, this.topPos + CONTAINER_HEIGHT, 0, 126, VANILLA_WIDTH, PLAYER_SECTION_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos + EXTENSION_X, this.topPos, EXTENSION_SOURCE_X, 0, EXTENSION_WIDTH, EXTENSION_HEIGHT, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, this.leftPos + EXTENSION_X, this.topPos + EXTENSION_HEIGHT, EXTENSION_SOURCE_X, 70, EXTENSION_WIDTH, 1, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, this.title, 8, 6, 0xFF404040);
        graphics.text(this.font, this.playerInventoryTitle, 8, 75, 0xFF404040);
    }
}
