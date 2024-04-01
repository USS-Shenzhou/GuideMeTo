package cn.ussshenzhou.tellmewhere.item;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, TellMeWhere.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TellMeWhere.MODID);

    public static final Supplier<BlockItem> SIGN_HANG_THIN = ITEMS.register("sign_hang_thin", () -> new BlockItem(ModBlockRegistry.SIGN_HANG_THIN.get(), new Item.Properties()));
    public static final Supplier<BlockItem> SIGN_HANG_THICK = ITEMS.register("sign_hang_thick", () -> new BlockItem(ModBlockRegistry.SIGN_HANG_THICK.get(), new Item.Properties()));
    public static final Supplier<BlockItem> SIGN_STAND_THIN = ITEMS.register("sign_stand_thin", () -> new BlockItem(ModBlockRegistry.SIGN_STAND_THIN.get(), new Item.Properties()));
    public static final Supplier<BlockItem> SIGN_STAND_THICK = ITEMS.register("sign_stand_thick", () -> new BlockItem(ModBlockRegistry.SIGN_STAND_THICK.get(), new Item.Properties()));


    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .withTabsImage(new ResourceLocation(TellMeWhere.MODID, "textures/gui/up.png"))
            .title(Component.literal("Tell Me Where"))
            .build());

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (BuiltInRegistries.CREATIVE_MODE_TAB.getKey(event.getTab()).equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(TAB.get()))) {
            event.accept(SIGN_HANG_THIN.get());
            event.accept(SIGN_HANG_THICK.get());
            event.accept(SIGN_STAND_THIN.get());
            event.accept(SIGN_STAND_THICK.get());
        }
    }
}
