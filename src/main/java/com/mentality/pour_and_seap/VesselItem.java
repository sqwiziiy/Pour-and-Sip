package com.mentality.pour_and_seap;

import net.minecraft.block.Block;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
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

public class VesselItem extends BlockItem {
    private static final String DRINK_KEY = "Drink";
    public VesselItem(Block block, Settings settings) { super(block, settings); }

    public static int getFill(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(DRINK_KEY)) return 0;
        String drink = nbt.getString(DRINK_KEY);
        return "water".equals(drink) ? 1 : ("milk".equals(drink) ? 2 : 0);
    }

    public static void setFill(ItemStack stack, int fill) {
        if (fill == 0) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) nbt.remove(DRINK_KEY);
        } else {
            stack.getOrCreateNbt().putString(DRINK_KEY, fill == 1 ? "water" : "milk");
        }
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
        if (!player.getInventory().insertStack(remainder)) player.dropItem(remainder, false);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        int fill = getFill(stack);
        if (fill == 0 && hand == Hand.MAIN_HAND) {
            ItemStack source = player.getOffHandStack();
            int sourceFill = getSourceFill(source);
            if (sourceFill != 0) {
                if (!world.isClient) {
                    setFill(stack, sourceFill);
                    consumeSource(player, source, sourceFill);
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
        if (!world.isClient) {
            int fill = getFill(stack);
            if (fill == 2) user.clearStatusEffects();
            if (fill != 0) setFill(stack, 0);
        }
        return stack;
    }

    @Override public int getMaxUseTime(ItemStack stack) { return 32; }
    @Override public UseAction getUseAction(ItemStack stack) { return UseAction.DRINK; }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        String key = switch (getFill(stack)) {
            case 1 -> "water";
            case 2 -> "milk";
            default -> "empty";
        };
        tooltip.add(Text.translatable("tooltip.pour_and_seap." + key).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
