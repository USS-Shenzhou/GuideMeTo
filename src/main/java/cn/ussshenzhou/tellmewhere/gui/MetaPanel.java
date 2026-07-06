package cn.ussshenzhou.tellmewhere.gui;

import cn.ussshenzhou.t88.gui.advanced.TLabelButton;
import cn.ussshenzhou.t88.gui.combine.TTitledSimpleConstrainedEditBox;
import cn.ussshenzhou.t88.gui.container.TVerticalScrollContainer;
import cn.ussshenzhou.t88.gui.util.Border;
import cn.ussshenzhou.t88.gui.util.LayoutHelper;
import cn.ussshenzhou.t88.gui.widegt.TLabel;
import net.minecraft.commands.arguments.HexColorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.ARGB;

/**
 * @author USS_Shenzhou
 */
public class MetaPanel extends TVerticalScrollContainer {
    private final TTitledSimpleConstrainedEditBox foreground = new TTitledSimpleConstrainedEditBox(
            Component.translatable("gui.gmt.editor.foreground"),
            HexColorArgument.hexColor()
    );

    private final TTitledSimpleConstrainedEditBox background = new TTitledSimpleConstrainedEditBox(
            Component.translatable("gui.gmt.editor.background"),
            HexColorArgument.hexColor()
    );

    private final TLabel presets = new TLabel(Component.translatable("gui.gmt.editor.presets"));
    private static final Style STYLE = Style.EMPTY.withoutShadow();
    //private final ColorButton normal = new ColorButton(Component.translatable("gui.gmt.editor.presets.normal").setStyle(STYLE), 0xffffff, 0x000000);
    private final ColorButton striking = new ColorButton(Component.translatable("gui.gmt.editor.presets.striking").setStyle(STYLE), 0x000000, 0xfae222);
    private final ColorButton attention = new ColorButton(Component.translatable("gui.gmt.editor.presets.attention").setStyle(STYLE), 0x000000, 0xfa8000);
    private final ColorButton forbid = new ColorButton(Component.translatable("gui.gmt.editor.presets.forbid").setStyle(STYLE), 0x000000, 0xfa0b00);
    private final ColorButton reverse = new ColorButton(Component.translatable("gui.gmt.editor.presets.reverse").setStyle(STYLE), 0x000000, 0xffffff);


    public MetaPanel(int foregroundColor, int backgroundColor) {
        this.addAll(foreground, background, presets, /*normal,*/ striking, attention, forbid, reverse);
        this.setBackground(0);
        this.foreground.getComponent().setValue(String.format("%06X", (0xffffff & foregroundColor)));
        this.background.getComponent().setValue(String.format("%06X", (0xffffff & backgroundColor)));
    }

    @Override
    public void layout() {
        foreground.setBounds(4, 4, 60, foreground.getPreferredSize().y);
        LayoutHelper.BBottomOfA(background, 4, foreground);
        presets.setBounds(foreground.getXT() + foreground.getWidth() + 8, 4, presets.getPreferredSize());
        LayoutHelper.BBottomOfA(reverse/*normal*/, 2, presets, 60, 20);
        LayoutHelper.BRightOfA(striking, 4, reverse/*normal*/);
        LayoutHelper.BRightOfA(attention, 4, striking);
        LayoutHelper.BRightOfA(forbid, 4, attention);
        //LayoutHelper.BBottomOfA(reverse, 14, normal);
        super.layout();
    }

    public int getForegroundColor() {
        return rgb2argb(parse(foreground.getComponent().getValue(), 0xffffff));
    }

    public int getBackgroundColor() {
        return rgb2argb(parse(background.getComponent().getValue(), 0x000000));
    }

    public int parse(String colorString, int defaultValue) {
        try {
            Integer.parseInt(colorString, 16);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
        return switch (colorString.length()) {
            case 3 -> ARGB.color(
                    duplicateDigit(Integer.parseInt(colorString, 0, 1, 16)),
                    duplicateDigit(Integer.parseInt(colorString, 1, 2, 16)),
                    duplicateDigit(Integer.parseInt(colorString, 2, 3, 16))
            );
            case 6 -> ARGB.color(Integer.parseInt(colorString, 0, 2, 16), Integer.parseInt(colorString, 2, 4, 16), Integer.parseInt(colorString, 4, 6, 16));
            default -> defaultValue;
        };
    }

    private static int duplicateDigit(int digit) {
        return digit * 17;
    }

    public static int rgb2argb(int rgb) {
        return 0xff000000 | rgb;
    }

    public class ColorButton extends TLabelButton {
        private final int foregroundColor;
        private final int backgroundColor;

        public ColorButton(Component s, int foregroundColor, int backgroundColor) {
            super(s, b -> {
                MetaPanel.this.foreground.getComponent().setValue(String.format("%06X", (0xffffff & foregroundColor)));
                MetaPanel.this.background.getComponent().setValue(String.format("%06X", (0xffffff & backgroundColor)));
            });
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.setNormalBackGround(rgb2argb(backgroundColor));
            this.setHoverBackGround(rgb2argb(backgroundColor));
            this.setForeground(rgb2argb(foregroundColor));
            this.setBorder(null);
        }

        @Override
        public void tickT() {
            if (this.button.isHoveredOrFocused()) {
                if (this.border == null) {
                    this.setBorder(new Border(0xffffffff, 1));
                }
            } else {
                this.setBorder(null);
            }
            super.tickT();
        }
    }
}
