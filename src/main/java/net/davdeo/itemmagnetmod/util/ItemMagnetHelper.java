package net.davdeo.itemmagnetmod.util;

import net.davdeo.itemmagnetmod.component.ModComponents;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.davdeo.itemmagnetmod.enchantment.ModEnchantments;
import net.davdeo.itemmagnetmod.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import java.util.List;

public class ItemMagnetHelper {
    private ItemMagnetHelper() {
        super();
    }

    public static final int getFirstActiveMagnetInventoryIndex(Player player) {
        List<Integer> indices = InventoryUtil.getInventoryIndices(player, ModItems.ITEM_MAGNET);

        for (int inventoryIndex : indices) {
            ItemStack stack =  player.getInventory().getItem(inventoryIndex);

            if (ItemMagnetHelper.getIsActive(stack)) {
                return inventoryIndex;
            }
        }

        return -1;
    }

    public static Player getClosestPlayerWithActiveMagnet(Level world, Entity target) {
        List<? extends  Player> playersWithActiveMagnet = world.players().stream().filter(playerEntity -> {
            int magnetInventoryPosition = ItemMagnetHelper.getFirstActiveMagnetInventoryIndex(playerEntity);

            if (magnetInventoryPosition != -1) {
                ItemStack stack = playerEntity.getInventory().getItem(magnetInventoryPosition);

                if (!ItemMagnetHelper.getIsActive(stack)) {
                    return false;
                }

                double pickupDistance = ItemMagnetHelper.getPickupDistance(playerEntity, stack);
                return playerEntity.distanceToSqr(target) <= pickupDistance * pickupDistance;
            }

            return false;
        }).toList();

        int playerIndex  = WorldUtil.getClosestEntity(playersWithActiveMagnet, target);

        if (playerIndex != -1) {
            return playersWithActiveMagnet.get(playerIndex);
        } else {
           return null;
        }
    }


    public static void toggleIsActive(ItemStack stack) {
        boolean isActive = ItemMagnetHelper.getIsActive(stack);

        stack.set(ModComponents.ITEM_MAGNET_ITEM_IS_ACTIVE_COMPONENT, !isActive);
    }

    public static boolean getIsActive(ItemStack stack) {
        return stack.getOrDefault(ModComponents.ITEM_MAGNET_ITEM_IS_ACTIVE_COMPONENT, false);
    }

    public static ItemStack getFirstActiveMagnet(Player player) {
        int inventoryIndex = getFirstActiveMagnetInventoryIndex(player);
        return inventoryIndex == -1 ? ItemStack.EMPTY : player.getInventory().getItem(inventoryIndex);
    }

    public static int getPickupDistance(Player player, ItemStack stack) {
        return ModConfig.magnetDistance + (getMagneticReachLevel(player, stack) * 4);
    }

    private static int getMagneticReachLevel(Player player, ItemStack stack) {
        Holder<Enchantment> enchantmentHolder = player.level()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(ModEnchantments.MAGNETIC_REACH);

        return EnchantmentHelper.getItemEnchantmentLevel(enchantmentHolder, stack);
    }
}
