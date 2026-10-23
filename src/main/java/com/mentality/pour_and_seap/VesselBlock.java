package com.mentality.pour_and_seap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

public class VesselBlock extends Block implements BlockEntityProvider {
    public static final IntProperty FILL = IntProperty.of("fill", 0, 4);
    private static final VoxelShape TUMBLER_SHAPE = Block.createCuboidShape(5.3, 0, 5.3, 10.7, 7.8, 10.7);
    private static final VoxelShape GOBLET_SHAPE = Block.createCuboidShape(5.1, 0, 5.1, 10.9, 10.3, 10.9);
    private final boolean wineGlass;

    public VesselBlock(Settings settings, boolean wineGlass) {
        super(settings);
        this.wineGlass = wineGlass;
        setDefaultState(getStateManager().getDefaultState().with(FILL, 0));
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VesselBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FILL);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FILL, VesselItem.getFill(ctx.getStack()));
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (!world.isClient && state.get(FILL) >= 3 && world.getBlockEntity(pos) instanceof VesselBlockEntity be)
            be.setWine(WineContents.read(stack));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wineGlass ? GOBLET_SHAPE : TUMBLER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    private ItemStack makeFilledItem(BlockState state, BlockEntity entity) {
        ItemStack stack = new ItemStack(PourAndSeap.vesselItem(this, state.get(FILL)));
        if (state.get(FILL) >= 3 && entity instanceof VesselBlockEntity be)
            WineContents.write(stack, be.wine());
        return stack;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        return makeFilledItem(state, world.getBlockEntity(pos));
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        int fill = state.get(FILL);
        ItemStack main = player.getMainHandStack();
        VesselBlockEntity be = world.getBlockEntity(pos) instanceof VesselBlockEntity vessel ? vessel : null;

        // Click filled wine glass with empty wine bottle to pour it back.
        if (fill >= 3 && WineContents.isEmptyWineBottle(main) && be != null && !be.wine().isEmpty()) {
            if (!world.isClient) {
                ItemStack wine = be.wine();
                WineContents.restore(player, Hand.MAIN_HAND, main, wine);
                be.setWine(ItemStack.EMPTY);
                world.setBlockState(pos, state.with(FILL, 0), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }

        if (fill == 0) {
            ItemStack source = VesselItem.getSourceFill(main) != 0 ? main : player.getOffHandStack();
            int liquid = VesselItem.getSourceFill(source);
            if (liquid != 0) {
                if (!world.isClient) {
                    ItemStack originalWine = liquid >= 3 ? source.copyWithCount(1) : ItemStack.EMPTY;
                    VesselItem.consumeSource(player, source, liquid);
                    world.setBlockState(pos, state.with(FILL, liquid), Block.NOTIFY_ALL);
                    if (liquid >= 3 && world.getBlockEntity(pos) instanceof VesselBlockEntity placed)
                        placed.setWine(originalWine);
                }
                return ActionResult.success(world.isClient);
            }
        }

        if (main.isEmpty() && player.isSneaking()) {
            if (!world.isClient) {
                ItemStack picked = makeFilledItem(state, be);
                if (!player.getInventory().insertStack(picked)) player.dropItem(picked, false);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }

        if (fill != 0 && main.isEmpty()) {
            if (!world.isClient) {
                if (fill == 2) player.clearStatusEffects();
                if (fill >= 3 && be != null) {
                    WineContents.drink(be.wine(), world, player);
                    be.setWine(ItemStack.EMPTY);
                }
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_DRINK,
                    SoundCategory.PLAYERS, 0.7f, 1.0f);
                world.setBlockState(pos, state.with(FILL, 0), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!world.isClient) Block.dropStack(world, pos, makeFilledItem(state, blockEntity));
    }
}
