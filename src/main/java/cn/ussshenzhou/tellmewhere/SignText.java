package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author USS_Shenzhou
 */
public class SignText {

    private Map<String, String> rawTexts = new HashMap<>();
    private List<BakedText> bakedTexts;

    public SignText(Map<String, String> rawTexts) {
        this();
        this.rawTexts = rawTexts;
    }

    public SignText(String languageCode, String rawText) {
        this();
        rawTexts.put(languageCode, rawText);
    }

    public SignText() {
        if (FMLEnvironment.dist.isClient()) {
            bakeTexts();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void bakeTexts() {
        //TODO
    }

    public String getRawText(String languageCode) {
        if (rawTexts.size() == 1) {
            return rawTexts.values().stream().findFirst().get();
        }
        return rawTexts.get(languageCode);
    }

    @OnlyIn(Dist.CLIENT)
    public String getRawText() {
        return getRawText(Minecraft.getInstance().getLanguageManager().getSelected());
    }

    public void setRawText(String languageCode, String rawText) {
        rawTexts.put(languageCode, rawText);
        if (FMLEnvironment.dist.isClient()) {
            bakeTexts();
        }
    }

    public static SignText read(CompoundTag tag) {
        var map = new FriendlyByteBuf(Unpooled.copiedBuffer(tag.getByteArray(TestSignBlockEntity.RAW_TEXT))).readMap(FriendlyByteBuf::readUtf, FriendlyByteBuf::readUtf);
        return new SignText(map);
    }

    public void write(CompoundTag tag) {
        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeMap(rawTexts, FriendlyByteBuf::writeUtf, FriendlyByteBuf::writeUtf);
        tag.putByteArray(TestSignBlockEntity.RAW_TEXT, buf.array());
    }

    public class BakedText {

    }
}
