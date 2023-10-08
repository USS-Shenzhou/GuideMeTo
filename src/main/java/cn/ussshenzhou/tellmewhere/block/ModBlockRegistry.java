package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

/**
 * @author USS_Shenzhou
 */
public class ModBlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TellMeWhere.MODID);

    public static final RegistryObject<Block> SIGN_HANG_THIN = BLOCKS.register("sign_hang_thin",
            () -> new BaseSignBlock(new Vector3f(1, 1, 7), 14, 8, 2, 1)
    );
    public static final RegistryObject<Block> SIGN_HANG_THICK = BLOCKS.register("sign_hang_thick",
            () -> new BaseSignBlock(new Vector3f(1, 1, 6), 14, 8, 4, 1)
    );
    public static final RegistryObject<Block> SIGN_STAND_THIN = BLOCKS.register("sign_stand_thin",
            () -> new BaseSignBlock(new Vector3f(1, 7, 7), 14, 8, 2, 1)
    );
    public static final RegistryObject<Block> SIGN_STAND_THICK = BLOCKS.register("sign_stand_thick",
            () -> new BaseSignBlock(new Vector3f(1, 7, 6), 14, 8, 4, 1)
    );
}
