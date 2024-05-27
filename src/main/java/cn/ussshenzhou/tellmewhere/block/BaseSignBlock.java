package cn.ussshenzhou.tellmewhere.block;

import cn.ussshenzhou.t88.util.BlockUtil;
import cn.ussshenzhou.tellmewhere.DirectionUtil;
import cn.ussshenzhou.tellmewhere.TellMeWhere;
import cn.ussshenzhou.tellmewhere.blockentity.SignBlockEntity;
import cn.ussshenzhou.tellmewhere.gui.SignEditScreen;
import com.mojang.math.Axis;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;

/**
 * @author USS_Shenzhou
 */
public class BaseSignBlock extends BaseEntityBlock {

    private final VoxelShape NORTH;
    private final VoxelShape SOUTH;
    private final VoxelShape EAST;
    private final VoxelShape WEST;

    public static final MapCodec<BaseSignBlock> CODEC = simpleCodec((Properties p) -> new BaseSignBlock(new Vector3f(), 0, 0, 0, 0));

    public final int defaultScreenLength16;
    public final Vector3f screenStart16;
    public final int screenHeight16;
    public final int screenThick16;
    public final int screenMargin16;

    @SuppressWarnings("AlibabaLowerCamelCaseVariableNaming")
    public BaseSignBlock(Vector3f screenStart16, int defaultScreenLength16, int screenHeight16, int screenThick16, int screenMargin16) {
        super(BlockBehaviour.Properties.of()
                .noOcclusion()
                .strength(3, 6)
        );
        registerDefaultState(defaultBlockState()
                .setValue(FACING, Direction.NORTH)
        );
        this.defaultScreenLength16 = defaultScreenLength16;
        this.screenStart16 = screenStart16;
        this.screenHeight16 = screenHeight16;
        this.screenThick16 = screenThick16;
        this.screenMargin16 = screenMargin16;

        var helper = BlockUtil.cube16(16 - (screenStart16.x - screenMargin16), 16 - (screenStart16.x + defaultScreenLength16 + screenMargin16),
                16 - (screenStart16.y - screenMargin16), 16 - (screenStart16.y + screenHeight16 + screenMargin16),
                screenStart16.z, screenStart16.z + screenThick16
        );
        var m = new Matrix4f().rotateAround(Axis.YP.rotationDegrees(90), 8, 8, 8);
        this.NORTH = helper.getShape();
        this.WEST = helper.doo(v -> v.mulProject(m)).getShape();
        this.SOUTH = helper.doo(v -> v.mulProject(m)).getShape();
        this.EAST = helper.doo(v -> v.mulProject(m)).getShape();
    }

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return switch (pState.getValue(FACING)) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case EAST -> EAST;
            case WEST -> WEST;
            default -> Shapes.block();
        };
    }

    @Override
    protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState) {
        try {
            super.spawnDestroyParticles(pLevel, pPlayer, pPos, ((SignBlockEntity) pLevel.getBlockEntity(pPos)).getDisguiseBlockState());
        } catch (Exception ignored) {
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new SignBlockEntity(pPos, pState, screenStart16, defaultScreenLength16, screenHeight16, screenThick16, screenMargin16);
    }

    /**
     * Use setPlacedBy+destroy to avoid avoiding StackOverFlow in onNeighborChange
     */
    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        super.setPlacedBy(level, pos, pState, pPlacer, pStack);
        if (!level.isClientSide()) {
            var thisEntity = (SignBlockEntity) level.getBlockEntity(pos);
            if (thisEntity != null) {
                thisEntity.neighborUpdated();
            }
        }
    }

    @Override
    public void destroy(LevelAccessor level, BlockPos pos, BlockState state) {
        super.destroy(level, pos, state);
        if (!level.isClientSide()) {
            //notify left
            var left = DirectionUtil.getBlockEntityAtLeft(level, pos, state);
            if (left instanceof SignBlockEntity e) {
                e.neighborUpdated();
            }
            //notify right
            var right = DirectionUtil.getBlockEntityAtRight(level, pos, state);
            if (right instanceof SignBlockEntity e) {
                e.neighborUpdated();
            }
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pPos, Player player, InteractionHand hang, BlockHitResult hit) {
        SignBlockEntity signBlockEntity = (SignBlockEntity) level.getBlockEntity(pPos);
        Item item = player.getItemInHand(hang).getItem();
        var itemName = BuiltInRegistries.ITEM.getKey(item);
        if (TellMeWhere.MODID.equals(itemName.getNamespace()) && itemName.getPath().contains("sign_")) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            if (player.isCreative()) {
                var entity = (SignBlockEntity) player.level().getBlockEntity(pPos);
                if (entity == null) {
                    return InteractionResult.FAIL;
                }
                if (DirectionUtil.isParallel(hit.getDirection(), state.getValue(FACING))) {
                    var rightMaster = entity.findMasterAt(false);
                    if (rightMaster.getBlockState().getValue(FACING) == hit.getDirection()) {
                        openEditor(rightMaster);
                        return InteractionResult.SUCCESS;
                    }
                    var leftMaster = entity.findMasterAt(true);
                    if (leftMaster.getBlockState().getValue(FACING) == hit.getDirection()) {
                        openEditor(leftMaster);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        } else {
            //hit other sides
            if (hit.getDirection() != state.getValue(FACING)) {
                if (item instanceof BlockItem blockItem) {
                    //itemInHand can place a block
                    BlockState blockState = blockItem.getBlock().defaultBlockState();
                    //try set direction
                    if (blockState.getOptionalValue(FACING).isPresent()) {
                        blockState.setValue(FACING, state.getValue(FACING));
                    }
                    if (blockState.getShape(level, pPos) == Shapes.block()
                            //block placed by itemInHand is a full block
                            && signBlockEntity.getDisguiseBlockState().getBlock() != blockItem.getBlock()) {
                        //block placed by itemInHand is a new block
                        signBlockEntity.setDisguise(blockState);
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    private void openEditor(SignBlockEntity blockEntity) {
        Minecraft.getInstance().setScreen(new SignEditScreen(blockEntity));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }
}
