package net.njw.justtractor;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.njw.justtractor.entity.ModEntities;
import org.slf4j.Logger;

@Mod(JustTractor.MODID)
public class JustTractor {
    public static final String MODID = "njw_just_tractor";
    public static final Logger LOGGER = LogUtils.getLogger();

    public JustTractor(IEventBus modEventBus, ModContainer modContainer) {
        ModEntities.ENTITY_TYPES.register(modEventBus);
    }
}