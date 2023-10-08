package cn.ussshenzhou.tellmewhere.blockentity.renderer;

import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.SignText;
import cn.ussshenzhou.tellmewhere.blockentity.SignBlockEntity;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

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
}
