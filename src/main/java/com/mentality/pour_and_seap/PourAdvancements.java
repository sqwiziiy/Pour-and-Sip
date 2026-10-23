package com.mentality.pour_and_seap;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Server-side progression for interactions that may not modify inventory
 * (pouring into a glass already placed in the world).
 */
public final class PourAdvancements {
    private PourAdvancements() {}

    public static void winePoured(PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;
        Advancement advancement = serverPlayer.getServer().getAdvancementLoader()
                .get(PourAndSeap.id("wine_tasting"));
        if (advancement != null) {
            serverPlayer.getAdvancementTracker().grantCriterion(advancement, "poured_wine");
        }
    }
