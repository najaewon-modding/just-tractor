package net.njw.justtractor.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;

public final class TractorTiller {
    private static final double HORIZONTAL_INSET = 0.1;
    private static final double GROUND_Y_OFFSET = -0.01;

    private TractorTiller() {}

    public static void till(TractorEntity tractor, ServerLevel level, ServerPlayer player) {
        AABB box = tractor.getBoundingBox();
        int minX = Mth.floor(box.minX + HORIZONTAL_INSET);
        int maxX = Mth.floor(box.maxX - HORIZONTAL_INSET);
        int y = Mth.floor(box.minY + GROUND_Y_OFFSET);
        int minZ = Mth.floor(box.minZ + HORIZONTAL_INSET);
        int maxZ = Mth.floor(box.maxZ - HORIZONTAL_INSET);
        ItemStack tool = new ItemStack(Items.IRON_HOE);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                BlockPos pos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(pos);
                Vec3 hitLocation = new Vec3(x + 0.5, y + 1.0, z + 0.5);
                BlockHitResult hitResult = new BlockHitResult(hitLocation, Direction.UP, pos, false);
                UseOnContext context = new AttachmentUseOnContext(level, player, tool, hitResult);
                BlockState tilledState = state.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);

                if (tilledState != null && tilledState != state) {
                    level.setBlock(pos, tilledState, Block.UPDATE_ALL);
                }
            }
        }
    }

    private static final class AttachmentUseOnContext extends UseOnContext {
        private AttachmentUseOnContext(Level level, ServerPlayer player, ItemStack stack, BlockHitResult hitResult) {
            super(level, player, InteractionHand.MAIN_HAND, stack, hitResult);
        }
    }
}