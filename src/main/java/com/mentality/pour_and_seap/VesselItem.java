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
        if (intrinsicFill == 0) super.appendBlocks(map, item);
    }

    public static int getFill(ItemStack stack) {
        if (stack.getItem() instanceof VesselItem vessel && vessel.intrinsicFill != 0)
            return vessel.intrinsicFill;
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
        return WineContents.getWineFill(source);
    }

    public static void consumeSource(PlayerEntity player, ItemStack source, int fill) {
        if (player.getAbilities().creativeMode) return;
        source.decrement(1);
        ItemStack remainder = fill == 1 ? new ItemStack(Items.GLASS_BOTTLE)
            : fill == 2 ? new ItemStack(Items.BUCKET) : WineContents.bottle();
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
        if (fill >= 3 && hand == Hand.MAIN_HAND && WineContents.isEmptyWineBottle(player.getOffHandStack())) {
            ItemStack stored = WineContents.read(stack);
            if (!stored.isEmpty()) {
                if (!world.isClient) {
                    WineContents.restore(player, Hand.OFF_HAND, player.getOffHandStack(), stored);
                    player.setStackInHand(hand, new ItemStack(PourAndSeap.vesselItem(vesselBlock, 0)));
                }
                return TypedActionResult.success(stack, world.isClient);
            }
        }
        if (fill == 0 && hand == Hand.MAIN_HAND) {
            ItemStack source = player.getOffHandStack();
            int liquid = getSourceFill(source);
            if (liquid != 0) {
                if (!world.isClient) {
                    ItemStack originalWine = liquid >= 3 ? source.copyWithCount(1) : ItemStack.EMPTY;
                    consumeSource(player, source, liquid);
                    ItemStack filled = new ItemStack(PourAndSeap.vesselItem(vesselBlock, liquid));
                    if (liquid >= 3) WineContents.write(filled, originalWine);
                    if (stack.getCount() > 1) {
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
        int fill = getFill(stack);
        if (fill == 0) return stack;
        if (!world.isClient && fill == 2) user.clearStatusEffects();
        if (fill >= 3) WineContents.drink(WineContents.read(stack), world, user);
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
            Text contents;
            if (fill == 1) contents = Text.translatable("drink.pour_and_seap.water");
            else if (fill == 2) contents = Text.translatable("drink.pour_and_seap.milk");
            else {
                ItemStack original = WineContents.read(stack);
                contents = original.isEmpty()
                    ? Text.translatable("drink.pour_and_seap.wine")
                    : original.getName();
            }
            tooltip.add(Text.translatable("tooltip.pour_and_seap.contents", contents)
                .formatted(Formatting.GRAY));
        }
        super.appendTooltip(stack, world, tooltip, context);
    }
}
