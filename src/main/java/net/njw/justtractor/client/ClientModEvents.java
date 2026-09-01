package net.njw.justtractor.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.njw.justtractor.JustTractor;
import net.njw.justtractor.client.model.TractorModel;
import net.njw.justtractor.client.renderer.TractorRenderer;
import net.njw.justtractor.entity.ModEntities;
import net.njw.justtractor.entity.TractorEntity;

@EventBusSubscriber(modid = JustTractor.MODID, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(TractorModel.LAYER_LOCATION, TractorModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.TRACTOR.get(), TractorRenderer::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        if (player == null) {
            return;
        }

        if (!(player.getVehicle() instanceof TractorEntity tractor)) {
            return;
        }

        if (tractor.getControllingPassenger() != player) {
            return;
        }

        tractor.controlFromClient(player.input.keyPresses);
    }
}