package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.advanced.TSuggestedEditBox;
import cn.ussshenzhou.t88.gui.container.TTabPageContainer;
import cn.ussshenzhou.t88.gui.util.HorizontalAlignment;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import cn.ussshenzhou.t88.gui.widegt.TPanel;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

/**
 * @author USS_Shenzhou
 */
public class NewLanguagePanel extends TPanel {
    private TLabel title = new TLabel(Component.literal("Language:"));

    private TSuggestedEditBox language = new TSuggestedEditBox(dispatcher -> {
        var l = Minecraft.getInstance().getLanguageManager();
        var current = l.getSelected();
        l.getLanguages().keySet().forEach(code -> {
            if (!current.equals(code)) {
                dispatcher.register(Commands.literal(code));
            }
        });
    });

    private TLabelButton create = new TLabelButton(Component.literal("Create"), pButton -> {
        var s = language.getEditBox().getValue();
        var multi = this.getParentInstanceOf(TTabPageContainer.class);
        var tab = multi.newTab(Component.literal(s), new SignContentPanel(""));
        multi.selectTab(tab);
    });

    public NewLanguagePanel() {
        this.addAll(title, language, create);
        title.setHorizontalAlignment(HorizontalAlignment.RIGHT);
    }

    @Override
    public void layout() {
        title.setBounds(width / 2 - 100 - 2, height / 2 - 20 - 4, 100, 20);
        LayoutHelper.BRightOfA(language, 4, title);
        create.setBounds(width / 2 - 50, height / 2 + 4, 100, 20);
        super.layout();
    }
}
