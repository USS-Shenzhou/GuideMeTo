package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import cn.ussshenzhou.tellmewhere.ImageHelper;
import cn.ussshenzhou.tellmewhere.SignText;
import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ForgeHooksClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author USS_Shenzhou
 */
public class SignEditScreen extends TScreen {
    private final SignContentPanel defaultPanel;
    private final TTabPageContainer multiLanguageContainer;
    private final TLabelButton cancel = new TLabelButton(Component.translatable("gui.tmw.editor.cancel"), pButton -> onClose(true));
    private final TLabelButton done = new TLabelButton(Component.translatable("gui.tmw.editor.done"), pButton -> {
        //TODO
    });
    private final TTabPageContainer imageSelector = new TTabPageContainer();

    int gap = 5;


    public SignEditScreen(TestSignBlockEntity blockEntity) {
        super(Component.literal("Sign Edit Screen"));
        SignText text = blockEntity.getSignText();
        defaultPanel = new SignContentPanel(text.getRawText());
        this.add(defaultPanel);
        multiLanguageContainer = new TTabPageContainer();
        this.add(multiLanguageContainer);
        this.add(cancel);
        this.add(done);
        this.add(imageSelector);
        initMultiLanguageContainer(text);
        initImageSelector();
    }

    private void initMultiLanguageContainer(SignText text) {
        text.getRawTexts().forEach((language, raw) -> {
            if (language.equals(Minecraft.getInstance().getLanguageManager().getSelected())) {
                return;
            }
            multiLanguageContainer.newTab(Component.literal(language), new SignContentPanel(raw));
        });
        multiLanguageContainer.newTab(Component.literal("+"), new NewLanguagePanel()).setCloseable(false).setKeepFinal(true);
        multiLanguageContainer.selectTab(0);
    }

    private void initImageSelector() {
        HashMap<String, List<ImageHelper.ImageInfo>> categories = new HashMap<>();
        ImageHelper.IMAGES.forEach(imageInfo -> {
            categories.compute(imageInfo.category(), (key, imageInfos) -> {
                if (imageInfos == null) {
                    ArrayList<ImageHelper.ImageInfo> l = new ArrayList<>();
                    l.add(imageInfo);
                    return l;
                } else {
                    imageInfos.add(imageInfo);
                    return imageInfos;
                }
            });
        });
        categories.forEach((c, imageInfos) -> imageSelector.newTab(Component.literal(c), new ImageSelectPanel(imageInfos)).setCloseable(false));
        imageSelector.selectTab(0);
    }

    @Override
    public void layout() {
        defaultPanel.setBounds(gap, gap, (int) (width * 0.7 - 2 * gap), (int) (height * 0.5 - 2 * gap));
        LayoutHelper.BBottomOfA(multiLanguageContainer, gap, defaultPanel, defaultPanel.getWidth(), (int) (height * 0.4));

        cancel.setBounds(gap, (int) (height * 0.9 + (height * 0.1 - 20) / 2), (int) (width * 0.6 - 3 * gap) / 2, 20);
        LayoutHelper.BRightOfA(done, (int) (width * 0.1), cancel);

        LayoutHelper.BRightOfA(imageSelector, gap, defaultPanel, width - defaultPanel.getXT() - defaultPanel.getWidth() - 2 * gap, height - 2 * gap);
        super.layout();
    }

    @Override
    protected void renderBackGround(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(0, 0, width, height, 0x80000000);
    }

    public static class NewLanguagePanel extends TPanel {

    }
}
