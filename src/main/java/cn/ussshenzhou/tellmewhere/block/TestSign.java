package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import cn.ussshenzhou.tellmewhere.gui.SignEditScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.registries.ForgeRegistries;
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

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.isCreative()) {
            if (pPlayer.level().isClientSide()) {
                openEditor((TestSignBlockEntity) pPlayer.level().getBlockEntity(pPos));
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private void openEditor(TestSignBlockEntity blockEntity) {
        ForgeHooksClient.pushGuiLayer(Minecraft.getInstance(), new SignEditScreen(blockEntity));
    }
}
