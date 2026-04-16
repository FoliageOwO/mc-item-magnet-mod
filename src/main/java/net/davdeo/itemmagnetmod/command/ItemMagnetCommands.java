package net.davdeo.itemmagnetmod.command;

import com.mojang.brigadier.CommandDispatcher;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ItemMagnetCommands {
    private ItemMagnetCommands() {
        super();
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("itemmagnet")
                .requires(source -> Commands.hasPermission(Commands.LEVEL_ADMINS).test(source))
                .then(Commands.literal("reload")
                        .executes(context -> reload(context.getSource())))
                .then(Commands.literal("status")
                        .executes(context -> status(context.getSource()))));
    }

    private static int reload(CommandSourceStack source) {
        ModConfig.load();

        source.sendSuccess(() -> Component.literal("Item Magnet config reloaded successfully.")
                .withStyle(ChatFormatting.GREEN), false);

        return 1;
    }

    private static int status(CommandSourceStack source) {
        source.sendSuccess(ItemMagnetCommands::buildStatusMessage, false);
        return 1;
    }

    private static MutableComponent buildStatusMessage() {
        return Component.empty()
                .append(title("Item Magnet Status"))
                .append(section("General"))
                .append(line("Pickup Radius", ModConfig.magnetDistance + " blocks", ChatFormatting.GREEN))
                .append(line("Magnet Durability", Integer.toString(ModConfig.magnetDurability), ChatFormatting.AQUA))
                .append(line("Broken Magnet Stack", Integer.toString(ModConfig.brokenMagnetStack), ChatFormatting.AQUA))
                .append(line("Magnet Core Stack", Integer.toString(ModConfig.magnetCoreStack), ChatFormatting.AQUA))
                .append(line("Indestructible", onOff(ModConfig.isIndestructible), boolColor(ModConfig.isIndestructible)))
                .append(section("Loot Tables"))
                .append(line("Bastion Remnants", onOff(ModConfig.canFindInBastion), boolColor(ModConfig.canFindInBastion)))
                .append(line("Ancient City", onOff(ModConfig.canFindInAncientCity), boolColor(ModConfig.canFindInAncientCity)))
                .append(line("End City", onOff(ModConfig.canFindInEndCity), boolColor(ModConfig.canFindInEndCity)))
                .append(line("Stronghold Library", onOff(ModConfig.canFindInStrongholdLibrary), boolColor(ModConfig.canFindInStrongholdLibrary)))
                .append(section("Reload Notes"))
                .append(line("Hot Reload", "Pickup radius and indestructible mode", ChatFormatting.YELLOW))
                .append(line("Restart Needed", "Durability, stack sizes, loot table registration", ChatFormatting.RED));
    }

    private static MutableComponent title(String text) {
        return Component.literal("========== " + text + " ==========\n")
                .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
    }

    private static MutableComponent section(String text) {
        return Component.literal(text + "\n")
                .withStyle(ChatFormatting.LIGHT_PURPLE);
    }

    private static MutableComponent line(String label, String value, ChatFormatting valueColor) {
        return Component.literal("  ")
                .append(Component.literal(label + ": ").withStyle(ChatFormatting.WHITE))
                .append(Component.literal(value).withStyle(valueColor))
                .append(Component.literal("\n"));
    }

    private static String onOff(boolean value) {
        return value ? "Enabled" : "Disabled";
    }

    private static ChatFormatting boolColor(boolean value) {
        return value ? ChatFormatting.GREEN : ChatFormatting.RED;
    }
}
