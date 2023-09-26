package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.advanced.TImageButton;
import cn.ussshenzhou.t88.gui.container.TScrollContainer;
import cn.ussshenzhou.t88.gui.widegt.TWidget;
import cn.ussshenzhou.tellmewhere.ImageHelper;
import cn.ussshenzhou.tellmewhere.SignText;

import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class ImageSelectPanel extends TScrollContainer {

    public ImageSelectPanel(List<ImageHelper.ImageInfo> images) {
        images.forEach(imageInfo -> {
            var button = new TImageButton(imageInfo.getForFile(), pButton -> {
                ((SignEditScreen) getTopParentScreen()).insertImageRaw(SignText.SPEC_PREFIX +
                        (imageInfo.index() < 35 ? "0" : "")
                        + Integer.toString(imageInfo.index(), 36)
                );
            });
            button.setNormalBorder(0);
            this.add(button);
        });
    }

    public boolean isInChildrenRange(double pMouseX, double pMouseY) {
        for (TWidget tWidget : this.children) {
            if (tWidget.isInRange(pMouseX, pMouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void layout() {
        int gap = 2;
        int number = 6;
        int w = (getUsableWidth() - (number - 1) * gap) / number;
        int i = 0;
        for (TWidget widget : this.children) {
            if (widget instanceof TImageButton imageButton) {
                imageButton.setBounds((gap + w) * (i % number), (gap + w) * (i / number), w, w);
                i++;
            }
        }
        super.layout();
    }
}