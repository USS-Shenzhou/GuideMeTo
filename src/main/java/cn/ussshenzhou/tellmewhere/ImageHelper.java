package cn.ussshenzhou.tellmewhere;

import com.mojang.logging.LogUtils;
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

        registerGoogle(202, "up");
        registerGoogle(203, "down");
        registerGoogle(204, "left");
        registerGoogle(205, "right");
        registerGoogle(206, "left_up");
        registerGoogle(207, "left_down");
        registerGoogle(208, "right_up");
        registerGoogle(209, "right_down");
        registerGoogle(210, "up_then_left");
        registerGoogle(211, "up_then_right");
        registerGoogle(212, "back_left");
        registerGoogle(213, "back_right");
        registerGoogle(214, "clockwise");
        registerGoogle(215, "counterclockwise");
        registerGoogle(216, "bypass_left");
        registerGoogle(217, "bypass_right");
        registerGoogle(218, "merge_left");
        registerGoogle(219, "merge_right");
        registerGoogle(220, "split_left");
        registerGoogle(221, "split_right");
        registerGoogle(222, "cross_passing_left");
        registerGoogle(223, "cross_passing_right");
        registerGoogle(224, "dual_passing");

        registerGoogle(230, "start");
        registerGoogle(231, "end");
        registerGoogle(232, "close");
        registerGoogle(233, "block1");
        registerGoogle(234, "block2");
        registerGoogle(235, "block3");
        registerGoogle(236, "entrance1");
        registerGoogle(237, "exit1");
        registerGoogle(238, "entrance2");
        registerGoogle(239, "exit2");

        registerGoogle(250, "warning");
        registerGoogle(236, "attention");
        registerGoogle(237, "query");
        registerGoogle(238, "info");
        registerGoogle(239, "pin");
        registerGoogle(240, "checkpoint");
        registerGoogle(241, "accessibility");
        registerGoogle(242, "accessible");

        registerGoogle(250, "subway");
        registerGoogle(251, "train");
        registerGoogle(252, "tram");
        registerGoogle(253, "bus");
        registerGoogle(254, "taxi");
        registerGoogle(255, "boat");
        registerGoogle(256, "flight");
        registerGoogle(257, "takeoff");
        registerGoogle(258, "land");

        registerGoogle(260, "bathroom");
        registerGoogle(261, "baby_changing_room");
        registerGoogle(262, "medic");
        registerGoogle(263, "trash_can");
        registerGoogle(264, "restaurant");
        registerGoogle(265, "hail_to_go");
        registerGoogle(266, "hotel");
        registerGoogle(267, "currency_exchange");
        registerGoogle(268, "theater");
        registerGoogle(269, "warehouse");
        registerGoogle(270, "write");
        registerGoogle(271, "wifi");
        registerGoogle(272, "translate");
        registerGoogle(273, "timer");
        registerGoogle(274, "music");

        registerGoogle(300, "no_stroller");
        registerGoogle(301, "do_not_touch");
        registerGoogle(302, "do_not_step");
        registerGoogle(303, "no_step");
        registerGoogle(304, "smoke_free");
        registerGoogle(305, "smoking_area");

        registerGoogle(320, "airwave");
    }

    private static void registerStd(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_STD);
    }

    private static void registerGoogle(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_GOOGLE);
    }

    private static void registerImage(int index, String resourceName, String category) {
        if (!IMAGES.add(new ImageInfo(index, resourceName, category))) {
            LogUtils.getLogger().error("Conflict image index! Index {} of {} with category {} already exists. This should not happen.", index, resourceName, category);
        }
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
