package cn.ussshenzhou.tellmewhere.blockentity;

import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.ModBlockRegistry;
import com.mojang.datafixers.DSL;
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
            ModBlockRegistry.SIGN_HANG_THIN.get(),
            ModBlockRegistry.SIGN_HANG_THICK.get(),
            ModBlockRegistry.SIGN_STAND_THIN.get(),
            ModBlockRegistry.SIGN_STAND_THICK.get()
    ).build(DSL.remainderType()));
}
