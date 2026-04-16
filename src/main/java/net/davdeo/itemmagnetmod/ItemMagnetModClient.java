package net.davdeo.itemmagnetmod;

import net.davdeo.itemmagnetmod.util.ItemMagnetHelper;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ItemMagnetModClient implements ClientModInitializer {
	private static final int INACTIVE_MESSAGE_TICKS = 20;

	private boolean hadActiveMagnetLastTick = false;
	private int inactiveMessageTicksRemaining = 0;

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
	}

	private void onEndClientTick(Minecraft minecraft) {
		if (minecraft.player == null || minecraft.gui == null) {
			return;
		}

		boolean hasActiveMagnet = ItemMagnetHelper.getFirstActiveMagnetInventoryIndex(minecraft.player) != -1;
		int pickupDistance = 0;

		if (!hasActiveMagnet && hadActiveMagnetLastTick) {
			inactiveMessageTicksRemaining = INACTIVE_MESSAGE_TICKS;
		}

		if (hasActiveMagnet) {
			pickupDistance = ItemMagnetHelper.getPickupDistance(minecraft.player, ItemMagnetHelper.getFirstActiveMagnet(minecraft.player));

			minecraft.gui.setOverlayMessage(
					Component.literal("Item Magnet: ")
							.withStyle(ChatFormatting.WHITE)
							.append(Component.translatable("actionbar.itemmagnetmod.item_magnet.active")
									.withStyle(ChatFormatting.GREEN))
							.append(Component.literal(" (" + pickupDistance + " blocks)")
									.withStyle(ChatFormatting.WHITE)),
					false
			);
		} else if (inactiveMessageTicksRemaining > 0) {
			minecraft.gui.setOverlayMessage(
					Component.literal("Item Magnet: ")
							.withStyle(ChatFormatting.WHITE)
							.append(Component.translatable("actionbar.itemmagnetmod.item_magnet.not_active")
									.withStyle(ChatFormatting.GRAY)),
					false
			);
			inactiveMessageTicksRemaining--;
		}

		hadActiveMagnetLastTick = hasActiveMagnet;
	}
}
