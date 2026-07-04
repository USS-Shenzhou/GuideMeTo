package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.tellmewhere.SignText;
import cn.ussshenzhou.tellmewhere.block.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author USS_Shenzhou
 */
public class SignBlockData {

    public static final Codec<SignBlockData> CODEC = RecordCodecBuilder.create(ins -> ins.group(
            Codec.SHORT.fieldOf("light").forGetter(o -> o.light),
            BlockState.CODEC.fieldOf("disguiseBlockState").forGetter(o -> o.disguiseBlockState),
            Codec.BOOL.fieldOf("slave").forGetter(o -> o.slave),
            Codec.INT.fieldOf("screenLength16").forGetter(o -> o.screenLength16),
            SignText.CODEC.fieldOf("signText").forGetter(o -> o.signText),
            Codec.INT.fieldOf("backgroundRgba").forGetter(o -> o.backgroundArgb),
            Codec.INT.fieldOf("foregroundRgba").forGetter(o -> o.foregroundArgb)
    ).apply(ins, SignBlockData::new));

    private short light = 15;
    private BlockState disguiseBlockState = ModBlocks.MISSING.get().defaultBlockState();
    private boolean slave = false;
    private int screenLength16;
    private SignText signText = new SignText();
    private int backgroundArgb = 0xffffffff;
    private int foregroundArgb = 0xff000000;

    public SignBlockData(short light, BlockState disguiseBlockState, boolean slave, int screenLength16, SignText signText, int backgroundArgb, int foregroundArgb) {
        this.light = light;
        this.disguiseBlockState = disguiseBlockState;
        this.slave = slave;
        this.screenLength16 = screenLength16;
        this.signText = signText;
        this.backgroundArgb = backgroundArgb;
        this.foregroundArgb = foregroundArgb;
    }

    public SignBlockData() {
    }

    public SignText getSignText() {
        return signText;
    }

    public void setSignText(SignText signText) {
        this.signText = signText;
    }

    public short getLight() {
        return light;
    }

    public void setLight(short light) {
        this.light = light;
    }

    public BlockState getDisguiseBlockState() {
        return disguiseBlockState;
    }

    public void setDisguiseBlockState(BlockState disguiseBlockState) {
        this.disguiseBlockState = disguiseBlockState;
    }

    public boolean isSlave() {
        return slave;
    }

    public void setSlave(boolean slave) {
        this.slave = slave;
    }

    public int getScreenLength16() {
        return screenLength16;
    }

    public void setScreenLength16(int screenLength16) {
        this.screenLength16 = screenLength16;
    }

    public int getBackgroundArgb() {
        return backgroundArgb;
    }

    public void setBackgroundArgb(int backgroundArgb) {
        this.backgroundArgb = backgroundArgb;
    }

    public int getForegroundArgb() {
        return foregroundArgb;
    }

    public void setForegroundArgb(int foregroundArgb) {
        this.foregroundArgb = foregroundArgb;
    }
}
