package net.njw.justtractor.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class TractorHarvester {
    private static final double HORIZONTAL_INSET = 0.1;
    private static final double MIN_Y_OFFSET = -0.25;
    private static final double MAX_Y_OFFSET = 0.5;

    private TractorHarvester() {}

    public static void harvest(TractorEntity tractor, ServerLevel level) {
        AABB box = tractor.getBoundingBox();
        ItemStack harvestTool = createHarvestTool(tractor, level);

        int minX = Mth.floor(box.minX + HORIZONTAL_INSET);
        int maxX = Mth.floor(box.maxX - HORIZONTAL_INSET);
        int minY = Mth.floor(box.minY + MIN_Y_OFFSET);
        int maxY = Mth.floor(box.minY + MAX_Y_OFFSET);
        int minZ = Mth.floor(box.minZ + HORIZONTAL_INSET);
        int maxZ = Mth.floor(box.maxZ - HORIZONTAL_INSET);

        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                for (int z = minZ; z <= maxZ; z++) {
                    harvestAt(tractor, level, new BlockPos(x, y, z), harvestTool);
                }
            }
        }
    }

    private static ItemStack createHarvestTool(TractorEntity tractor, ServerLevel level) {
        int fortuneLevel = tractor.getFortuneLevel();

        if (fortuneLevel <= 0) return ItemStack.EMPTY;

        ItemStack tool = new ItemStack(Items.DIAMOND_HOE);
        tool.enchant(level.registryAccess().getOrThrow(Enchantments.FORTUNE), fortuneLevel);
        return tool;
    }

    private static void harvestAt(TractorEntity tractor, ServerLevel level, BlockPos pos, ItemStack harvestTool) {
        BlockState state = level.getBlockState(pos);

        if (!(state.getBlock() instanceof CropBlock crop)) return;
        if (!crop.isMaxAge(state)) return;

        List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), tractor, harvestTool);

        if (!level.destroyBlock(pos, false, tractor)) return;

        tryReplant(level, pos, crop, drops);

        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;

            ItemStack remainder = tractor.addCargo(drop);

            if (!remainder.isEmpty()) {
                Block.popResource(level, pos, remainder);
            }
        }
    }

    private static boolean tryReplant(ServerLevel level, BlockPos cropPos, CropBlock crop, List<ItemStack> drops) {
        for (ItemStack drop : drops) {
            if (drop.isEmpty()) continue;
            if (!(drop.getItem() instanceof BlockItem blockItem)) continue;
            if (blockItem.getBlock() != crop) continue;

            ItemStack plantingStack = drop.copyWithCount(1);
            BlockPos soilPos = cropPos.below();
            Vec3 hitLocation = new Vec3(cropPos.getX() + 0.5, cropPos.getY(), cropPos.getZ() + 0.5);
            BlockHitResult hitResult = new BlockHitResult(hitLocation, Direction.UP, soilPos, false);
            BlockPlaceContext context = new BlockPlaceContext(level, null, InteractionHand.MAIN_HAND, plantingStack, hitResult);
            InteractionResult result = blockItem.place(context);

            if (!result.consumesAction()) continue;
            if (level.getBlockState(cropPos).getBlock() != crop) continue;

            drop.shrink(1);
            return true;
        }

        return false;
    }
}