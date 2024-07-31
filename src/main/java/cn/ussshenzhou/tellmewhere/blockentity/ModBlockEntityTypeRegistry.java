package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.item.SignBlockAndItemRegistryHelper;
import com.mojang.datafixers.DSL;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Supplier;


/**
 * @author USS_Shenzhou
 */
public class ModBlockEntityTypeRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, TellMeWhere.MODID);

    public static final Supplier<BlockEntityType<SignBlockEntity>> TEST_SIGN = BLOCK_ENTITIES.register("sign", () -> BlockEntityType.Builder.of(
            SignBlockEntity::new,
            SignBlockAndItemRegistryHelper.SIGN_BLOCKS.values()
                    .stream().map(Supplier::get)
                    .toArray(Block[]::new)
    ).build(DSL.remainderType()));
}
