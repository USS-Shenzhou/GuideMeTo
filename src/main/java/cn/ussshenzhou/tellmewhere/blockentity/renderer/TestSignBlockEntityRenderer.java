package cn.ussshenzhou.tellmewhere.blockentity.renderer;

import cn.ussshenzhou.t88.util.T88Util;
import cn.ussshenzhou.tellmewhere.ModRenderTypes;
import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.data.ModelData;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntityRenderer implements BlockEntityRenderer<TestSignBlockEntity> {

    public TestSignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TestSignBlockEntity sign, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (sign.getLight() != -1) {
            packedLight = T88Util.overrideBlockLight(packedLight, sign.getLight());
        }
        poseStack.pushPose();
        renderDisguise(sign, poseStack, buffer, packedLight, packedOverlay);
        if (sign.isMaster()) {
            //start from left-up, just like gui
            poseStack.translate(1, 1, sign.screenDepth16 / 16f);
            var m = poseStack.last().pose();
            switch (sign.getBlockState().getValue(BlockStateProperties.FACING)) {
                case SOUTH ->
                        m.rotateAround(Axis.YP.rotation((float) Math.PI), -0.5f, -0.5f, -sign.screenDepth16 / 16f + 0.5f);
                case EAST ->
                        m.rotateAround(Axis.YP.rotation((float) Math.PI * -0.5f), -0.5f, -0.5f, -sign.screenDepth16 / 16f + 0.5f);
                case WEST ->
                        m.rotateAround(Axis.YP.rotation((float) Math.PI * 0.5f), -0.5f, -0.5f, -sign.screenDepth16 / 16f + 0.5f);
            }
            renderBackGround(sign, poseStack, buffer, packedLight);
            renderText(sign, poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

    private void renderDisguise(TestSignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (sign.getDisguiseModel() != null) {
            poseStack.pushPose();
            var m = poseStack.last().pose();
            switch (sign.getBlockState().getValue(BlockStateProperties.FACING)) {
                case SOUTH -> m.rotateAround(Axis.YP.rotation((float) Math.PI), 0.5f, 0.5f, 0.5f);
                case EAST -> m.rotateAround(Axis.YP.rotation((float) Math.PI * -0.5f), 0.5f, 0.5f, 0.5f);
                case WEST -> m.rotateAround(Axis.YP.rotation((float) Math.PI * 0.5f), 0.5f, 0.5f, 0.5f);
            }
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(sign.getLevel(),
                    sign.getDisguiseModel(),
                    sign.getDisguiseBlockState(),
                    sign.getBlockPos(),
                    poseStack,
                    buffer.getBuffer(RenderType.translucent()),
                    true,
                    sign.getLevel().random,
                    42,
                    packedOverlay,
                    ModelData.EMPTY,
                    RenderType.translucent()
            );
            poseStack.popPose();
        }
    }

    private void renderBackGround(TestSignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        float x0 = sign.screenStart16.x / 16f;
        float x1 = x0 + sign.screenLength16 / 16f;
        float y0 = sign.screenStart16.y / 16f;
        float y1 = y0 + sign.screenHeight16 / 16f;
        x0 *= -1;
        x1 *= -1;
        y0 *= -1;
        y1 *= -1;
        var consumer = buffer.getBuffer(ModRenderTypes.FILL_COLOR);
        var matrix = poseStack.last().pose();
        poseStack.translate(0, 0, -0.001f);
        consumer.vertex(matrix, x0, y0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x0, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x1, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        consumer.vertex(matrix, x1, y0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        poseStack.popPose();
    }

    private void renderText(TestSignBlockEntity sign, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        var m = poseStack.last().pose();
        m.translate(-sign.screenStart16.x / 16f, -(sign.screenStart16.y + sign.screenHeight16 / 2f) / 16, -0.002f);
        m.rotateZ((float) Math.PI);
        m.scale(1 / 12f, 1 / 12f, 0);
        m.scale(sign.screenHeight16 / 16f, sign.screenHeight16 / 16f, 0);
        sign.getSignText().render(poseStack, buffer, packedLight);
        poseStack.popPose();
    }
}
