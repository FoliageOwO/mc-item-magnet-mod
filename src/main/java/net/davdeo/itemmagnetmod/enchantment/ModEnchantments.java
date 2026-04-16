package net.davdeo.itemmagnetmod.enchantment;

import net.davdeo.itemmagnetmod.ItemMagnetMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.enchantment.Enchantment;

public final class ModEnchantments {
    private ModEnchantments() {
        super();
    }

    public static final ResourceKey<Enchantment> MAGNETIC_RESERVE = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(ItemMagnetMod.MOD_ID, "magnetic_reserve")
    );

    public static final ResourceKey<Enchantment> MAGNETIC_REACH = ResourceKey.create(
            Registries.ENCHANTMENT,
            Identifier.fromNamespaceAndPath(ItemMagnetMod.MOD_ID, "magnetic_reach")
    );
}
