package com.mentality.pour_and_seap;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.Set;

public final class WineContents {
    private WineContents() {}

    public static final String SOURCE_KEY = "WineSource";
    private static final TagKey<Item> WINES = TagKey.of(RegistryKeys.ITEM, PourAndSeap.id("wines"));
    private static final TagKey<Item> RED_WINES = TagKey.of(RegistryKeys.ITEM, PourAndSeap.id("red_wines"));

    // Optional integration: neither Vinery nor NetherVinery is linked or required.
    // The item tags allow a modpack datapack to extend wine support safely.
    public static int getWineFill(ItemStack source) {
        if (source.isEmpty()) return 0;
        Identifier id = Registries.ITEM.getId(source.getItem());
        boolean vinery = id.getNamespace().equals("vinery");
        boolean nether = id.getNamespace().equals("nethervinery");
        String className = source.getItem().getClass().getSimpleName();
        if (!source.isIn(WINES) &&
            !((vinery && className.equals("DrinkBlockItem")) ||
              (nether && className.equals("NetherDrinkBlockItem")))) return 0;
        // Non-red wine, cider, and mead are displayed as pale golden wine.
        return source.isIn(RED_WINES) || RED_IDS.contains(id.getPath()) && (vinery || nether) ? 3 : 4;
    }

    private static final Set<String> RED_IDS = Set.of(
        "red_wine", "noir_wine", "cherry_wine", "stal_wine", "bolvar_wine",
        "lilitu_wine", "villagers_fright", "bottle_mojang_noir",
        "ghastly_grenache", "blazewine_pinot", "netherite_nectar",
        "lava_fizz", "improved_lava_fizz"
    );

    public static ItemStack read(ItemStack filledGlass) {
        if (filledGlass.isEmpty() || filledGlass.getNbt() == null) return ItemStack.EMPTY;
        return fromNbt(filledGlass.getNbt());
    }

    public static ItemStack fromNbt(NbtCompound holder) {
        if (holder == null || !holder.contains(SOURCE_KEY, NbtElement.COMPOUND_TYPE)) return ItemStack.EMPTY;
        ItemStack wine = ItemStack.fromNbt(holder.getCompound(SOURCE_KEY));
        // Invalid/custom deleted content must never become a duplicating vessel.
        return wine.isEmpty() ? ItemStack.EMPTY : wine.copyWithCount(1);
    }

    public static void write(ItemStack filledGlass, ItemStack wine) {
        if (wine.isEmpty()) return;
        filledGlass.getOrCreateNbt().put(SOURCE_KEY, wine.copyWithCount(1).writeNbt(new NbtCompound()));
    }

    public static boolean isEmptyWineBottle(ItemStack source) {
        return Registries.ITEM.getId(source.getItem()).toString().equals("vinery:wine_bottle");
    }

    public static ItemStack bottle() {
        Identifier id = new Identifier("vinery", "wine_bottle");
        Item item = Registries.ITEM.get(id);
        return item == Items.AIR ? new ItemStack(Items.GLASS_BOTTLE) : new ItemStack(item);
    }

    // Restore the exact original stack, including every WineYears NBT field.
    public static void restore(PlayerEntity player, Hand hand, ItemStack emptyBottle, ItemStack wine) {
        if (wine.isEmpty()) return;
        if (!player.getAbilities().creativeMode) emptyBottle.decrement(1);
        if (emptyBottle.isEmpty()) {
            player.setStackInHand(hand, wine.copyWithCount(1));
        } else if (!player.getInventory().insertStack(wine.copyWithCount(1))) {
            player.dropItem(wine.copyWithCount(1), false);
        }
        player.getInventory().markDirty();
    }

    // Let the originating mod determine effects, duration and amplifier.
    // In particular, never recreate or approximate WineYears calculations.
    public static void drink(ItemStack source, World world, LivingEntity drinker) {
        if (!world.isClient && !source.isEmpty()) source.copyWithCount(1).finishUsing(world, drinker);
    }
}
