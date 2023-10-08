package cn.ussshenzhou.tellmewhere.item;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author USS_Shenzhou
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TellMeWhere.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TellMeWhere.MODID);

    public static final RegistryObject<BlockItem> SIGN_HANG_THIN = ITEMS.register("sign_hang_thin", () -> new BlockItem(ModBlockRegistry.SIGN_HANG_THIN.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> SIGN_HANG_THICK = ITEMS.register("sign_hang_thick", () -> new BlockItem(ModBlockRegistry.SIGN_HANG_THICK.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> SIGN_STAND_THIN = ITEMS.register("sign_stand_thin", () -> new BlockItem(ModBlockRegistry.SIGN_STAND_THIN.get(), new Item.Properties()));
    public static final RegistryObject<BlockItem> SIGN_STAND_THICK = ITEMS.register("sign_stand_thick", () -> new BlockItem(ModBlockRegistry.SIGN_STAND_THICK.get(), new Item.Properties()));


    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .withTabsImage(new ResourceLocation(TellMeWhere.MODID, "textures/block/signs/std_up.png"))
            .title(Component.literal("Tell Me Where"))
            .build());

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == TAB.getKey()) {
            event.accept(SIGN_HANG_THIN);
            event.accept(SIGN_HANG_THICK);
            event.accept(SIGN_STAND_THIN);
            event.accept(SIGN_STAND_THICK);
        }
    }
}
