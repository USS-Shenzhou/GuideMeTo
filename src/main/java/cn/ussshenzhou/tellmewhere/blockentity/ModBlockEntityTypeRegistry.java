package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * @author USS_Shenzhou
 */
public class ModBlockEntityTypeRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, TellMeWhere.MODID);

    public static final RegistryObject<BlockEntityType<TestSignBlockEntity>> TEST_SIGN = BLOCK_ENTITIES.register("test_sign", () -> BlockEntityType.Builder.of(
            TestSignBlockEntity::new, ModBlockRegistry.TEST_SIGN.get()
    ).build(DSL.remainderType()));
}
