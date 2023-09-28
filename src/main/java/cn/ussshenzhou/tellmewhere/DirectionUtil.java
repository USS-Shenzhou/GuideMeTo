package cn.ussshenzhou.tellmewhere;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import javax.annotation.Nullable;

/**
 * @author USS_Shenzhou
 */
public class DirectionUtil {

    public static BlockPos getPosAtRight(BlockPos pos, BlockState state) {
        try {
            return getPosAtRightDirect(pos, state);
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return BlockPos.ZERO;
        }
    }

    public static BlockPos getPosAtRightDirect(BlockPos pos, BlockState state) throws IllegalArgumentException {
        return getPosAtRight(pos, state.getValue(BlockStateProperties.FACING));
    }

    public static BlockPos getPosAtRight(BlockPos pos, Direction origionalDirection) {
        return pos.relative(origionalDirection.getClockWise());
    }

    public static BlockPos getPosAtLeft(BlockPos pos, BlockState state) {
        try {
            return getPosAtLeftDirect(pos, state);
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return BlockPos.ZERO;
        }
    }

    public static BlockPos getPosAtLeftDirect(BlockPos pos, BlockState state) throws IllegalArgumentException {
        return getPosAtLeft(pos, state.getValue(BlockStateProperties.FACING));
    }

    public static BlockPos getPosAtLeft(BlockPos pos, Direction origionalDirection) {
        return pos.relative(origionalDirection.getCounterClockWise());
    }

    public static @Nullable BlockEntity getBlockEntityAtRight(LevelAccessor level, BlockPos pos, BlockState state) {
        try {
            return level.getBlockEntity(getPosAtRightDirect(pos, state));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static @Nullable BlockEntity getBlockEntityAtLeft(LevelAccessor level, BlockPos pos, BlockState state) {
        try {
            return level.getBlockEntity(getPosAtLeftDirect(pos, state));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static @Nullable BlockEntity getBlockEntityAtRight(LevelAccessor level, BlockPos pos, Direction origionalDirection) {
        try {
            return level.getBlockEntity(getPosAtRight(pos, origionalDirection));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static @Nullable BlockEntity getBlockEntityAtLeft(LevelAccessor level, BlockPos pos, Direction origionalDirection) {
        try {
            return level.getBlockEntity(getPosAtLeft(pos, origionalDirection));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return null;
        }
    }

    public static BlockState getBlockStateAtRight(LevelAccessor level, BlockPos pos, BlockState state) {
        try {
            return level.getBlockState(getPosAtRightDirect(pos, state));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return Blocks.AIR.defaultBlockState();
        }
    }

    public static BlockState getBlockStateAtLeft(LevelAccessor level, BlockPos pos, BlockState state) {
        try {
            return level.getBlockState(getPosAtLeftDirect(pos, state));
        } catch (IllegalArgumentException e) {
            LogUtils.getLogger().error(e.getMessage());
            return Blocks.AIR.defaultBlockState();
        }
    }

    public static boolean isParallel(Direction d1, Direction d2) {
        return d1.getAxis() == d2.getAxis();
    }
}
