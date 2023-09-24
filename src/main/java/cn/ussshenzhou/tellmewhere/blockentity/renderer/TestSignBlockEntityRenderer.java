package cn.ussshenzhou.tellmewhere.blockentity.renderer;

import cn.ussshenzhou.t88.util.T88Util;
import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntityRenderer implements BlockEntityRenderer<TestSignBlockEntity> {

    public TestSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TestSignBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pBlockEntity.getSignText().render(pPoseStack, pBuffer,
                pBlockEntity.getLight() == -1 ? pPackedLight : T88Util.overrideBlockLight(pPackedLight, pBlockEntity.getLight()),
                pPackedOverlay);
        pPoseStack.popPose();
    }
}
