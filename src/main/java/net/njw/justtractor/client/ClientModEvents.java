package net.njw.justtractor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent;
import net.njw.justtractor.JustTractor;
import net.njw.justtractor.client.model.TractorModel;
import net.njw.justtractor.client.renderer.TractorItemSpecialRenderer;
import net.njw.justtractor.client.renderer.TractorRenderer;
import net.njw.justtractor.client.screen.TractorInventoryScreen;
import net.njw.justtractor.client.screen.TractorUpgradeScreen;
import net.njw.justtractor.entity.ModEntities;
import net.njw.justtractor.entity.TractorEntity;
import net.njw.justtractor.menu.ModMenus;

@EventBusSubscriber(modid = JustTractor.MODID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {}

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TractorModel.LAYER_LOCATION, TractorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TRACTOR.get(), TractorRenderer::new);
    }

    @SubscribeEvent
    public static void registerSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(JustTractor.MODID, "tractor"), TractorItemSpecialRenderer.Unbaked.MAP_CODEC);
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.TRACTOR_INVENTORY.get(), TractorInventoryScreen::new);
        event.register(ModMenus.TRACTOR_UPGRADE.get(), TractorUpgradeScreen::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) return;
        if (!(player.getVehicle() instanceof TractorEntity tractor)) return;
        if (tractor.getControllingPassenger() != player) return;

        tractor.controlFromClient(player.input.keyPresses);
    }
}
