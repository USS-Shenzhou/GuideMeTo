package cn.ussshenzhou.tellmewhere.item;

import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.block.BaseSignBlock;
import cn.ussshenzhou.tellmewhere.blockentity.SignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.function.Supplier;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class SignBlockAndItemRegistryHelper {
    public static final LinkedHashMap<String, Supplier<Block>> SIGN_BLOCKS = new LinkedHashMap<>();
    public static final LinkedHashMap<String, Supplier<Item>> SIGN_ITEMS = new LinkedHashMap<>();

    private static final LinkedHashSet<RegistryContext> CONTEXTS = new LinkedHashSet<>();

    private static void addContext(String name,
                                   int screenStart16X, int screenStart16Y, int screenStart16Z,
                                   int defaultScreenLength16, int screenHeight16, int screenThick16, int screenMargin16) {
        CONTEXTS.add(new RegistryContext(name, screenStart16X, screenStart16Y, screenStart16Z, defaultScreenLength16, screenHeight16, screenThick16, screenMargin16));
    }

    static {
        addContext("sign_hang_thin", 1, 1, 7, 14, 8, 2, 1);
        addContext("sign_hang_thick", 1, 1, 6, 14, 8, 4, 1);
        addContext("sign_hang_thin_narrow", 1, 1, 7, 14, 4, 2, 1);
        addContext("sign_hang_thick_narrow", 1, 1, 6, 14, 4, 4, 1);
        addContext("sign_stand_thin", 1, 7, 7, 14, 8, 2, 1);
        addContext("sign_stand_thick", 1, 7, 6, 14, 8, 4, 1);
        addContext("sign_stand_thin_narrow", 1, 11, 7, 14, 4, 2, 1);
        addContext("sign_stand_thick_narrow", 1, 11, 6, 14, 4, 4, 1);
        addContext("sign_stick_thin", 1, 4, 14, 14, 8, 2, 1);
        addContext("sign_stick_thick", 1, 4, 12, 14, 8, 4, 1);
        addContext("sign_stick_thin_full", 1, 1, 14, 14, 14, 2, 1);
        addContext("sign_stick_thick_full", 1, 1, 12, 14, 14, 4, 1);
        addContext("sign_stick_thin_narrow", 1, 6, 14, 14, 4, 2, 1);
        addContext("sign_stick_thick_narrow", 1, 6, 12, 14, 4, 4, 1);
        addContext("simple_pillar_thin", 8, 1, 7, 0, 14, 2, 1);
        addContext("simple_pillar_thick", 8, 2, 6, 0, 12, 4, 2);
    }

    @SuppressWarnings("NullableProblems")
    public static void registerBlock(DeferredRegister<Block> block) {
        CONTEXTS.forEach(c -> SIGN_BLOCKS.put(c.name, block.register(c.name, () -> {
            if (c.name.startsWith("simple")) {
                return new BaseSignBlock(new Vector3f(c.screenStart16X, c.screenStart16Y, c.screenStart16Z), c.defaultScreenLength16, c.screenHeight16, c.screenThick16, c.screenMargin16) {
                    @Override
                    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
                        SignBlockEntity signBlockEntity = (SignBlockEntity) level.getBlockEntity(pos);
                        Item item = player.getItemInHand(hand).getItem();
                        var itemName = BuiltInRegistries.ITEM.getKey(item);
                        if (TellMeWhere.MODID.equals(itemName.getNamespace()) && itemName.getPath().contains("sign_")) {
                            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                        }
                        if (!level.isClientSide() && item instanceof BlockItem blockItem) {
                            //itemInHand can place a block
                            BlockState blockState = blockItem.getBlock().defaultBlockState();
                            //try set direction
                            if (blockState.getOptionalValue(FACING).isPresent()) {
                                blockState.setValue(FACING, state.getValue(FACING));
                            }
                            if (blockState.getShape(level, pos) == Shapes.block()
                                    //block placed by itemInHand is a full block
                                    && signBlockEntity.getDisguiseBlockState().getBlock() != blockItem.getBlock()) {
                                //block placed by itemInHand is a new block
                                signBlockEntity.setDisguise(blockState);
                                return ItemInteractionResult.SUCCESS;
                            }
                        }
                        return ItemInteractionResult.SUCCESS;
                    }

                    @Override
                    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
                        return InteractionResult.PASS;
                    }

                    @Override
                    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
                        return new SignBlockEntity(pPos, pState, screenStart16, defaultScreenLength16, screenHeight16, screenThick16, screenMargin16){
                            @Override
                            public void checkSlavesAt() {
                                return;
                            }
                        };
                    }
                };
            }
            return new BaseSignBlock(new Vector3f(c.screenStart16X, c.screenStart16Y, c.screenStart16Z), c.defaultScreenLength16, c.screenHeight16, c.screenThick16, c.screenMargin16);
        })));
    }

    public static void registerItem(DeferredRegister<Item> item) {
        CONTEXTS.forEach(c -> SIGN_ITEMS.put(c.name, item.register(c.name, () -> new BlockItem(SIGN_BLOCKS.get(c.name).get(), new Item.Properties()))));
    }

    public record RegistryContext(
            String name,
            int screenStart16X, int screenStart16Y, int screenStart16Z,
            int defaultScreenLength16, int screenHeight16, int screenThick16, int screenMargin16
    ) {
        @Override
        public boolean equals(Object obj) {
            return this == obj;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }
}
