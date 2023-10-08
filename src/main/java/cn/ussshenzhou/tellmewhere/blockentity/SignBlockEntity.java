package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.render.ChunkCompileContext;
import cn.ussshenzhou.t88.render.IFixedModelBlockEntity;
import cn.ussshenzhou.t88.render.RawQuad;
import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.ModRenderTypes;
import cn.ussshenzhou.tellmewhere.SignText;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class SignBlockEntity extends BlockEntity implements IFixedModelBlockEntity {
    public static final String RAW_TEXT = "tmw_rawtext";
    public static final String LIGHT = "tmw_light";
    public static final String DISGUISE = "tmw_disguise";
    public static final String SLAVE = "tmw_slave";
    public static final String LENGTH = "tmw_length";

    private SignText signText = new SignText();
    private short light = -1;
    private BlockState disguiseBlockState = Blocks.AIR.defaultBlockState();
    private boolean slave = false;
    public int screenLength16;

    public final int defaultScreenLength16;
    public final Vector3f screenStart16;
    public final int screenHeight16;
    public final int screenThick16;
    public final int screenMargin16;
    //public final int textMargin = 0;

    @OnlyIn(Dist.CLIENT)
    private BakedModel disguiseModel;

    public SignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(pPos, pBlockState, new Vector3f(0, 0, 0), 16, 16, 16, 0);
    }

    public SignBlockEntity(BlockPos pPos, BlockState pBlockState, Vector3f screenStart16, int defaultScreenLength16, int screenHeight16, int screenThick16, int screenMargin16) {
        super(ModBlockEntityTypeRegistry.TEST_SIGN.get(), pPos, pBlockState);
        this.defaultScreenLength16 = defaultScreenLength16;
        this.screenStart16 = screenStart16;
        this.screenHeight16 = screenHeight16;
        this.screenThick16 = screenThick16;
        this.screenMargin16 = screenMargin16;
        screenLength16 = defaultScreenLength16;
    }

    public void neighborUpdated() {
        this.findMasterAt(true).checkSlavesAt();
        this.findMasterAt(false).checkSlavesAt();
    }

    public SignBlockEntity findMasterAt(boolean left) {
        var t = this;
        Direction facing = t.getBlockState().getValue(FACING);
        if (left) {
            facing = facing.getOpposite();
        }
        while (true) {
            BlockEntity rightEntity = DirectionUtil.getBlockEntityAtRight(level, t.getBlockPos(), facing);
            if (rightEntity instanceof SignBlockEntity r && DirectionUtil.isParallel(r.getBlockState().getValue(FACING), facing)) {
                t = r;
            } else {
                break;
            }
        }
        return t;
    }

    public ArrayList<SignBlockEntity> findSlaves() {
        ArrayList<SignBlockEntity> slaves = new ArrayList<>();
        var t = this;
        Direction facing = t.getBlockState().getValue(FACING);
        while (true) {
            BlockEntity rightEntity = DirectionUtil.getBlockEntityAtLeft(level, t.getBlockPos(), facing);
            if (rightEntity instanceof SignBlockEntity l && DirectionUtil.isParallel(l.getBlockState().getValue(FACING), facing)) {
                t = l;
                slaves.add(l);
            } else {
                break;
            }
        }
        return slaves;
    }

    public void checkSlavesAt() {
        var slaves = this.findSlaves();
        this.slave = false;
        this.screenLength16 = defaultScreenLength16 + 16 * slaves.size();
        slaves.forEach(SignBlockEntity::setSlave);
        var last = slaves.isEmpty() ? this : slaves.get(slaves.size() - 1);
        if (last.getBlockState().getValue(FACING) != this.getBlockState().getValue(FACING)) {
            //master of the other direction
            last.setFree();
        }
        needBroadcastToClients();
    }

    public void setSlave() {
        this.slave = true;
        needBroadcastToClients();
    }

    public void setFree() {
        this.slave = false;
        needBroadcastToClients();
    }

    public boolean isSlave() {
        return slave;
    }

    public boolean isMaster() {
        return !slave;
    }

    public SignText getSignText() {
        return signText;
    }

    public void setRawTexts(Map<String, String> languageAndText) {
        this.setSignText(new SignText(languageAndText));
    }

    public void setSignText(SignText signText) {
        this.signText = signText;
        if (level != null && !level.isClientSide) {
            needBroadcastToClients();
        }
        this.signText.restrainWidth(this.screenLength16);
    }

    private void needBroadcastToClients() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_NONE);
        }
    }

    public short getLight() {
        return light;
    }

    public void setLight(short light) {
        this.light = light;
        needBroadcastToClients();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        signText.write(tag);
        tag.putShort(LIGHT, light);
        tag.put(DISGUISE, NbtUtils.writeBlockState(disguiseBlockState));
        tag.putBoolean(SLAVE, slave);
        tag.putInt(LENGTH, screenLength16);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        signText.write(tag);
        tag.putShort(LIGHT, light);
        tag.put(DISGUISE, NbtUtils.writeBlockState(disguiseBlockState));
        tag.putBoolean(SLAVE, slave);
        tag.putInt(LENGTH, screenLength16);
        return tag;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        light = tag.getShort(LIGHT);
        HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.disguiseBlockState = NbtUtils.readBlockState(holdergetter, tag.getCompound(DISGUISE));
        if (level != null) {
            setDisguise(disguiseBlockState);
        }
        slave = tag.getBoolean(SLAVE);
        screenLength16 = tag.getInt(LENGTH);
        //keep it at last
        this.setSignText(SignText.read(tag));
    }

    public void setDisguise(BlockState disguiseState) {
        disguiseBlockState = disguiseState;
        needBroadcastToClients();
        if (level.isClientSide) {
            calculateDisguiseModel();
        }
    }

    public BlockState getDisguiseBlockState() {
        return disguiseBlockState;
    }

    @Override
    public AABB getRenderBoundingBox() {
        var extra = this.getBlockState().getValue(FACING).getCounterClockWise().getNormal().multiply(screenLength16 / 16);
        return super.getRenderBoundingBox().expandTowards(extra.getX(), extra.getY(), extra.getZ());
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //----------client----------

    protected void handleFrontAndBack(RawQuad rawQuad) {
        rawQuad.shrink16(screenStart16.y - screenMargin16, 16 - screenStart16.y - screenHeight16 - screenMargin16, 0, 0);
    }

    protected void handleFront(RawQuad front) {
        handleFrontAndBack(front);
        front.shrink16(0, 0, screenStart16.x - screenMargin16, 16 - screenStart16.x - defaultScreenLength16 - screenMargin16);
        int i = noReverse() ? 1 : -1;
        if (front.getDirection().getAxis() == Direction.Axis.X) {
            front.move16(screenStart16.z * i, 0, 0);
        } else {
            front.move16(0, 0, screenStart16.z * i);
        }
    }

    protected void handleBack(RawQuad back) {
        handleFrontAndBack(back);
        back.shrink16(0, 0, 16 - screenStart16.x - defaultScreenLength16 - screenMargin16, screenStart16.x - screenMargin16);
        int i = noReverse() ? 1 : -1;
        if (back.getDirection().getAxis() == Direction.Axis.X) {
            back.move16(-(16 - screenThick16 - screenStart16.z) * i, 0, 0);
        } else {
            back.move16(0, 0, -(16 - screenThick16 - screenStart16.z) * i);
        }
    }

    protected void handleLeft(RawQuad left) {
        left.shrink16(0, 16 - screenStart16.y - screenHeight16 - screenMargin16, 16 - screenThick16 - screenStart16.z, screenStart16.z);
        int i = noReverse() ? 1 : -1;
        if (left.getDirection().getAxis() == Direction.Axis.X) {
            left.move16(-(screenStart16.x - screenMargin16) * i, 0, 0);
        } else {
            left.move16(0, 0, (screenStart16.x - screenMargin16) * i);
        }
    }

    protected void handleRight(RawQuad right) {
        right.shrink16(0, 16 - screenStart16.y - screenHeight16 - screenMargin16, screenStart16.z, 16 - screenThick16 - screenStart16.z);
        int i = noReverse() ? 1 : -1;
        if (right.getDirection().getAxis() == Direction.Axis.X) {
            right.move16((16 - screenStart16.x - defaultScreenLength16 - screenMargin16) * i, 0, 0);
        } else {
            right.move16(0, 0, -(16 - screenStart16.x - defaultScreenLength16 - screenMargin16) * i);
        }
    }

    protected void handleUpAndDown(RawQuad rawQuad) {
        float i1 = screenStart16.z;
        float i2 = 16 - screenThick16 - screenStart16.z;
        switch (getFacing()) {
            case NORTH -> {
                handleUpAndDownInternalZ(i1, i2, rawQuad);
                rawQuad.shrink16(0, 0, 16 - screenStart16.y - defaultScreenLength16 - screenMargin16, screenStart16.x - screenMargin16);
            }
            case SOUTH -> {
                handleUpAndDownInternalZ(i2, i1, rawQuad);
                rawQuad.shrink16(0, 0, screenStart16.x - screenMargin16, 16 - screenStart16.y - defaultScreenLength16 - screenMargin16);
            }
            case WEST -> {
                rawQuad.shrink16(0, 0, i1, i2);
                handleUpAndDownInternalX(screenStart16.x - screenMargin16, 16 - screenStart16.y - defaultScreenLength16 - screenMargin16, rawQuad);
            }
            case EAST -> {
                rawQuad.shrink16(0, 0, i2, i1);
                handleUpAndDownInternalX(16 - screenStart16.y - defaultScreenLength16 - screenMargin16, screenStart16.x - screenMargin16, rawQuad);
            }
        }
    }

    protected void handleUpAndDownInternalX(float f1, float f2, RawQuad rawQuad) {
        if (rawQuad.getDirection() == Direction.UP) {
            rawQuad.shrink16(f1, f2, 0, 0);
        } else {
            rawQuad.shrink16(f2, f1, 0, 0);
        }
    }

    protected void handleUpAndDownInternalZ(float f1, float f2, RawQuad rawQuad) {
        if (rawQuad.getDirection() == Direction.UP) {
            rawQuad.shrink16(f1, f2, 0, 0);
        } else {
            rawQuad.shrink16(f2, f1, 0, 0);
        }
    }

    protected void handleUp(RawQuad up) {
        handleUpAndDown(up);
        up.move16(0, -(screenStart16.y - screenMargin16), 0);
    }

    protected void handleDown(RawQuad down) {
        handleUpAndDown(down);
        down.move16(0, 16 - screenStart16.y - screenHeight16 - screenMargin16, 0);
    }

    protected boolean noReverse() {
        return getFacing() == Direction.NORTH || getFacing() == Direction.WEST;
    }

    protected void handleQuads(BakedModel blockModel, Direction d, Consumer<RawQuad> directionalHandler, List<BakedQuad> quadList) {
        for (BakedQuad b : blockModel.getQuads(disguiseBlockState, d, level.random)) {
            RawQuad r = new RawQuad(b);
            directionalHandler.accept(r);
            quadList.add(r.bake());
        }
    }

    public void calculateDisguiseModel() {
        BakedModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguiseBlockState);
        List<BakedQuad> quadList = new ArrayList<>();
        Direction front = getFacing();
        handleQuads(blockModel, front, this::handleFront, quadList);
        front = front.getCounterClockWise();
        handleQuads(blockModel, front, this::handleRight, quadList);
        front = front.getCounterClockWise();
        handleQuads(blockModel, front, this::handleBack, quadList);
        front = front.getCounterClockWise();
        handleQuads(blockModel, front, this::handleLeft, quadList);
        handleQuads(blockModel, Direction.UP, this::handleUp, quadList);
        handleQuads(blockModel, Direction.DOWN, this::handleDown, quadList);
        disguiseModel = RenderUtil.simpleFromBakedQuads(quadList, blockModel);
        if (level != null) {
            level.setBlocksDirty(getBlockPos(), getBlockState(), getBlockState());
        }
    }

    public Direction getFacing() {
        return BlockUtil.justGetFacing(disguiseBlockState, this.getBlockState());
    }

    public BakedModel getDisguiseModel() {
        return disguiseModel;
    }

    @Override
    public ChunkCompileContext handleCompileContext(ChunkCompileContext chunkCompileContext) {
        return chunkCompileContext
                .withRenderType(RenderType.translucent())
                .withPrepareBakedModelRender(
                        chunkCompileContext.resetToBlock000()
                        //fixme wrong AO if tessellateBlock, may related to quad's points' normal, and/or poseStack's normal.
                        //.andThen(chunkCompileContext.rotateByState())
                )
                .withBakedModel(disguiseModel)
                .withBlockState(disguiseBlockState)
                .withAdditionalRender();
    }

    @Override
    public void renderAdditional(ChunkCompileContext context, Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay) {
        if (this.isMaster()) {
            int l = getPackedLight();
            renderBackGround(poseStack, getBuilder(begunRenderTypes, builderPack, ModRenderTypes.FILL_COLOR), l);
            renderTextOnlyImage(poseStack, getSimpleMultiBufferSource(begunRenderTypes, builderPack, RenderType.translucent()), l);
        }
    }

    public void moveToUpLeft(PoseStack poseStack) {
        //start from left-up, just like gui UV
        RenderUtil.rotateAroundBlockCenter(getFacing(), poseStack);
        poseStack.translate(1, 1, 0);
        poseStack.translate(-screenStart16.x / 16, -screenStart16.y / 16, screenStart16.z / 16);
    }

    private void renderBackGround(PoseStack poseStack, BufferBuilder builder, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(ModRenderTypes.FILL_COLOR, poseStack);
        moveToUpLeft(poseStack);
        float x1 = -this.screenLength16 / 16f;
        float y1 = -this.screenHeight16 / 16f;
        var matrix = poseStack.last().pose();
        poseStack.translate(0, 0, -0.001f);
        builder.vertex(matrix, 0, 0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, 0, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, x1, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, x1, 0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        poseStack.popPose();
    }

    private void renderTextOnlyImage(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(RenderType.translucent(), poseStack);
        renderText(poseStack, buffer, packedLight, SignText.BakedType.IMAGE);
        poseStack.popPose();
    }

    public void renderText(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SignText.BakedType only) {
        moveToUpLeft(poseStack);
        poseStack.translate(0, -this.screenHeight16 / 2f / 16, -0.002f);
        poseStack.rotateAround(Axis.ZP.rotation((float) Math.PI), 0, 0, 0);
        poseStack.scale(1 / 12f, 1 / 12f, 0);
        poseStack.scale(this.screenHeight16 / 16f, this.screenHeight16 / 16f, 0);
        this.signText.render(poseStack, buffer, packedLight, only);
    }
}
