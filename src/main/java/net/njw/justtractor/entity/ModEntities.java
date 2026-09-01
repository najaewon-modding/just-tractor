package net.njw.justtractor.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.njw.justtractor.JustTractor;

import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(JustTractor.MODID);

    public static final Supplier<EntityType<TractorEntity>> TRACTOR = ENTITY_TYPES.registerEntityType(
            "tractor",
            TractorEntity::new,
            MobCategory.MISC,
            builder -> builder.sized(2.15F, 1.75F).clientTrackingRange(10).updateInterval(1)
    );

    private ModEntities() {
    }
}