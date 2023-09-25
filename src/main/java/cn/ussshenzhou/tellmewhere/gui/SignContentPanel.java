package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.util.T88Util;
import cn.ussshenzhou.tellmewhere.SignText;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class SignContentPanel extends TPanel {
    private ArrayList<SignText.BakedText> bakedTextList;
    private int totalLength = 0;
    private String rawText;


    public SignContentPanel(String rawText) {
        this.rawText = rawText;
        bakeText();
    }

    @Override
    public void layout() {

        super.layout();
    }

    private void bakeText() {
        //"左侧乘车&@04test"
        bakedTextList = SignText.bakeTexts(rawText);
        bakedTextList.forEach(bakedText -> totalLength += bakedText.getLength());
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        var pose = graphics.pose();
        var buffer = graphics.bufferSource();
        pose.pushPose();
        //TODO dynamic change
        float scale = 2f;
        pose.translate((this.width - totalLength * scale) / 2f, this.height / 2f, 0);
        pose.scale(scale, scale, 0);
        for (SignText.BakedText text : bakedTextList) {
            text.render(pose, buffer, 0b11110000);
            pose.translate(text.getLength(), 0, 0);
        }
        pose.popPose();
    }

}
