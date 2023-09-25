package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.*;

/**
 * @author USS_Shenzhou
 */
public class SignText {
    public static final String SPEC_PREFIX = "&@";

    private Map<String, String> rawTexts = new HashMap<>();

    private List<BakedText> bakedTexts = new LinkedList<>();
    private int totalLength;

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

    public String getRawText(String languageCode) {
        if (rawTexts.size() == 1) {
            return rawTexts.values().stream().findFirst().get();
        }
        return rawTexts.get(languageCode);
    }

    @OnlyIn(Dist.CLIENT)
    public String getRawText() {
        String rawText = getRawText(Minecraft.getInstance().getLanguageManager().getSelected());
        return rawText == null ? "" : rawText;
    }

    public void setRawText(String languageCode, String rawText) {
        rawTexts.put(languageCode, rawText);
        if (FMLEnvironment.dist.isClient()) {
            bakeTexts();
        }
    }

    public Map<String, String> getRawTexts() {
        return rawTexts;
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

    @OnlyIn(Dist.CLIENT)
    private void bakeTexts() {
        bakedTexts = bakeTexts(getRawText());
        totalLength = 0;
        bakedTexts.forEach(bakedText -> totalLength += bakedText.length);
    }

    @OnlyIn(Dist.CLIENT)
    public static ArrayList<BakedText> bakeTexts(String raw) {
        ArrayList<BakedText> list = new ArrayList<>();
        while (!raw.isEmpty()) {
            int index = raw.indexOf(SPEC_PREFIX);
            if (index == -1) {
                //no SPEC_PREFIX: "abcde"
                list.add(new BakedText(raw));
                break;
            }
            if (index == 0) {
                if (raw.length() <= 4) {
                    //"&@d" invalid mark
                    //"&@01" valid mark
                    list.add(new BakedText(raw));
                    break;
                } else {
                    //"&@01abcde" valid mark and go on
                    list.add(new BakedText(raw.substring(0, 4)));
                    raw = raw.substring(4);
                    continue;
                }
            }
            //"abc&@01de"
            list.add(new BakedText(raw.substring(0, index)));
            raw = raw.substring(index);
            continue;
        }
        return list;
    }

    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        for (int i = 0; i < bakedTexts.size(); i++) {
            var b = bakedTexts.get(i);
            b.render(poseStack, buffer, packedLight);
            poseStack.translate(b.length, 0, 0);
        }
        poseStack.popPose();
    }

    public static class BakedText {
        private BakedType type;
        private int length;

        private String text;
        private int imageIndex;

        public BakedText(String rawText) {
            if (rawText.length() == 4 && rawText.startsWith(SPEC_PREFIX)) {
                initImage(rawText);
            } else {
                initText(rawText);
            }
        }

        private void initText(String rawText) {
            type = BakedType.TEXT;
            length = Minecraft.getInstance().font.width(rawText);
            text = rawText;
        }

        private void initImage(String rawText) {
            type = BakedType.IMAGE;
            length = ImageHelper.IMAGE_SIZE;
            imageIndex = ImageHelper.fromString(rawText);
        }

        public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
            poseStack.pushPose();
            if (type == BakedType.TEXT) {
                //0.5: compensate shadow
                poseStack.translate(0, -4 + 0.5f, 0);
                Minecraft.getInstance().font.drawInBatch(text, 0, 0, 0xffffffff, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, packedLight, false);
            } else {
                float scale = ImageHelper.IMAGE_SIZE / 2f;
                poseStack.translate(scale, 0, 0);
                var image = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(ImageHelper.get(imageIndex).getForRender());
                var matrix = poseStack.last().pose();
                var consumer = buffer.getBuffer(RenderType.translucent());
                //float scale = ImageHelper.IMAGE_SIZE / 128f;
                consumer.vertex(matrix, -scale, -scale, 0).color(255, 255, 255, 255).uv(image.getU0(), image.getV0()).uv2(0b11110000, 0b11110000).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, -scale, scale, 0).color(255, 255, 255, 255).uv(image.getU0(), image.getV1()).uv2(0b11110000, 0b11110000).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, scale, scale, 0).color(255, 255, 255, 255).uv(image.getU1(), image.getV1()).uv2(0b11110000, 0b11110000).normal(1, 0, 0).endVertex();
                consumer.vertex(matrix, scale, -scale, 0).color(255, 255, 255, 255).uv(image.getU1(), image.getV0()).uv2(0b11110000, 0b11110000).normal(1, 0, 0).endVertex();
            }
            poseStack.popPose();
        }

        public int getLength() {
            return length;
        }
    }

    public enum BakedType {
        TEXT,
        IMAGE
    }
}
