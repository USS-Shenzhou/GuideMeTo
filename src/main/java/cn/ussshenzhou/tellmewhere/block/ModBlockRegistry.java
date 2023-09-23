package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author USS_Shenzhou
 */
public class ModBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TellMeWhere.MODID);

    public static final RegistryObject<Block> TEST_SIGN = BLOCKS.register("test_sign", TestSign::new);
}
