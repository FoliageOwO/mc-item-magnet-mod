package net.davdeo.itemmagnetmod;

import net.davdeo.itemmagnetmod.util.ItemMagnetHelper;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class ItemMagnetModClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientTickEvents.END_CLIENT_TICK.register(this::onEndClientTick);
	}

	private void onEndClientTick(Minecraft minecraft) {
		if (minecraft.player == null || minecraft.gui == null) {
			return;
		}

		if (ItemMagnetHelper.getFirstActiveMagnetInventoryIndex(minecraft.player) != -1) {
			minecraft.gui.setOverlayMessage(
					Component.literal("Item Magnet: ")
							.withStyle(ChatFormatting.WHITE)
							.append(Component.translatable("actionbar.itemmagnetmod.item_magnet.active")
									.withStyle(ChatFormatting.GREEN))
							.append(Component.literal(" (" + ModConfig.magnetDistance + " blocks)")
									.withStyle(ChatFormatting.WHITE)),
					false
			);
		}
	}
}
