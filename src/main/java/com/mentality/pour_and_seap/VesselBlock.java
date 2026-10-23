package com.mentality.pour_and_seap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
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

public class VesselBlock extends Block {
    public static final IntProperty FILL = IntProperty.of("fill", 0, 2);
    private static final VoxelShape TUMBLER_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 11, 12);
    private static final VoxelShape GOBLET_SHAPE = Block.createCuboidShape(4, 0, 4, 12, 13, 12);
    private final boolean wineGlass;

    public VesselBlock(Settings settings, boolean wineGlass) {
        super(settings);
        this.wineGlass = wineGlass;
        setDefaultState(getStateManager().getDefaultState().with(FILL, 0));
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return wineGlass ? GOBLET_SHAPE : TUMBLER_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getOutlineShape(state, world, pos, context);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.PASS;
        int fill = state.get(FILL);
        ItemStack mainHand = player.getMainHandStack();
        if (fill == 0) {
            int sourceFill = VesselItem.getSourceFill(mainHand);
            if (sourceFill == 0) sourceFill = VesselItem.getSourceFill(player.getOffHandStack());
            if (sourceFill != 0) {
                if (!world.isClient) {
                    ItemStack source = VesselItem.getSourceFill(mainHand) != 0 ? mainHand : player.getOffHandStack();
                    VesselItem.consumeSource(player, source, sourceFill);
                    world.setBlockState(pos, state.with(FILL, sourceFill), Block.NOTIFY_ALL);
                }
                return ActionResult.success(world.isClient);
            }
        }
        if (mainHand.isEmpty() && player.isSneaking()) {
            if (!world.isClient) {
                ItemStack stack = getPickStack(world, pos, state);
                if (!player.getInventory().insertStack(stack)) player.dropItem(stack, false);
                world.setBlockState(pos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }
        if (fill != 0 && mainHand.isEmpty()) {
            if (!world.isClient) {
                if (fill == 2) player.clearStatusEffects();
                // An audible sip for all nearby players on the authoritative server.
                world.playSound(null, pos, SoundEvents.ENTITY_GENERIC_DRINK,
                    SoundCategory.PLAYERS, 0.7f, 1.0f);
                world.setBlockState(pos, state.with(FILL, 0), Block.NOTIFY_ALL);
            }
            return ActionResult.success(world.isClient);
        }
        return ActionResult.PASS;
    }

    @Override
    public ItemStack getPickStack(BlockView world, BlockPos pos, BlockState state) {
        ItemStack stack = new ItemStack(asItem());
        VesselItem.setFill(stack, state.get(FILL));
        return stack;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state,
                           @Nullable net.minecraft.block.entity.BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!world.isClient) Block.dropStack(world, pos, getPickStack(world, pos, state));
    }
}
