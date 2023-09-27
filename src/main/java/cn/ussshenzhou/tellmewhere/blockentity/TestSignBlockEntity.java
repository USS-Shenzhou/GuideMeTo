package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.t88.render.RawQuad;
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
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;
import org.joml.Random;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntity extends BlockEntity {
    public static final String RAW_TEXT = "tmw_rawtext";
    public static final String LIGHT = "tmw_light";

    private SignText signText = new SignText();
    private short light = -1;

    public Vector2i screenStart16;
    public int screenLength16;
    public final int screenHeight16 = 8;
    public int screenDepth16 = 7;

    private BlockState disguiseBlockState = Blocks.AIR.defaultBlockState();
    @OnlyIn(Dist.CLIENT)
    private BakedModel disguiseModel;

    public TestSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypeRegistry.TEST_SIGN.get(), pPos, pBlockState);

        screenStart16 = new Vector2i(1, 1);
        screenLength16 = 14;
    }

    public SignText getSignText() {
        return signText;
    }

    public void setRawTexts(Map<String, String> languageAndText) {
        signText = new SignText(languageAndText);
        update();
    }

    private void update() {
        setChanged();
        if (level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    public short getLight() {
        return light;
    }

    public void setLight(short light) {
        this.light = light;
        update();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        signText.write(pTag);
        pTag.putShort(LIGHT, light);
        pTag.put("disguise", NbtUtils.writeBlockState(disguiseBlockState));
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        signText.write(tag);
        tag.putShort(LIGHT, light);
        tag.put("disguise", NbtUtils.writeBlockState(disguiseBlockState));
        return tag;
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        signText = SignText.read(pTag);
        light = pTag.getShort(LIGHT);
        HolderGetter<Block> holdergetter = this.level != null ? this.level.holderLookup(Registries.BLOCK) : BuiltInRegistries.BLOCK.asLookup();
        this.disguiseBlockState = NbtUtils.readBlockState(holdergetter, pTag.getCompound("disguise"));
        if (level != null && level.isClientSide) {
            setDisguise(disguiseBlockState);
        }
    }

    public void setDisguise(BlockState disguiseState) {
        disguiseBlockState = disguiseState;
        update();
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

    //needtest
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
