package cn.ussshenzhou.tellmewhere.item;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.util.SignBlockAndItemRegistryHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;


/**
 * @author USS_Shenzhou
 */
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, TellMeWhere.MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, TellMeWhere.MODID);

    static {
        SignBlockAndItemRegistryHelper.registerItem(ITEMS);
    }

    public static final Supplier<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .icon(() -> new ItemStack(SignBlockAndItemRegistryHelper.SIGN_ITEMS.get("sign_hang_thin").get()))
            .title(Component.literal("Tell Me Where"))
            .build());

    @SubscribeEvent
    public static void onBuildContents(BuildCreativeModeTabContentsEvent event) {
        if (BuiltInRegistries.CREATIVE_MODE_TAB.getKey(event.getTab()).equals(BuiltInRegistries.CREATIVE_MODE_TAB.getKey(TAB.get()))) {
            event.acceptAll(SignBlockAndItemRegistryHelper.SIGN_ITEMS.values().stream().map(s -> new ItemStack(s.get())).toList());
        }
    }
}
