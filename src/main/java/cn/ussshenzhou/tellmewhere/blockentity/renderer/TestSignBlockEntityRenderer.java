package cn.ussshenzhou.tellmewhere.blockentity.renderer;

import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.SignText;
import cn.ussshenzhou.tellmewhere.blockentity.SignBlockEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity> {

    public TestSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (sign.isMaster()) {
            packedLight = RenderUtil.getPackedLight(sign.getLight(), packedLight);
            poseStack.pushPose();
            sign.renderText(poseStack, buffer, packedLight, SignText.BakedType.TEXT);
            poseStack.popPose();
        }
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(SignBlockEntity blockEntity) {
        var extra = blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getCounterClockWise().getNormal().multiply(blockEntity.screenLength16 / 16);
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).expandTowards(extra.getX(), extra.getY(), extra.getZ());
    }
}
