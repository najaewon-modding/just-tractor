package net.njw.justtractor.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.njw.justtractor.entity.ModEntities;
import net.njw.justtractor.entity.TractorEntity;

public final class TractorItem extends Item {
    private static final String FORTUNE_LEVEL_TAG = "FortuneLevel";
    private static final String SPEED_LEVEL_TAG = "SpeedLevel";

    public TractorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getClickedFace() != Direction.UP) return InteractionResult.FAIL;

        Level level = context.getLevel();

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel)level;
        BlockPos spawnPos = context.getClickedPos().above();
        TractorEntity tractor = ModEntities.TRACTOR.get().create(serverLevel, EntitySpawnReason.SPAWN_ITEM_USE);

        if (tractor == null) return InteractionResult.FAIL;

        tractor.setPos(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);

        Player player = context.getPlayer();

        if (player != null) {
            tractor.setYRot(Mth.wrapDegrees(player.getYRot()));
        }

        applyUpgrades(context.getItemInHand(), tractor);

        if (!serverLevel.noCollision(tractor)) {
            return InteractionResult.FAIL;
        }

        if (!serverLevel.addFreshEntity(tractor)) {
            return InteractionResult.FAIL;
        }

        if (player == null || !player.isCreative()) {
            context.getItemInHand().shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    public static ItemStack createStack(int fortuneLevel, int speedLevel) {
        ItemStack stack = new ItemStack(ModItems.TRACTOR.get());

        if (fortuneLevel <= 0 && speedLevel <= 0) {
            return stack;
        }

        CompoundTag tag = new CompoundTag();

        if (fortuneLevel > 0) {
            tag.putInt(FORTUNE_LEVEL_TAG, fortuneLevel);
        }

        if (speedLevel > 0) {
            tag.putInt(SPEED_LEVEL_TAG, speedLevel);
        }

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    private static void applyUpgrades(ItemStack stack, TractorEntity tractor) {
        CustomData data = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag tag = data.copyTag();

        tractor.setFortuneLevel(tag.getIntOr(FORTUNE_LEVEL_TAG, 0));
        tractor.setSpeedLevel(tag.getIntOr(SPEED_LEVEL_TAG, 0));
    }
}