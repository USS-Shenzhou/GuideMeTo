package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.item.SignBlockAndItemRegistryHelper;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * @author USS_Shenzhou
 */
public class ModBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, TellMeWhere.MODID);

    static {
        SignBlockAndItemRegistryHelper.registerBlock(BLOCKS);
    }
}
