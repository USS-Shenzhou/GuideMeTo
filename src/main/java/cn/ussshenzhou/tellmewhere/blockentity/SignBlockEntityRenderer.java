package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RawQuad;
import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.ImageHelper;
import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.PillarBlock;
import cn.ussshenzhou.tellmewhere.util.AlwaysZeroRandomSource;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.block.dispatch.SingleVariant;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.SimpleModelWrapper;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.geometry.QuadCollection;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author USS_Shenzhou
 */
@ParametersAreNonnullByDefault
public class SignBlockEntityRenderer implements BlockEntityRenderer<SignBlockEntity, SignBlockEntityRenderState> {

    public static final Identifier BACKGROUND = Identifier.fromNamespaceAndPath(TellMeWhere.MODID, "block/background");

    public SignBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(SignBlockEntity blockEntity) {
        var extra = blockEntity.getBlockState().getValue(BlockStateProperties.FACING).getCounterClockWise().normal.multiply(blockEntity.getScreenLength16() / 16);
        return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity).expandTowards(extra.getX(), extra.getY(), extra.getZ());
    }

    @Override
    public SignBlockEntityRenderState createRenderState() {
        return new SignBlockEntityRenderState();
    }

    @Override
    public void extractRenderState(SignBlockEntity blockEntity, SignBlockEntityRenderState state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
        state.blockEntity = blockEntity;
    }

    @Override
    public void submit(SignBlockEntityRenderState signBlockEntityRenderState, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState cameraRenderState) {
        var block = signBlockEntityRenderState.blockState.getBlock();
        if (block instanceof PillarBlock) {
            return;
        }
        var sign = signBlockEntityRenderState.blockEntity;
        var packedLight = RenderUtil.getPackedLight(sign.getLight(), signBlockEntityRenderState.lightCoords);
        if (sign.isMaster()) {
            poseStack.pushPose();
            moveToUpLeft(sign, poseStack);
            renderBackGround(sign, poseStack, submitNodeCollector, packedLight);
            renderContent(sign, poseStack, submitNodeCollector, packedLight);
            poseStack.popPose();
        }
    }

    @SuppressWarnings("deprecation")
    private static void renderBackGround(SignBlockEntity thiz, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight) {
        poseStack.pushPose();
        float x1 = -thiz.getScreenLength16() / 16f;
        float y1 = -thiz.screenHeight16 / 16f;
        poseStack.translate(0, 0, -0.01f);
        var image = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getSprite(BACKGROUND);
        var color = thiz.data.getBackgroundArgb();
        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.solidMovingBlock(), (pose, buffer) -> {
            buffer.addVertex(pose, 0, 0, 0).setColor(color).setUv(image.getU0(), image.getV0()).setLight(packedLight).setNormal(1, 0, 0);
            buffer.addVertex(pose, 0, y1, 0).setColor(color).setUv(image.getU0(), image.getV1()).setLight(packedLight).setNormal(1, 0, 0);
            buffer.addVertex(pose, x1, y1, 0).setColor(color).setUv(image.getU1(), image.getV1()).setLight(packedLight).setNormal(1, 0, 0);
            buffer.addVertex(pose, x1, 0, 0).setColor(color).setUv(image.getU1(), image.getV0()).setLight(packedLight).setNormal(1, 0, 0);

        });
        poseStack.popPose();
    }

    public static void moveToUpLeft(SignBlockEntity thiz, PoseStack poseStack) {
        //start from left-up, just like gui UV
        RenderUtil.rotateAroundBlockCenter(getFacing(thiz), poseStack);
        poseStack.translate(1, 1, 0);
        poseStack.translate(-thiz.screenStart16.x / 16, -thiz.screenStart16.y / 16, thiz.screenStart16.z / 16);
    }

    public static void renderContent(SignBlockEntity thiz, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight) {
        poseStack.pushPose();
        //moveToUpLeft(poseStack);
        poseStack.translate(0, -thiz.screenHeight16 / 2f / 16, -0.015f);
        poseStack.rotateAround(Axis.ZP.rotation((float) Math.PI), 0, 0, 0);
        poseStack.scale(1f / ImageHelper.IMAGE_SIZE, 1f / ImageHelper.IMAGE_SIZE, 0);
        poseStack.scale(thiz.screenHeight16 / 16f, thiz.screenHeight16 / 16f, 0);
        thiz.getSignText().renderInWorld(poseStack, submitNodeCollector, packedLight, thiz.getData().getForegroundArgb());
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 192;
    }


    private static float blockStartX16(SignBlockEntity thiz) {
        return thiz.screenStart16.x - thiz.screenMargin16;
    }


    private static float blockStartY16(SignBlockEntity thiz) {
        return thiz.screenStart16.y - thiz.screenMargin16;
    }


    private static float blockStartZ16(SignBlockEntity thiz) {
        return thiz.screenStart16.z;
    }


    private static float blockEndX16(SignBlockEntity thiz) {
        return thiz.screenStart16.x + thiz.defaultScreenLength16 + thiz.screenMargin16;
    }


    private static float blockEndY16(SignBlockEntity thiz) {
        return thiz.screenStart16.y + thiz.screenHeight16 + thiz.screenMargin16;
    }


    private static float blockEndZ16(SignBlockEntity thiz) {
        return thiz.screenStart16.z + thiz.screenThick16;
    }


    protected static void handleFront(SignBlockEntity thiz, RawQuad front) {
        front.shrink16(blockStartY16(thiz), 16 - blockEndY16(thiz), blockStartX16(thiz), 16 - blockEndX16(thiz));
        front.moveRel16(0, 0, blockStartZ16(thiz));
    }


    protected static void handleBack(SignBlockEntity thiz, RawQuad back) {
        back.shrink16(blockStartY16(thiz), 16 - blockEndY16(thiz), 16 - blockEndX16(thiz), blockStartX16(thiz));
        back.moveRel16(0, 0, 16 - blockEndZ16(thiz));
    }


    protected static void handleLeft(SignBlockEntity thiz, RawQuad left) {
        left.shrink16(blockStartY16(thiz), 16 - blockEndY16(thiz), 16 - blockEndZ16(thiz), blockStartZ16(thiz));
        left.moveRel16(0, 0, blockStartX16(thiz));
    }


    protected static void handleRight(SignBlockEntity thiz, RawQuad right) {
        right.shrink16(blockStartY16(thiz), 16 - blockEndY16(thiz), blockStartZ16(thiz), 16 - blockEndZ16(thiz));
        right.moveRel16(0, 0, 16 - blockEndX16(thiz));
    }


    protected static void handleUp(SignBlockEntity thiz, RawQuad up) {
        switch (getFacing(thiz)) {
            case NORTH -> up.shrink16(blockStartZ16(thiz), 16 - blockEndZ16(thiz), blockStartX16(thiz), 16 - blockEndX16(thiz));
            case SOUTH -> up.shrink16(16 - blockEndZ16(thiz), blockStartZ16(thiz), 16 - blockEndX16(thiz), blockStartX16(thiz));
            case EAST -> up.shrink16(16 - blockEndX16(thiz), blockStartX16(thiz), 16 - blockEndZ16(thiz), blockStartZ16(thiz));
            case WEST -> up.shrink16(blockStartX16(thiz), 16 - blockEndX16(thiz), blockStartZ16(thiz), 16 - blockEndZ16(thiz));
        }
        up.moveRel16(0, 0, blockStartY16(thiz));
    }


    protected static void handleDown(SignBlockEntity thiz, RawQuad down) {
        switch (getFacing(thiz)) {
            case NORTH -> down.shrink16(16 - blockEndZ16(thiz), blockStartZ16(thiz), blockStartX16(thiz), 16 - blockEndX16(thiz));
            case SOUTH -> down.shrink16(blockStartZ16(thiz), 16 - blockEndZ16(thiz), 16 - blockEndX16(thiz), blockStartX16(thiz));
            case EAST -> down.shrink16(blockStartX16(thiz), 16 - blockEndX16(thiz), 16 - blockEndZ16(thiz), blockStartZ16(thiz));
            case WEST -> down.shrink16(16 - blockEndX16(thiz), blockStartX16(thiz), blockStartZ16(thiz), 16 - blockEndZ16(thiz));
        }
        down.moveRel16(0, 0, 16 - blockEndY16(thiz));
    }


    protected static void handleQuads(SignBlockEntity thiz, BlockStateModel blockModel, Direction d, Consumer<RawQuad> directionalHandler, List<BakedQuad> quadList) {
        var list = new ArrayList<BlockStateModelPart>();
        blockModel.collectParts((BlockAndTintGetter) thiz.getLevel(), thiz.getBlockPos(), thiz.getDisguiseBlockState(), new AlwaysZeroRandomSource(), list);
        for (var p : list) {
            for (var b : p.getQuads(d)) {
                RawQuad r = new RawQuad(b);
                directionalHandler.accept(r);
                quadList.add(r.bake());
            }
        }
    }


    public static void calculateDisguiseModel(SignBlockEntity thiz) {
        var blockModel = Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(thiz.getDisguiseBlockState());
        List<BakedQuad> quadList = new ArrayList<>();
        Direction front = getFacing(thiz);
        handleQuads(thiz, blockModel, front, r -> handleFront(thiz, r), quadList);
        front = front.getCounterClockWise();
        handleQuads(thiz, blockModel, front, r -> handleRight(thiz, r), quadList);
        front = front.getCounterClockWise();
        handleQuads(thiz, blockModel, front, r -> handleBack(thiz, r), quadList);
        front = front.getCounterClockWise();
        handleQuads(thiz, blockModel, front, r -> handleLeft(thiz, r), quadList);
        handleQuads(thiz, blockModel, Direction.UP, r -> handleUp(thiz, r), quadList);
        handleQuads(thiz, blockModel, Direction.DOWN, r -> handleDown(thiz, r), quadList);
        var modelBuilder = new QuadCollection.Builder();
        quadList.forEach(modelBuilder::addUnculledFace);
        if (quadList.isEmpty()) {
            thiz.disguiseModel = null;
            return;
        }
        var quad = quadList.getFirst();
        thiz.disguiseModel = new SingleVariant(new SimpleModelWrapper(modelBuilder.build(), quad.materialInfo().ambientOcclusion(), new Material.Baked(quad.materialInfo().sprite(), true)));
        if (thiz.getLevel() != null) {
            thiz.getLevel().setBlocksDirty(thiz.getBlockPos(), thiz.getBlockState(), thiz.getBlockState());
        }
    }


    public static Direction getFacing(SignBlockEntity thiz) {
        return BlockUtil.justGetFacing(thiz.getDisguiseBlockState(), thiz.getBlockState());
    }
}
