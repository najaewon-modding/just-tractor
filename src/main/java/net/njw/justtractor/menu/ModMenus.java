package net.njw.justtractor.menu;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justtractor.JustTractor;

import java.util.function.Supplier;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(Registries.MENU, JustTractor.MODID);
    public static final Supplier<MenuType<TractorInventoryMenu>> TRACTOR_INVENTORY = MENU_TYPES.register("tractor_inventory", () -> new MenuType<>(TractorInventoryMenu::new, FeatureFlags.DEFAULT_FLAGS));
    public static final Supplier<MenuType<TractorUpgradeMenu>> TRACTOR_UPGRADE = MENU_TYPES.register("tractor_upgrade", () -> new MenuType<>(TractorUpgradeMenu::new, FeatureFlags.DEFAULT_FLAGS));

    private ModMenus() {}
}
