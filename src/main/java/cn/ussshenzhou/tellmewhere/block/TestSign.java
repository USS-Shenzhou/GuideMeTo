package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author USS_Shenzhou
 */
public class TestSign extends BaseEntityBlock {
    protected TestSign() {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
        );
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TestSignBlockEntity(pPos, pState);
    }
}
