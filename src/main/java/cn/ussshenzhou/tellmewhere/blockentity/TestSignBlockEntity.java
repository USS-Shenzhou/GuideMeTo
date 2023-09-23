package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.tellmewhere.SignText;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * @author USS_Shenzhou
 */
public class TestSignBlockEntity extends BlockEntity {
    public static final String RAW_TEXT = "tmw_rawtext";
    public static final String LIGHT = "tmw_light";

    private SignText signText = new SignText();
    private int light = -1;

    public TestSignBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntityTypeRegistry.TEST_SIGN.get(), pPos, pBlockState);
    }

    public SignText getSignText() {
        return signText;
    }

    public void setRawText(String languageCode, String rawText) {
        signText.setRawText(languageCode, rawText);
        setChanged();
    }

    public int getLight() {
        return light;
    }

    public void setLight(int light) {
        this.light = light;
        setChanged();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        signText.write(pTag);
        pTag.putInt(LIGHT, light);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        signText = SignText.read(pTag);
        light = pTag.getInt(LIGHT);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = super.getUpdateTag();
        signText.write(tag);
        tag.putInt(LIGHT, light);
        return tag;
    }

    //needtest
    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
