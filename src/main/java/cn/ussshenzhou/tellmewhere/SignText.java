package cn.ussshenzhou.tellmewhere;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.TextAlignment;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.util.thread.EffectiveSide;

import java.util.*;

/**
 * @author USS_Shenzhou
 */
public class SignText {
    public static final Codec<SignText> CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(SignText::new, SignText::getRawTexts);

    public static final String SPEC_PREFIX = "&@";
    public static final Style STYLE = Style.EMPTY.withoutShadow();

    private Map<String, String> rawTexts = new HashMap<>();
    private List<BakedText> bakedTexts = new LinkedList<>();
    private int totalLength;
    private float usableWidth = Integer.MAX_VALUE;
    private float usedWidth;
    private float textHorizontalCompressFactor = 1;
    private float totalCompressFactor = 1;

    public SignText(Map<String, String> rawTexts) {
        this();
        this.rawTexts = rawTexts;
        if (FMLEnvironment.getDist().isClient()) {
            bakeTexts();
        }
    }

    public SignText() {
    }

    public String getRawText(String languageCode) {
        if (rawTexts.size() == 1) {
            return rawTexts.values().stream().findFirst().get();
        }
        return rawTexts.get(languageCode);
    }

    public Map<String, String> getRawTexts() {
        return rawTexts;
    }

    //----------client----------

    public void setUsableWidth(float usableWidth16, float screenHeight) {
        this.usableWidth = usableWidth16 / (screenHeight / ImageHelper.IMAGE_SIZE);
        bakeTexts();
    }

    public String getRawText() {
        String rawText = getRawText(Minecraft.getInstance().getLanguageManager().getSelected());
        return rawText == null ? "" : rawText;
    }

    private void bakeTexts() {
        bakedTexts = bakeTexts(getRawText());
        totalLength = 0;
        bakedTexts.forEach(bakedText -> totalLength += bakedText.length);
        checkWidth();
    }

    private void checkWidth() {
        //init
        totalCompressFactor = 1;
        textHorizontalCompressFactor = 1;
        //if need Compress
        if (usableWidth >= totalLength) {
            usedWidth = totalLength;
            return;
        }
        usedWidth = usableWidth;
        //try compress texts
        float textLength = 0;
        for (BakedText bakedText : bakedTexts) {
            if (bakedText.type == BakedType.TEXT) {
                textLength += bakedText.length;
            }
        }
        float textNeedCompress = (usableWidth - (totalLength - textLength)) / textLength;
        float maxTextCompress = "zh_cn".equals(Minecraft.getInstance().getLanguageManager().getSelected()) ? 0.7f : 0.6f;
        if (textNeedCompress >= maxTextCompress) {
            textHorizontalCompressFactor = textNeedCompress;
            return;
        }
        //if not enough, then compress all
        textHorizontalCompressFactor = maxTextCompress;
        totalCompressFactor = usableWidth / (totalLength - textLength + textLength * textHorizontalCompressFactor);
    }

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

    public void renderInWorld(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int color) {
        poseStack.pushPose();
        poseStack.translate((usableWidth - usedWidth) / 2, 0, 0);
        poseStack.scale(totalCompressFactor, totalCompressFactor, 1);
        for (BakedText b : bakedTexts) {
            if (b.type == BakedType.TEXT) {
                poseStack.pushPose();
                poseStack.scale(textHorizontalCompressFactor, 1, 1);
                b.renderInWorld(poseStack, submitNodeCollector, packedLight, color);
                poseStack.popPose();
                poseStack.translate(b.length * textHorizontalCompressFactor, 0, 0);
            } else {
                b.renderInWorld(poseStack, submitNodeCollector, packedLight, color);
                poseStack.translate(b.length, 0, 0);
            }
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
            if (imageIndex == -1) {
                initText(rawText);
            }
        }

        public void renderInWorld(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int color) {
            if (type == BakedType.TEXT) {
                renderTextInWorld(poseStack, submitNodeCollector, packedLight, color);
            } else if (type == BakedType.IMAGE) {
                renderImageInWorld(poseStack, submitNodeCollector, packedLight, color);
            }
        }

        public void renderInGui(GuiGraphicsExtractor graphics, int foreground) {
            if (type == BakedType.TEXT) {
                renderTextInGui(graphics, foreground);
            } else if (type == BakedType.IMAGE) {
                renderImageInGui(graphics, foreground);
            }
        }

        public void renderTextInWorld(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int color) {
            poseStack.pushPose();
            //0.5: to compensate shadow
            poseStack.translate(0, -4 + 0.5f, 0);
            var c = Component.literal(text).setStyle(STYLE.withColor(color));
            submitNodeCollector.submitText(poseStack, 0, 0, c.getVisualOrderText(), false, Font.DisplayMode.NORMAL, packedLight, color, 0, 0);
            poseStack.popPose();
        }

        public void renderTextInGui(GuiGraphicsExtractor graphics, int foreground) {
            graphics.pose().pushMatrix();
            graphics.pose().translate(0, -4 + 0.5f);
            graphics.textRenderer().accept(TextAlignment.LEFT, 0, 0, Component.literal(text).setStyle(STYLE.withColor(foreground)));
            graphics.pose().popMatrix();
        }

        @SuppressWarnings("deprecation")
        public void renderImageInWorld(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int packedLight, int color) {
            poseStack.pushPose();
            float size = ImageHelper.IMAGE_SIZE / 2f;
            poseStack.translate(size, 0, 0);
            var i = ImageHelper.get(imageIndex);
            if (i == null) {
                return;
            }
            var image = ((TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS)).getSprite(i.getForRender());
            submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.cutoutMovingBlock(), (pose, buffer) -> {
                var c = i.hasColor() ? -1 : color;
                buffer.addVertex(pose, -size, -size, 0).setColor(c).setUv(image.getU0(), image.getV0()).setLight(packedLight).setNormal(1, 0, 0);
                buffer.addVertex(pose, -size, size, 0).setColor(c).setUv(image.getU0(), image.getV1()).setLight(packedLight).setNormal(1, 0, 0);
                buffer.addVertex(pose, size, size, 0).setColor(c).setUv(image.getU1(), image.getV1()).setLight(packedLight).setNormal(1, 0, 0);
                buffer.addVertex(pose, size, -size, 0).setColor(c).setUv(image.getU1(), image.getV0()).setLight(packedLight).setNormal(1, 0, 0);
            });
            poseStack.popPose();
        }

        public void renderImageInGui(GuiGraphicsExtractor graphics, int foreground) {
            graphics.pose().pushMatrix();
            int size = (int) (ImageHelper.IMAGE_SIZE / 2f);
            graphics.pose().translate(size, 0);
            var i = ImageHelper.get(imageIndex);
            if (i == null) {
                return;
            }
            graphics.innerBlit(RenderPipelines.GUI_TEXTURED, i.getForFile(), -size, size, -size, size, 0, 1, 0, 1, i.hasColor() ? -1 : foreground);
            graphics.pose().popMatrix();
        }

        public int getLength() {
            return length;
        }
    }

    public enum BakedType {
        TEXT, IMAGE
    }
}
