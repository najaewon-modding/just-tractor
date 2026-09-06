package net.njw.justtractor.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.CommonHooks;

public final class TractorPlanter {
    private static final double HORIZONTAL_INSET = 0.1;
    private static final double CROP_Y_OFFSET = 0.1;

    private TractorPlanter() {}

    public static void plant(TractorEntity tractor, ServerLevel level, ServerPlayer player) {
        AABB box = tractor.getBoundingBox();
        int minX = Mth.floor(box.minX + HORIZONTAL_INSET);
        int maxX = Mth.floor(box.maxX - HORIZONTAL_INSET);
        int y = Mth.floor(box.minY + CROP_Y_OFFSET);
        int minZ = Mth.floor(box.minZ + HORIZONTAL_INSET);
        int maxZ = Mth.floor(box.maxZ - HORIZONTAL_INSET);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos cropPos = new BlockPos(x, y, z);
                BlockPos soilPos = cropPos.below();
                if (!level.getBlockState(cropPos).isAir()) continue;
                if (!level.getBlockState(soilPos).is(Blocks.FARMLAND)) continue;
                if (tryPlant(level, player, InteractionHand.MAIN_HAND, cropPos, soilPos)) continue;
                tryPlant(level, player, InteractionHand.OFF_HAND, cropPos, soilPos);
            }
        }
    }

    private static boolean tryPlant(ServerLevel level, ServerPlayer player, InteractionHand hand, BlockPos cropPos, BlockPos soilPos) {
        if (!level.getBlockState(cropPos).isAir()) return false;

        ItemStack stack = player.getItemInHand(hand);
        if (stack.isEmpty()) return false;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return false;
        if (!(blockItem.getBlock() instanceof CropBlock)) return false;

        Vec3 hitLocation = new Vec3(cropPos.getX() + 0.5, cropPos.getY(), cropPos.getZ() + 0.5);
        BlockHitResult hitResult = new BlockHitResult(hitLocation, Direction.UP, soilPos, false);
        InteractionResult result = CommonHooks.onPlaceItemIntoWorld(new UseOnContext(player, hand, hitResult));
        return result.consumesAction() && level.getBlockState(cropPos).getBlock() == blockItem.getBlock();
    }
}