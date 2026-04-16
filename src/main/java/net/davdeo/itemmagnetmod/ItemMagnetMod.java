package net.davdeo.itemmagnetmod;

import net.davdeo.itemmagnetmod.command.ItemMagnetCommands;
import net.davdeo.itemmagnetmod.component.ModComponents;
import net.davdeo.itemmagnetmod.datagen.ModLootTableModifier;
import net.davdeo.itemmagnetmod.debug.ItemMagnetDebugManager;
import net.davdeo.itemmagnetmod.event.ModEvents;
import net.davdeo.itemmagnetmod.item.ModCreativeTab;
import net.davdeo.itemmagnetmod.item.ModItems;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemMagnetMod implements ModInitializer {
	public static final String MOD_ID = "itemmagnetmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModConfig.load();

		ModItems.registerModItems();
		ModCreativeTab.registerItemGroups();
		ModComponents.registerComponents();
		CommandRegistrationCallback.EVENT.register((dispatcher, context, environment) ->
				ItemMagnetCommands.register(dispatcher));
		ServerTickEvents.END_SERVER_TICK.register(server ->
				ItemMagnetDebugManager.flush(server.getTickCount(), server.getPlayerList().getPlayers()));

		ModEvents.registerModEvents();

		ModLootTableModifier.modifyLootTables();
	}
}
