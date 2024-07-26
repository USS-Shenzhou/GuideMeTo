package cn.ussshenzhou.tellmewhere;

import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import cn.ussshenzhou.tellmewhere.blockentity.ModBlockEntityTypeRegistry;
import cn.ussshenzhou.tellmewhere.item.ModItemRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;


/**
 * @author USS_Shenzhou
 */
@Mod(TellMeWhere.MODID)
public class TellMeWhere {

    public static final String MODID = "guide_me_to";

    public TellMeWhere(IEventBus modEventBus) {
        ModBlockRegistry.BLOCKS.register(modEventBus);
        ModBlockEntityTypeRegistry.BLOCK_ENTITIES.register(modEventBus);
        ModItemRegistry.ITEMS.register(modEventBus);
        ModItemRegistry.CREATIVE_MODE_TABS.register(modEventBus);
    }
}
