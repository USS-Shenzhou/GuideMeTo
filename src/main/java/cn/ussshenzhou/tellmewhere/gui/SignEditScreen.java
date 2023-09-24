package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.screen.TScreen;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TButton;
import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.TextAndImageButton;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.ForgeHooksClient;

/**
 * @author USS_Shenzhou
 */
public class SignEditScreen extends TScreen {
    private final SignContentPanel defaultPanel = new SignContentPanel();
    private final TTabPageContainer multiLanguageContainer = new TTabPageContainer();
    private final TLabelButton cancel = new TLabelButton(Component.translatable("gui.tmw.editor.cancel"), pButton -> onClose(true));
    private final TLabelButton done = new TLabelButton(Component.translatable("gui.tmw.editor.done"), pButton -> {
        //TODO
    });
    private final TTabPageContainer imageSelector = new TTabPageContainer();

    int gap = 5;


    public SignEditScreen(TestSignBlockEntity blockEntity) {
        super(Component.literal("Sign Edit Screen"));
        this.add(defaultPanel);
        this.add(multiLanguageContainer);
        this.add(cancel);
        this.add(done);
        //TODO init
    }

    @Override
    public void layout() {
        defaultPanel.setBounds(gap, gap, (int) (width * 0.7 - 2 * gap), (int) (height * 0.5 - 2 * gap));
        LayoutHelper.BBottomOfA(multiLanguageContainer, gap, defaultPanel, defaultPanel.getWidth(), (int) (height * 0.4));
        cancel.setBounds(gap, (int) (height * 0.9 + (height * 0.1 - 20) / 2), (int) (width * 0.7 - 3 * gap) / 2, 20);
        LayoutHelper.BRightOfA(done, (int) (width * 0.1), cancel);
        LayoutHelper.BRightOfA(imageSelector, 5, defaultPanel, width - 3 * gap, height - 2 * gap);
        super.layout();
    }

    @Override
    protected void renderBackGround(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.fill(0, 0, width, height, 0x80000000);
    }
}
