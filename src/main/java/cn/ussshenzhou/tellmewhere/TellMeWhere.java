package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import cn.ussshenzhou.tellmewhere.blockentity.ModBlockEntityTypeRegistry;
import cn.ussshenzhou.tellmewhere.item.ModItemRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;


/**
 * @author USS_Shenzhou
 */
@Mod(TellMeWhere.MODID)
public class TellMeWhere {

    public static final String MODID = "tell_me_where";

    public TellMeWhere() {
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModBlockRegistry.BLOCKS.register(modEventBus);
        ModBlockEntityTypeRegistry.BLOCK_ENTITIES.register(modEventBus);
        ModItemRegistry.ITEMS.register(modEventBus);
        ModItemRegistry.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
