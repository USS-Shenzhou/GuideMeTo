package cn.ussshenzhou.tellmewhere;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;

/**
 * @author USS_Shenzhou
 */
public class ImageHelper {
    public static final int IMAGE_SIZE = 12;

    private static final String CATEGORY_STD = "std";
    private static final String CATEGORY_GOOGLE = "google";
    //-----TeaCon-----
    private static final String CATEGORY_TEACON = "teacon";

    public static final LinkedHashMap<Integer, ImageInfo> IMAGES = new LinkedHashMap<>();

    static {
        //max index zz = 1296
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
        registerGoogle(225, "dual_passing2");
        registerGoogle(226, "roundabout_left");
        registerGoogle(227, "roundabout_right");
        registerGoogle(228, "double_arrow_left");
        registerGoogle(229, "double_arrow_right");
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
        registerGoogle(240, "rendezvous");
        registerGoogle(241, "flag");

        registerGoogle(250, "warning");
        registerGoogle(251, "attention");
        registerGoogle(252, "query");
        registerGoogle(253, "info");
        registerGoogle(254, "pin");
        registerGoogle(255, "checkpoint");
        registerGoogle(256, "accessibility");
        registerGoogle(257, "accessible");
        registerGoogle(258, "productivity");

        registerGoogle(260, "subway");
        registerGoogle(261, "train");
        registerGoogle(262, "tram");
        registerGoogle(263, "bus");
        registerGoogle(264, "taxi");
        registerGoogle(265, "boat");
        registerGoogle(266, "flight");
        registerGoogle(267, "takeoff");
        registerGoogle(268, "land");
        registerGoogle(269, "gondola_lift");
        registerGoogle(270, "elevator");

        registerGoogle(280, "bathroom");
        registerGoogle(281, "baby_changing_room");
        registerGoogle(282, "medic");
        registerGoogle(283, "trash_can");
        registerGoogle(284, "restaurant");
        registerGoogle(285, "hail_to_go");
        registerGoogle(286, "hotel");
        registerGoogle(287, "currency_exchange");
        registerGoogle(288, "theater");
        registerGoogle(289, "warehouse");
        registerGoogle(290, "write");
        registerGoogle(291, "wifi");
        registerGoogle(292, "translate");
        registerGoogle(293, "timer");
        registerGoogle(294, "music");
        registerGoogle(295, "comment1");
        registerGoogle(296, "comment2");
        registerGoogle(297, "comment3");
        registerGoogle(298, "controller");
        registerGoogle(299, "house");
        registerGoogle(300, "city");
        registerGoogle(301, "home_work");
        registerGoogle(302, "forest");

        registerGoogle(304, "assured_workload");
        registerGoogle(305, "shopping_cart");
        registerGoogle(306, "precision_manufacturing");
        registerGoogle(307, "construction");
        registerGoogle(308, "handyman");
        registerGoogle(309, "add_a_photo");
        registerGoogle(310, "trophy");
        registerGoogle(311, "deceased");
        registerGoogle(312, "grass");

        registerGoogle(320, "no_stroller");
        registerGoogle(321, "do_not_touch");
        registerGoogle(322, "do_not_step");
        registerGoogle(323, "smoke_free");
        registerGoogle(324, "smoking_area");
        registerGoogle(325, "smoking_area");
        registerGoogle(326, "luggage");
        registerGoogle(327, "no_luggage");
        registerGoogle(328, "wifi_tethering_off");
        registerGoogle(329, "wifi_tethering_error");
        registerGoogle(330, "wifi_tethering");
        registerGoogle(331, "remove_moderator");
        registerGoogle(332, "shield_locked");
        registerGoogle(333, "shield_warning");
        registerGoogle(334, "verified");
        registerGoogle(335, "add_moderator");
        registerGoogle(336, "remove_road");
        registerGoogle(337, "add_road");
        registerGoogle(338, "edit_road");
        registerGoogle(339, "flyover");
        registerGoogle(340, "road");

        registerGoogle(342, "sprint");
        registerGoogle(343, "walk");
        registerGoogle(344, "run");

        registerGoogle(400, "airwave");

        registerGoogle(450, "badge");
        registerGoogle(451, "mic");
        registerGoogle(452, "speed");
        registerGoogle(453, "badge");

        registerImage(1000, "logo", CATEGORY_TEACON);
        registerImage(1001, "a", CATEGORY_TEACON);
        registerImage(1002, "b", CATEGORY_TEACON);
    }

    private static void registerStd(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_STD);
    }

    private static void registerGoogle(int index, String resourceName) {
        registerImage(index, resourceName, CATEGORY_GOOGLE);
    }

    private static void registerImage(int index, String resourceName, String category) {
        if (IMAGES.containsKey(index)) {
            LogUtils.getLogger().error("Conflict image index! Index {} of {} with category {} already exists. This should not happen.", index, resourceName, category);
            throw new RuntimeException();
        }
        IMAGES.put(index, new ImageInfo(index, resourceName, category));
    }

    public static int fromString(String rawText) {
        try {
            rawText = rawText.replace(SignText.SPEC_PREFIX, "");
            int i = Integer.parseInt(rawText, 36);
            if (i > 1295) {
                return 0;
            } else {
                return i;
            }
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public static ImageInfo get(int index) {
        return IMAGES.get(index);
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
            return ResourceLocation.fromNamespaceAndPath(TellMeWhere.MODID, "block/signs/" + category + "/" + name);
        }

        public ResourceLocation getForFile() {
            return ResourceLocation.fromNamespaceAndPath(TellMeWhere.MODID, "textures/block/signs/" + category + "/" + name + ".png");
        }
    }
}
