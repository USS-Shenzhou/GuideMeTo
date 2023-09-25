package cn.ussshenzhou.tellmewhere;

import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashSet;

/**
 * @author USS_Shenzhou
 */
public class ImageHelper {
    public static final int IMAGE_SIZE = 12;

    private static final String CATEGORY_STD = "Standard";

    public static final LinkedHashSet<ImageInfo> IMAGES = new LinkedHashSet<>();

    static {
        registerImage(0, "std_question_mark", CATEGORY_STD);
        registerImage(1, "std_no_entry", CATEGORY_STD);
        registerImage(2, "std_up", CATEGORY_STD);
        registerImage(3, "std_down", CATEGORY_STD);
        registerImage(4, "std_left", CATEGORY_STD);
        registerImage(5, "std_right", CATEGORY_STD);
        registerImage(6, "std_left_up", CATEGORY_STD);
        registerImage(7, "std_left_down", CATEGORY_STD);
        registerImage(8, "std_right_up", CATEGORY_STD);
        registerImage(9, "std_right_down", CATEGORY_STD);
        registerImage(10, "std_left_back", CATEGORY_STD);
        registerImage(11, "std_right_back", CATEGORY_STD);
    }

    private static void registerImage(int index, String resourceName, String category) {
        IMAGES.add(new ImageInfo(index, resourceName, category));
    }

    public static int fromString(String rawText) {
        rawText = rawText.replace(SignText.SPEC_PREFIX, "");
        int i = Integer.parseInt(rawText, 36);
        if (i >= IMAGES.size()) {
            return 0;
        } else {
            return i;
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
            return new ResourceLocation(TellMeWhere.MODID, "block/signs/" + name);
        }

        public ResourceLocation getForFile() {
            return new ResourceLocation(TellMeWhere.MODID, "textures/block/signs/" + name + ".png");
        }
    }
}
