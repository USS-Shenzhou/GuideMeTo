package cn.ussshenzhou.tellmewhere;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;

/**
 * @author USS_Shenzhou
 */
public class ImageHelper {
    public static final int IMAGE_SIZE = 12;

    private static final String CATEGORY_STD = "std";
    private static final String CATEGORY_GOOGLE = "google";

    public static final LinkedHashSet<ImageInfo> IMAGES = new LinkedHashSet<>();

    static {
        //max index zz = 1295
        registerStd(0, "query");
        registerStd(1, "close");
        registerStd(2, "up");
        registerStd(3, "down");
        registerStd(4, "left");
        registerStd(5, "right");
        registerStd(6, "left_up");
        registerStd(7, "left_down");
        registerStd(8, "right_up");
        registerStd(9, "right_down");
        registerStd(10, "left_back");
        registerStd(11, "right_back");
        registerStd(12, "clockwise");
        registerStd(13, "counterclockwise");
        registerStd(14, "exit");
        registerStd(15, "tickets");
        registerStd(16, "metro");

        registerGoogle(102, "up");
        registerGoogle(103, "down");
        registerGoogle(104, "left");
        registerGoogle(105, "right");
        registerGoogle(106, "left_up");
        registerGoogle(107, "left_down");
        registerGoogle(108, "right_up");
        registerGoogle(109, "right_down");
        registerGoogle(110, "up_then_left");
        registerGoogle(111, "up_then_right");
        registerGoogle(112, "back_left");
        registerGoogle(113, "back_right");
        registerGoogle(114, "clockwise");
        registerGoogle(115, "counterclockwise");
        registerGoogle(116, "bypass_left");
        registerGoogle(117, "bypass_right");
        registerGoogle(118, "merge_left");
        registerGoogle(119, "merge_right");
        registerGoogle(120, "split_left");
        registerGoogle(121, "split_right");
        registerGoogle(122, "cross_passing_left");
        registerGoogle(123, "cross_passing_right");
        registerGoogle(124, "");
        registerGoogle(125, "");
        registerGoogle(126, "");
        registerGoogle(127, "");
        registerGoogle(128, "");
        registerGoogle(129, "");
        registerGoogle(130, "");
        registerGoogle(131, "");
        registerGoogle(132, "");
        registerGoogle(133, "");
        registerGoogle(134, "");
        registerGoogle(135, "");
        registerGoogle(136, "");
        registerGoogle(137, "");
        registerGoogle(138, "");
        registerGoogle(139, "");
        registerGoogle(140, "");
        registerGoogle(141, "");
        registerGoogle(142, "");
        registerGoogle(143, "");
        registerGoogle(144, "");
        registerGoogle(145, "");
        registerGoogle(146, "");
        registerGoogle(147, "");
        registerGoogle(148, "");
        registerGoogle(149, "");
        registerGoogle(150, "");
        registerGoogle(151, "");
        registerGoogle(152, "");
        registerGoogle(153, "");
        registerGoogle(154, "");
        registerGoogle(155, "");
        registerGoogle(156, "");
        registerGoogle(157, "");
        registerGoogle(158, "");
        registerGoogle(159, "");
        registerGoogle(160, "");
        registerGoogle(161, "");
        registerGoogle(162, "");
        registerGoogle(163, "");
        registerGoogle(164, "");
        registerGoogle(165, "");
        registerGoogle(166, "");
        registerGoogle(167, "");
        registerGoogle(168, "");
        registerGoogle(169, "");
        registerGoogle(170, "");
        registerGoogle(171, "");
        registerGoogle(172, "");
        registerGoogle(173, "");
        registerGoogle(174, "");
        registerGoogle(175, "");
        registerGoogle(176, "");
        registerGoogle(177, "");
        registerGoogle(178, "");
        registerGoogle(179, "");
    }

    private static void registerStd(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_STD);
    }

    private static void registerGoogle(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_GOOGLE);
    }

    private static void registerImage(int index, String resourceName, String category) {
        IMAGES.add(new ImageInfo(index, resourceName, category));
    }

    public static int fromString(String rawText) {
        try {
            rawText = rawText.replace(SignText.SPEC_PREFIX, "");
            int i = Integer.parseInt(rawText, 36);
            if (i >= IMAGES.size()) {
                return 0;
            } else {
                return i;
            }
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public static ImageInfo get(int index) {
        return ((ImageInfo) IMAGES.toArray()[index]);
    }

    public record ImageInfo(int index, String name, String category) {
        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ImageInfo that) {
                return this.index == that.index;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.index;
        }

        public ResourceLocation getForRender() {
            return new ResourceLocation(TellMeWhere.MODID, "block/signs/" + category + "/" + name);
        }

        public ResourceLocation getForFile() {
            return new ResourceLocation(TellMeWhere.MODID, "textures/block/signs/" + category + "/" + name + ".png");
        }
    }
}
