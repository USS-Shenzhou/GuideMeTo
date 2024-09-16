package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.T88;
import cn.ussshenzhou.t88.render.RawQuad;
import cn.ussshenzhou.t88.render.fixedblockentity.IFixedModelBlockEntity;
import cn.ussshenzhou.t88.render.fixedblockentity.SectionCompileContext;
import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.ImageHelper;
import cn.ussshenzhou.tellmewhere.ModRenderTypes;
import cn.ussshenzhou.tellmewhere.SignText;
import cn.ussshenzhou.tellmewhere.block.BaseSignBlock;
import cn.ussshenzhou.tellmewhere.util.AlwaysZeroRandomSource;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
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

    public int defaultScreenLength16;
    public Vector3f screenStart16;
    public int screenHeight16;
    public int screenThick16;
    public int screenMargin16;
    //public final int textMargin = 0;

    @OnlyIn(Dist.CLIENT)
    private BakedModel disguiseModel;

    public SignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        this(pPos, pBlockState, new Vector3f(0, 0, 0), Integer.MAX_VALUE, 16, 16, 0);
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
            if (rightEntity instanceof SignBlockEntity r
                    && r.getBlockState().getBlock() == this.getBlockState().getBlock()
                    && DirectionUtil.isParallel(r.getBlockState().getValue(FACING), facing)) {
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
            if (rightEntity instanceof SignBlockEntity l
                    && l.getBlockState().getBlock() == this.getBlockState().getBlock()
                    && DirectionUtil.isParallel(l.getBlockState().getValue(FACING), facing)) {
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
        if (level != null) {
            if (!level.isClientSide) {
                needBroadcastToClients();
            } else {
                this.signText.setUsableWidth(this.screenLength16, this.screenHeight16);
            }
        }
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        if (this.defaultScreenLength16 == Integer.MAX_VALUE) {
            //load from disk
            BaseSignBlock block = (BaseSignBlock) this.getBlockState().getBlock();
            this.defaultScreenLength16 = block.defaultScreenLength16;
            this.screenStart16 = new Vector3f(block.screenStart16);
            this.screenHeight16 = block.screenHeight16;
            this.screenThick16 = block.screenThick16;
            this.screenMargin16 = block.screenMargin16;
        }
        super.loadAdditional(tag, registries);
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

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        writeTag(tag);
    }

    private void writeTag(CompoundTag tag) {
        signText.write(tag);
        tag.putShort(LIGHT, light);
        tag.put(DISGUISE, NbtUtils.writeBlockState(disguiseBlockState));
        tag.putBoolean(SLAVE, slave);
        tag.putInt(LENGTH, screenLength16);
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
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        var tag = super.getUpdateTag(registries);
        writeTag(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    //----------client----------

    @OnlyIn(Dist.CLIENT)
    private float blockStartX16() {
        return screenStart16.x - screenMargin16;
    }

    @OnlyIn(Dist.CLIENT)
    private float blockStartY16() {
        return screenStart16.y - screenMargin16;
    }

    @OnlyIn(Dist.CLIENT)
    private float blockStartZ16() {
        return screenStart16.z;
    }

    @OnlyIn(Dist.CLIENT)
    private float blockEndX16() {
        return screenStart16.x + defaultScreenLength16 + screenMargin16;
    }

    @OnlyIn(Dist.CLIENT)
    private float blockEndY16() {
        return screenStart16.y + screenHeight16 + screenMargin16;
    }

    @OnlyIn(Dist.CLIENT)
    private float blockEndZ16() {
        return screenStart16.z + screenThick16;
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleFront(RawQuad front) {
        front.shrink16(blockStartY16(), 16 - blockEndY16(), blockStartX16(), 16 - blockEndX16());
        front.moveRel16(0, 0, blockStartZ16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleBack(RawQuad back) {
        back.shrink16(blockStartY16(), 16 - blockEndY16(), 16 - blockEndX16(), blockStartX16());
        back.moveRel16(0, 0, 16 - blockEndZ16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleLeft(RawQuad left) {
        left.shrink16(blockStartY16(), 16 - blockEndY16(), 16 - blockEndZ16(), blockStartZ16());
        left.moveRel16(0, 0, blockStartX16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleRight(RawQuad right) {
        right.shrink16(blockStartY16(), 16 - blockEndY16(), blockStartZ16(), 16 - blockEndZ16());
        right.moveRel16(0, 0, 16 - blockEndX16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleUp(RawQuad up) {
        switch (getFacing()) {
            case NORTH -> up.shrink16(blockStartZ16(), 16 - blockEndZ16(), blockStartX16(), 16 - blockEndX16());
            case SOUTH -> up.shrink16(16 - blockEndZ16(), blockStartZ16(), 16 - blockEndX16(), blockStartX16());
            case EAST -> up.shrink16(16 - blockEndX16(), blockStartX16(), 16 - blockEndZ16(), blockStartZ16());
            case WEST -> up.shrink16(blockStartX16(), 16 - blockEndX16(), blockStartZ16(), 16 - blockEndZ16());
        }
        up.moveRel16(0, 0, blockStartY16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleDown(RawQuad down) {
        switch (getFacing()) {
            case NORTH -> down.shrink16(16 - blockEndZ16(), blockStartZ16(), blockStartX16(), 16 - blockEndX16());
            case SOUTH -> down.shrink16(blockStartZ16(), 16 - blockEndZ16(), 16 - blockEndX16(), blockStartX16());
            case EAST -> down.shrink16(blockStartX16(), 16 - blockEndX16(), 16 - blockEndZ16(), blockStartZ16());
            case WEST -> down.shrink16(16 - blockEndX16(), blockStartX16(), blockStartZ16(), 16 - blockEndZ16());
        }
        down.moveRel16(0, 0, 16 - blockEndY16());
    }

    @OnlyIn(Dist.CLIENT)
    protected void handleQuads(BakedModel blockModel, Direction d, Consumer<RawQuad> directionalHandler, List<BakedQuad> quadList) {
        for (BakedQuad b : blockModel.getQuads(disguiseBlockState, d, new AlwaysZeroRandomSource())) {
            RawQuad r = new RawQuad(b);
            directionalHandler.accept(r);
            quadList.add(r.bake());
        }
    }

    @OnlyIn(Dist.CLIENT)
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

    @OnlyIn(Dist.CLIENT)
    public Direction getFacing() {
        return BlockUtil.justGetFacing(disguiseBlockState, this.getBlockState());
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getDisguiseModel() {
        return disguiseModel;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public SectionCompileContext handleCompileContext(SectionCompileContext chunkCompileContext) {
        return chunkCompileContext
                .withRenderType(RenderType.translucent())
                .withPrepareBakedModelRender(chunkCompileContext.resetToBlock000())
                .withBakedModel(disguiseModel)
                .withBlockState(disguiseBlockState)
                .withAdditionalRender();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderAdditionalAsync(SectionCompileContext context, PoseStack poseStack) {
        if (this.isMaster()) {
            //TODO
            int l = 240;
            renderBackGround(poseStack, context.getVertexConsumer(T88.SODIUM_EXIST ? RenderType.solid() : ModRenderTypes.FILL_COLOR), l);
            renderTextOnlyImage(poseStack, context.getSimpleMultiBufferSource(RenderType.translucent()), l);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void moveToUpLeft(PoseStack poseStack) {
        //start from left-up, just like gui UV
        RenderUtil.rotateAroundBlockCenter(getFacing(), poseStack);
        poseStack.translate(1, 1, 0);
        poseStack.translate(-screenStart16.x / 16, -screenStart16.y / 16, screenStart16.z / 16);
    }

    @OnlyIn(Dist.CLIENT)
    private void renderBackGround(PoseStack poseStack, VertexConsumer builder, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(T88.SODIUM_EXIST ? RenderType.solid() : ModRenderTypes.FILL_COLOR, poseStack);
        moveToUpLeft(poseStack);
        float x1 = -this.screenLength16 / 16f;
        float y1 = -this.screenHeight16 / 16f;
        poseStack.translate(0, 0, -0.005f);
        var matrix = poseStack.last().pose();
        if (T88.SODIUM_EXIST) {
            var image = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ResourceLocation.withDefaultNamespace("missingno"));
            builder.addVertex(matrix, 0, 0, 0).setColor(0, 0, 0, 1).setLight(packedLight).setUv(image.getU0(), image.getV0()).setNormal(1, 0, 0);
            builder.addVertex(matrix, 0, y1, 0).setColor(0, 0, 0, 1).setLight(packedLight).setUv(image.getU0(), image.getV1()).setNormal(1, 0, 0);
            builder.addVertex(matrix, x1, y1, 0).setColor(0, 0, 0, 1).setLight(packedLight).setUv(image.getU1(), image.getV1()).setNormal(1, 0, 0);
            builder.addVertex(matrix, x1, 0, 0).setColor(0, 0, 0, 1).setLight(packedLight).setUv(image.getU1(), image.getV0()).setNormal(1, 0, 0);
        } else {
            builder.addVertex(matrix, 0, 0, 0).setColor(0, 0, 0, 1).setLight(packedLight);
            builder.addVertex(matrix, 0, y1, 0).setColor(0, 0, 0, 1).setLight(packedLight);
            builder.addVertex(matrix, x1, y1, 0).setColor(0, 0, 0, 1).setLight(packedLight);
            builder.addVertex(matrix, x1, 0, 0).setColor(0, 0, 0, 1).setLight(packedLight);
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private void renderTextOnlyImage(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(RenderType.translucent(), poseStack);
        renderText(poseStack, buffer, packedLight, SignText.BakedType.IMAGE);
        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    public void renderText(PoseStack poseStack, MultiBufferSource buffer, int packedLight, SignText.BakedType only) {
        moveToUpLeft(poseStack);
        poseStack.translate(0, -this.screenHeight16 / 2f / 16, -0.007f);
        poseStack.rotateAround(Axis.ZP.rotation((float) Math.PI), 0, 0, 0);
        poseStack.scale(1f / ImageHelper.IMAGE_SIZE, 1f / ImageHelper.IMAGE_SIZE, 0);
        poseStack.scale(this.screenHeight16 / 16f, this.screenHeight16 / 16f, 0);
        //TODO
        this.signText.render(poseStack, buffer, 240, only);
    }
}
