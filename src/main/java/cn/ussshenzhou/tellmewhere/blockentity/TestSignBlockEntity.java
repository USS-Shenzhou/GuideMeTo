package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.render.ChunkCompileContext;
import cn.ussshenzhou.t88.render.IFixedModelBlockEntity;
import cn.ussshenzhou.t88.render.RawQuad;
import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.t88.util.RenderUtil;
import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.ModRenderTypes;
import cn.ussshenzhou.tellmewhere.SignText;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkBufferBuilderPack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.AXIS;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntity extends BlockEntity implements IFixedModelBlockEntity {
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

    public final int vanillaScreenLength16 = 14;
    public final int screenHeight16 = 8;
    public final Vector2i screenStart16 = new Vector2i(1, 1);
    public final int screenDepth16 = 7;

    @OnlyIn(Dist.CLIENT)
    private BakedModel disguiseModel;

    public TestSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypeRegistry.TEST_SIGN.get(), pPos, pBlockState);
        screenLength16 = vanillaScreenLength16;
    }

    public void neighborUpdated() {
        this.findMasterAt(true).checkSlavesAt();
        this.findMasterAt(false).checkSlavesAt();
    }

    public TestSignBlockEntity findMasterAt(boolean left) {
        var t = this;
        Direction facing = t.getBlockState().getValue(FACING);
        if (left) {
            facing = facing.getOpposite();
        }
        while (true) {
            BlockEntity rightEntity = DirectionUtil.getBlockEntityAtRight(level, t.getBlockPos(), facing);
            if (rightEntity instanceof TestSignBlockEntity r && DirectionUtil.isParallel(r.getBlockState().getValue(FACING), facing)) {
                t = r;
            } else {
                break;
            }
        }
        return t;
    }

    public ArrayList<TestSignBlockEntity> findSlaves() {
        ArrayList<TestSignBlockEntity> slaves = new ArrayList<>();
        var t = this;
        Direction facing = t.getBlockState().getValue(FACING);
        while (true) {
            BlockEntity rightEntity = DirectionUtil.getBlockEntityAtLeft(level, t.getBlockPos(), facing);
            if (rightEntity instanceof TestSignBlockEntity l && DirectionUtil.isParallel(l.getBlockState().getValue(FACING), facing)) {
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
        this.screenLength16 = vanillaScreenLength16 + 16 * slaves.size();
        slaves.forEach(TestSignBlockEntity::setSlave);
        var last = slaves.isEmpty() ? this : slaves.get(slaves.size() - 1);
        if (last.getBlockState().getValue(FACING) != this.getBlockState().getValue(FACING)) {
            //master of the other direction
            last.setFree();
        }
        needUpdate();
    }

    public void setSlave() {
        this.slave = true;
        needUpdate();
    }

    public void setFree() {
        this.slave = false;
        needUpdate();
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
        signText = new SignText(languageAndText);
        needUpdate();
    }

    private void needUpdate() {
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
        needUpdate();
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
        signText = SignText.read(tag);
        light = tag.getShort(LIGHT);
        HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.disguiseBlockState = NbtUtils.readBlockState(holdergetter, tag.getCompound(DISGUISE));
        if (level != null && level.isClientSide) {
            setDisguise(disguiseBlockState);
        }
        slave = tag.getBoolean(SLAVE);
        screenLength16 = tag.getInt(LENGTH);
        if (level != null && level.isClientSide) {
            level.setBlocksDirty(getBlockPos(), getBlockState(), getBlockState());
        }
    }

    public void setDisguise(BlockState disguiseState) {
        disguiseBlockState = disguiseState;
        needUpdate();
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

    @OnlyIn(Dist.CLIENT)
    public void calculateDisguiseModel() {
        if (disguiseBlockState.getOptionalValue(FACING).isPresent()) {
            disguiseBlockState.setValue(FACING, Direction.NORTH);
        }
        BakedModel blockModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(disguiseBlockState);
        List<BakedQuad> quadList = new ArrayList<>();
        for (Direction d : Direction.values()) {
            for (BakedQuad b : blockModel.getQuads(disguiseBlockState, d, level.random)) {
                RawQuad rawQuad = new RawQuad(b);
                switch (d) {
                    case SOUTH -> rawQuad.shrink16(0, 6, 0, 0).move16(0, 0, -7);
                    case NORTH -> rawQuad.shrink16(0, 6, 0, 0).move16(0, 0, 7);
                    case EAST, WEST -> rawQuad.shrink16(0, 6, 7, 7);
                    case UP -> rawQuad.shrink16(7, 7, 0, 0);
                    case DOWN -> rawQuad.shrink16(7, 7, 0, 0).move16(0, 6, 0);
                }
                //see .handleCompileContext
                /*if (rawQuad.getDirection().getAxis() != Direction.Axis.Y) {
                    var dir = switch (this.getBlockState().getValue(FACING)) {
                        case SOUTH -> rawQuad.getDirection().getOpposite();
                        default -> rawQuad.getDirection();
                        case EAST -> rawQuad.getDirection().getClockWise();
                        case WEST -> rawQuad.getDirection().getCounterClockWise();
                    };
                    rawQuad.setDirection(dir);
                    for (RawQuad.Point p : rawQuad.getPoints()) {
                        //var n = new Vector3f(p.get(4));
                        var n = dir.getNormal();
                        p.setNormal(n.getX(), n.getY(), n.getZ());
                    }
                }*/
                quadList.add(rawQuad.bake());
            }
        }
        disguiseModel = new BakedModel() {

            @Override
            public List<BakedQuad> getQuads(@Nullable BlockState pState, @Nullable Direction pDirection, RandomSource pRandom) {
                return quadList;
            }

            @Override
            public boolean useAmbientOcclusion() {
                return blockModel.useAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return blockModel.isGui3d();
            }

            @Override
            public boolean usesBlockLight() {
                return blockModel.usesBlockLight();
            }

            @Override
            public boolean isCustomRenderer() {
                return blockModel.isCustomRenderer();
            }

            @Override
            public TextureAtlasSprite getParticleIcon() {
                return blockModel.getParticleIcon();
            }

            @Override
            public ItemOverrides getOverrides() {
                return blockModel.getOverrides();
            }
        };
        if (disguiseBlockState.getOptionalValue(FACING).isPresent()) {
            disguiseBlockState.setValue(FACING, getBlockState().getValue(FACING));
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
        return chunkCompileContext.withRenderType(RenderType.cutout())
                .withPrepareBakedModelRender(
                        chunkCompileContext.resetToBlock000()
                                //fixme wrong AO
                                .andThen(chunkCompileContext.rotateByState())
                )
                .withBakedModel(disguiseModel)
                .withBlockState(disguiseBlockState)
                .withAdditionalRender();
    }

    @Override
    public void renderAdditional(Set<RenderType> begunRenderTypes, ChunkBufferBuilderPack builderPack, PoseStack poseStack, int packedOverlay) {
        if (this.isMaster()) {
            int l = getPackedLight();
            renderBackGround(poseStack, getBuilder(begunRenderTypes, builderPack, ModRenderTypes.FILL_COLOR), l);
            //renderTextOnlyImage(poseStack, getSimpleMultiBufferSource(begunRenderTypes, builderPack, RenderType.translucent()), l);
            renderTextOnlyImage(poseStack,
                    getSimpleMultiBufferSource(begunRenderTypes, builderPack, RenderType.translucent()), l);
        }
    }

    private void moveToUpLeft(PoseStack poseStack) {
        //start from left-up, just like gui
        RenderUtil.rotateAroundBlockCenter(getFacing(), poseStack);
        poseStack.translate(1, 1, this.screenDepth16 / 16f);
    }

    private void renderBackGround(PoseStack poseStack, BufferBuilder builder, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(ModRenderTypes.FILL_COLOR, poseStack);
        moveToUpLeft(poseStack);
        float x0 = this.screenStart16.x / 16f;
        float x1 = x0 + this.screenLength16 / 16f;
        float y0 = this.screenStart16.y / 16f;
        float y1 = y0 + this.screenHeight16 / 16f;
        x0 *= -1;
        x1 *= -1;
        y0 *= -1;
        y1 *= -1;
        var matrix = poseStack.last().pose();
        poseStack.translate(0, 0, -0.001f);
        builder.vertex(matrix, x0, y0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, x0, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, x1, y1, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        builder.vertex(matrix, x1, y0, 0).color(0, 0, 0, 1).uv2(packedLight).endVertex();
        poseStack.popPose();
    }

    private void renderTextOnlyImage(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        resetToBlock000(RenderType.translucent(), poseStack);
        moveToUpLeft(poseStack);
        poseStack.translate(-this.screenStart16.x / 16f, -(this.screenStart16.y + this.screenHeight16 / 2f) / 16, -0.002f);
        poseStack.rotateAround(Axis.ZP.rotation((float) Math.PI), 0, 0, 0);
        poseStack.scale(1 / 12f, 1 / 12f, 0);
        poseStack.scale(this.screenHeight16 / 16f, this.screenHeight16 / 16f, 0);
        this.getSignText().render(poseStack, buffer, packedLight, SignText.BakedType.IMAGE);
        poseStack.popPose();
    }
}
