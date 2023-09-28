package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.render.RawQuad;
import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.SignText;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntity extends BlockEntity {
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
    }

    public void setDisguise(BlockState disguiseState) {
        disguiseBlockState = disguiseState;
        needUpdate();
        if (level.isClientSide) {
            calculateDisguiseModel();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void calculateDisguiseModel() {
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
    }

    @OnlyIn(Dist.CLIENT)
    public BakedModel getDisguiseModel() {
        return disguiseModel;
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
}
