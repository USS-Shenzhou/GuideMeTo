package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.event.ClearEditBoxFocusEvent;
import cn.ussshenzhou.t88.gui.widegt.TEditBox;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.t88.mixin.EditBoxAccessor;
import cn.ussshenzhou.tellmewhere.SignText;
import net.minecraft.client.gui.GuiGraphics;

import java.util.ArrayList;

/**
 * @author USS_Shenzhou
 */
public class SignContentPanel extends TPanel {
    private ArrayList<SignText.BakedText> bakedTextList;
    private int totalLength = 0;
    private String rawText;

    protected TEditBox editBox = new TEditBox() {
        @Override
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            if (checkNeedToKeepFocused(pMouseX, pMouseY)) {
                return false;
            }
            return super.mouseClicked(pMouseX, pMouseY, pButton);
        }

        @Override
        protected void onClearEditBoxFocusEvent(ClearEditBoxFocusEvent event) {
            if (checkNeedToKeepFocused(event.pMouseX, event.pMouseY)) {
                return;
            }
            super.onClearEditBoxFocusEvent(event);
        }

        private boolean checkNeedToKeepFocused(double pMouseX, double pMouseY) {
            if (!isInRange(pMouseX, pMouseY) && this.isFocused()) {
                //prevent losing focus when click images
                var screen = (SignEditScreen) SignContentPanel.this.getTopParentScreen();
                //noinspection RedundantIfStatement
                if (((ImageSelectPanel) screen.imageSelector.getSelectedTab().getContent()).isInChildrenRange(pMouseX, pMouseY)) {
                    return true;
                }
            }
            return false;
        }
    };

    public SignContentPanel(String rawText) {
        this.setRawText(rawText);
        this.add(editBox);
        editBox.setValue(rawText);
        ((EditBoxAccessor) editBox).setDisplayPos(0);
        editBox.addResponder(this::setRawText);
    }

    public boolean tryInsertImage(String raw) {
        if (editBox.isFocused()) {
            editBox.insertText(raw);
            return true;
        }
        return false;
    }

    @Override
    public void layout() {
        editBox.setBounds((int) (width * 0.2), height - 20 - 4, (int) (width * 0.6), 20);
        super.layout();
    }

    private void setRawText(String rawText) {
        this.rawText = rawText;
        bakeText();
    }

    private void bakeText() {
        //"左侧乘车&@04test"
        bakedTextList = SignText.bakeTexts(rawText);
        totalLength = 0;
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
        pose.translate(this.getXT() + (this.width - totalLength * scale) / 2f, this.getYT() + (this.height - 24) / 2f, 0);
        pose.scale(scale, scale, 0);
        for (SignText.BakedText text : bakedTextList) {
            text.render(pose, buffer, 0b11110000,null);
            pose.translate(text.getLength(), 0, 0);
        }
        pose.popPose();
    }

}
