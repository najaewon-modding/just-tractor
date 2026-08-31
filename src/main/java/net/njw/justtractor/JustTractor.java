package net.njw.justtractor;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;

@Mod(JustTractor.MODID)
public class JustTractor {
    public static final String MODID = "njw_just_tractor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustTractor(IEventBus modEventBus, ModContainer modContainer) {
    }
}
