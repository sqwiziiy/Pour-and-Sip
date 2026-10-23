package com.mentality.pour_and_seap;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class VesselItem extends BlockItem {
    private static final String OLD_DRINK_KEY = "Drink";
    private final VesselBlock vesselBlock;
    private final int intrinsicFill;

    public VesselItem(VesselBlock block, Settings settings, int intrinsicFill) {
        super(block, settings);
        this.vesselBlock = block;
        this.intrinsicFill = intrinsicFill;
    }

    @Override
    public void appendBlocks(Map<Block, Item> map, Item item) {
        // Register only the EMPTY variant as the canonical item for the block.
        if (intrinsicFill == 0) super.appendBlocks(map, item);
    }

    public static int getFill(ItemStack stack) {
        if (stack.getItem() instanceof VesselItem vessel) {
            if (vessel.intrinsicFill != 0) return vessel.intrinsicFill;
        }
        // Read 0.2.2 NBT-filled items so existing saves are not silently emptied.
        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains(OLD_DRINK_KEY)) return 0;
        return switch (tag.getString(OLD_DRINK_KEY)) {
            case "water" -> 1;
            case "milk" -> 2;
            default -> 0;
        };
    }

    public static int getSourceFill(ItemStack source) {
        if (source.isOf(Items.POTION) && PotionUtil.getPotion(source) == Potions.WATER) return 1;
        if (source.isOf(Items.MILK_BUCKET)) return 2;
        return 0;
    }

    public static void consumeSource(PlayerEntity player, ItemStack source, int fill) {
        if (player.getAbilities().creativeMode) return;
        source.decrement(1);
        ItemStack remainder = new ItemStack(fill == 1 ? Items.GLASS_BOTTLE : Items.BUCKET);
        if (source.isEmpty() && source == player.getStackInHand(Hand.OFF_HAND)) {
            player.setStackInHand(Hand.OFF_HAND, remainder);
        } else if (source.isEmpty() && source == player.getStackInHand(Hand.MAIN_HAND)) {
            player.setStackInHand(Hand.MAIN_HAND, remainder);
        } else if (!player.getInventory().insertStack(remainder)) {
            player.dropItem(remainder, false);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        int fill = getFill(stack);

        if (fill == 0 && hand == Hand.MAIN_HAND) {
            ItemStack source = player.getOffHandStack();
            int liquid = getSourceFill(source);
            if (liquid != 0) {
                if (!world.isClient) {
                    consumeSource(player, source, liquid);
                    ItemStack filled = new ItemStack(PourAndSeap.vesselItem(vesselBlock, liquid));
                    if (stack.getCount() > 1) {
                        // Split exactly ONE vessel from a stack without creating
                        // an illegal stack of >1 filled cups or losing empty cups.
                        ItemStack emptyRemainder = stack.copy();
                        emptyRemainder.decrement(1);
                        player.setStackInHand(hand, filled);
                        if (!player.getInventory().insertStack(emptyRemainder))
                            player.dropItem(emptyRemainder, false);
                    } else {
                        player.setStackInHand(hand, filled);
                    }
                    player.getInventory().markDirty();
                }
                return TypedActionResult.success(stack, world.isClient);
            }
        }

        if (fill != 0) {
            player.setCurrentHand(hand);
            return TypedActionResult.consume(stack);
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (getFill(stack) == 0) return stack;
        if (!world.isClient && getFill(stack) == 2) user.clearStatusEffects();
        // Return the canonical EMPTY stackable item after drinking.
        return new ItemStack(PourAndSeap.vesselItem(vesselBlock, 0));
    }

    @Override public int getMaxUseTime(ItemStack stack) { return 32; }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }
    @Override public SoundEvent getDrinkSound() { return SoundEvents.ENTITY_GENERIC_DRINK; }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
                              List<Text> tooltip, TooltipContext context) {
        int fill = getFill(stack);
        if (fill != 0) {
            Text contents = Text.translatable(
                fill == 1 ? "drink.pour_and_seap.water" : "drink.pour_and_seap.milk");
            tooltip.add(Text.translatable("tooltip.pour_and_seap.contents", contents)
                .formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
