package com.poursip;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TooltipContext;
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

/**
 * MVP: standalone glass you fill using an item held in the off hand.
 * The wine-glass shell is registered, but wine integration is intentionally deferred
 * until actual Vinery item IDs/effects are audited.
 *
 * Holding drinking glass in main hand + water bottle/milk bucket in offhand:
 * right-click air -> source consumed; empty bottle/bucket returned; glass stores Drink NBT.
 * Main hand full glass + empty offhand: hold right-click to drink, leaving empty glass.
 */
public class VesselItem extends Item {
    public static final String DRINK_KEY = "Drink";
    private final boolean allowBasicDrinks;

    public VesselItem(Settings settings, boolean allowBasicDrinks) {
        super(settings);
        this.allowBasicDrinks = allowBasicDrinks;
    }

    public static String getDrink(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains(DRINK_KEY)) return "empty";
        String value = nbt.getString(DRINK_KEY);
        return "water".equals(value) || "milk".equals(value) ? value : "empty";
    }

    public static void setDrink(ItemStack stack, String drink) {
        if ("empty".equals(drink)) {
            NbtCompound nbt = stack.getNbt();
            if (nbt != null) nbt.remove(DRINK_KEY);
        } else {
            stack.getOrCreateNbt().putString(DRINK_KEY, drink);
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack vessel = player.getStackInHand(hand);
        String drink = getDrink(vessel);

        if (hand == Hand.MAIN_HAND && "empty".equals(drink) && allowBasicDrinks) {
            ItemStack source = player.getOffHandStack();
            String filled = null;
            ItemStack remainder = ItemStack.EMPTY;
            if (source.isOf(Items.POTION) && PotionUtil.getPotion(source) == Potions.WATER) {
                filled = "water";
                remainder = new ItemStack(Items.GLASS_BOTTLE);
            } else if (source.isOf(Items.MILK_BUCKET)) {
                filled = "milk";
                remainder = new ItemStack(Items.BUCKET);
            }
            if (filled != null) {
                if (!world.isClient) {
                    setDrink(vessel, filled);
                    if (!player.getAbilities().creativeMode) {
                        source.decrement(1);
                        if (!player.getInventory().insertStack(remainder)) {
                            player.dropItem(remainder, false);
                        }
                    }
                    player.getInventory().markDirty();
                }
                return TypedActionResult.success(vessel, world.isClient);
            }
        }

        if (!"empty".equals(drink)) {
            player.setCurrentHand(hand);
            return TypedActionResult.consume(vessel);
        }
        return TypedActionResult.pass(vessel);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient && user instanceof PlayerEntity player) {
            String drink = getDrink(stack);
            if ("milk".equals(drink)) {
                // Reproduce the core vanilla milk effect. No new potion statuses.
                player.clearStatusEffects();
            }
            // Water has no native potion effect. Homeostatic integration is NOT implemented yet.
            if (!"empty".equals(drink)) setDrink(stack, "empty");
            player.getInventory().markDirty();
        }
        return stack;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 32;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world,
            List<Text> tooltip, TooltipContext context) {
        String value = getDrink(stack);
        tooltip.add(Text.translatable("tooltip.poursip." + value).formatted(Formatting.GRAY));
        super.appendTooltip(stack, world, tooltip, context);
    }
}
