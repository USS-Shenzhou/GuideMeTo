package cn.ussshenzhou.tellmewhere.network;

import cn.ussshenzhou.t88.network.annotation.Decoder;
import cn.ussshenzhou.t88.network.annotation.Encoder;
import cn.ussshenzhou.t88.network.annotation.NetPacket;
import cn.ussshenzhou.t88.network.annotation.ServerHandler;
import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.blockentity.SignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author USS_Shenzhou
 */
@NetPacket(modid = TellMeWhere.MODID)
public class EditSignPacket {
    public final BlockPos pos;
    public final Map<String, String> languageAndText;

    public EditSignPacket(BlockPos pos, Map<String, String> languageAndText) {
        this.pos = pos;
        this.languageAndText = languageAndText;
    }

    @Decoder
    public EditSignPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.languageAndText = buf.readMap(FriendlyByteBuf::readUtf, b -> b.readUtf());
    }

    @Encoder
    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeMap(languageAndText, FriendlyByteBuf::writeUtf, (b, s) -> b.writeUtf(s));
    }

    @ServerHandler
    public void handler(PlayPayloadContext context) {
        var level = context.level().orElseThrow();
        if (context.player().orElseThrow().isCreative() && level.isLoaded(pos) && level.getBlockEntity(pos) instanceof SignBlockEntity signBlockEntity) {
            signBlockEntity.setRawTexts(languageAndText);
        }
    }

}
