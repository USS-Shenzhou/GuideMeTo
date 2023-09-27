package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.tellmewhere.blockentity.TestSignBlockEntity;
import cn.ussshenzhou.tellmewhere.gui.SignEditScreen;
import cn.ussshenzhou.tellmewhere.item.ModItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class TestSign extends BaseEntityBlock {
    private static final VoxelShape SHAPE_Z = Block.box(0, 6, 7, 16, 16, 9);
    private static final VoxelShape SHAPE_X = Block.box(7, 6, 0, 9, 16, 16);

    protected TestSign() {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
        );
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH, SOUTH -> SHAPE_Z;
            case EAST, WEST -> SHAPE_X;
            default -> Shapes.block();
        };
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        //fixme
        BlockPos onPos = BlockPos.containing(context.getClickLocation());
        BlockState onState = context.getLevel().getBlockState(onPos);
        Direction newDirection;
        if (onState.getBlock() == ModBlockRegistry.TEST_SIGN.get()) {
            var onDirection = onState.getValue(FACING);
            var newPos = context.getClickedPos();
            if (newPos.equals(onPos.relative(onDirection.getCounterClockWise())) || newPos.equals(onPos.relative(onDirection.getClockWise()))) {
                newDirection = onDirection;
            } else {
                newDirection = context.getHorizontalDirection().getOpposite();
            }
        } else {
            newDirection = context.getHorizontalDirection().getOpposite();
        }
        return defaultBlockState()
                .setValue(FACING, newDirection);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new TestSignBlockEntity(pPos, pState);
    }

    /**
     * Use setPlacedBy+destroy to avoid avoiding StackOverFlow in onNeighborChange
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(level, pos, pState, pPlacer, pStack);
        if (!level.isClientSide()) {
            var thisEntity = (TestSignBlockEntity) level.getBlockEntity(pos);
            if (thisEntity != null) {
                thisEntity.neighborUpdated();
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (!level.isClientSide()) {
            Direction facing = state.getValue(BlockStateProperties.FACING);
            //notify left
            var left = level.getBlockEntity(pos.relative(facing.getCounterClockWise()));
            if (left instanceof TestSignBlockEntity e) {
                e.neighborUpdated();
            }
            //notify right
            var right = level.getBlockEntity(pos.relative(facing.getClockWise()));
            if (right instanceof TestSignBlockEntity e) {
                e.neighborUpdated();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pPos, Player player, InteractionHand hang, BlockHitResult hit) {
        TestSignBlockEntity signBlockEntity = (TestSignBlockEntity) level.getBlockEntity(pPos);
        Item item = player.getItemInHand(hang).getItem();
        if (item == ModItemRegistry.TEST_SIGN.get()) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            if (player.isCreative() && hit.getDirection() == state.getValue(FACING)) {
                openEditor((TestSignBlockEntity) player.level().getBlockEntity(pPos));
            }
        } else {
            //hit other sides
            if (hit.getDirection() != state.getValue(FACING)) {
                if (item instanceof BlockItem blockItem) {
                    //itemInHand can place a block
                    BlockState blockState = blockItem.getBlock().defaultBlockState();
                    //try set direction
                    if (blockState.getOptionalValue(FACING).isPresent()) {
                        blockState.setValue(FACING, Direction.NORTH);
                    }
                    if (blockState.getShape(level, pPos) == Shapes.block()
                            //block placed by itemInHand is a full block
                            && signBlockEntity.getDisguiseBlockState().getBlock() != blockItem.getBlock()) {
                        //block placed by itemInHand is a new block
                        signBlockEntity.setDisguise(blockState);
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void openEditor(TestSignBlockEntity blockEntity) {
        Minecraft.getInstance().setScreen(new SignEditScreen(blockEntity));
    }
}
