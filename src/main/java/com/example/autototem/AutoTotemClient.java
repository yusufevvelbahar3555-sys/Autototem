package com.example.autototem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;

public class AutoTotemClient implements ClientModInitializer {

    private static final int HOTBAR_INDEX = 2;
    private static final int HOTBAR_SCREEN_SLOT = 36 + HOTBAR_INDEX;
    private static final int OFFHAND_BUTTON = 40;
    private static int cooldown = 0;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            ClientPlayerEntity p = client.player;
            if (p == null || client.interactionManager == null) return;
            if (client.currentScreen instanceof HandledScreen) return;
            if (cooldown > 0) { cooldown--; return; }

            int syncId = p.playerScreenHandler.syncId;

            boolean offhandHasTotem = p.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
            boolean slot3HasTotem = p.getInventory().getStack(HOTBAR_INDEX).isOf(Items.TOTEM_OF_UNDYING);

            if (!offhandHasTotem && slot3HasTotem) {
                client.interactionManager.clickSlot(
                        syncId, HOTBAR_SCREEN_SLOT, OFFHAND_BUTTON, SlotActionType.SWAP, p);
                cooldown = 2;
                return;
            }

            if (!slot3HasTotem) {
                for (int i = 9; i < 36; i++) {
                    if (p.getInventory().getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
                        client.interactionManager.clickSlot(
                                syncId, i, HOTBAR_INDEX, SlotActionType.SWAP, p);
                        cooldown = 2;
                        return;
                    }
                }
            }
        });
    }
}
