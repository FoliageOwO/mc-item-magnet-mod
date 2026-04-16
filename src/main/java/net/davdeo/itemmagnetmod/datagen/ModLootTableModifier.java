package net.davdeo.itemmagnetmod.datagen;

import net.davdeo.itemmagnetmod.enchantment.ModEnchantments;
import net.davdeo.itemmagnetmod.item.ModItems;
import net.davdeo.itemmagnetmod.config.ModConfig;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public class ModLootTableModifier {
    private ModLootTableModifier() {
        super();
    }

    private static void registerChestLoot(ResourceKey<LootTable> structure, int numberOfRolls, float chance, float minAmount, float maxAmount, Item item) {
        registerChestLootEntry(structure, numberOfRolls, chance, LootItem.lootTableItem(item)
                .apply(SetItemCountFunction.setCount(UniformGenerator.between(minAmount, maxAmount))));
    }

    private static void registerChestLootEntry(ResourceKey<LootTable> structure, int numberOfRolls, float chance, LootPoolEntryContainer.Builder<?> entryBuilder) {
        LootTableEvents.MODIFY.register(((id, tableBuilder, source, x) -> {
            if (source.isBuiltin() && structure.equals(id)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(numberOfRolls))
                        .when(LootItemRandomChanceCondition.randomChance(chance))
                        .add(entryBuilder);

                tableBuilder.withPool(poolBuilder);
            }
        }));
    }

    private static LootPoolEntryContainer.Builder<?> magnetEnchantmentBook(HolderLookup.Provider registries) {
        return magnetEnchantmentBook(registries, ModEnchantments.MAGNETIC_RESERVE);
    }

    private static LootPoolEntryContainer.Builder<?> magnetEnchantmentBook(HolderLookup.Provider registries, ResourceKey<net.minecraft.world.item.enchantment.Enchantment> enchantmentKey) {
        return LootItem.lootTableItem(Items.BOOK)
                .apply(EnchantRandomlyFunction.randomEnchantment()
                        .withEnchantment(
                                registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(enchantmentKey)
                        ));
    }

    public static void modifyLootTables() {
        if (ModConfig.canFindInBastion) {
            registerChestLoot(BuiltInLootTables.BASTION_TREASURE, 2, 0.35f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInBastion) {
            registerChestLoot(BuiltInLootTables.BASTION_BRIDGE, 1, 0.15f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInBastion) {
            registerChestLoot(BuiltInLootTables.BASTION_HOGLIN_STABLE, 1, 0.15f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInBastion) {
            registerChestLoot(BuiltInLootTables.BASTION_OTHER, 1, 0.15f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInAncientCity) {
            registerChestLoot(BuiltInLootTables.ANCIENT_CITY, 1, 0.35f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInEndCity) {
            registerChestLoot(BuiltInLootTables.END_CITY_TREASURE, 1, 0.15f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        if (ModConfig.canFindInStrongholdLibrary) {
            registerChestLoot(BuiltInLootTables.STRONGHOLD_LIBRARY, 1, 0.1f, 1.0f, 1.0f, ModItems.MAGNET_CORE);
        }

        LootTableEvents.MODIFY.register(((id, tableBuilder, source, registries) -> {
            if (!source.isBuiltin()) {
                return;
            }

            if (ModConfig.canFindInBastion && (
                    BuiltInLootTables.BASTION_TREASURE.equals(id) ||
                    BuiltInLootTables.BASTION_BRIDGE.equals(id) ||
                    BuiltInLootTables.BASTION_HOGLIN_STABLE.equals(id) ||
                    BuiltInLootTables.BASTION_OTHER.equals(id)
            )) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.12f))
                        .add(magnetEnchantmentBook(registries)));
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.08f))
                        .add(magnetEnchantmentBook(registries, ModEnchantments.MAGNETIC_REACH)));
            }

            if (ModConfig.canFindInAncientCity && BuiltInLootTables.ANCIENT_CITY.equals(id)) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.18f))
                        .add(magnetEnchantmentBook(registries)));
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.12f))
                        .add(magnetEnchantmentBook(registries, ModEnchantments.MAGNETIC_REACH)));
            }

            if (ModConfig.canFindInEndCity && BuiltInLootTables.END_CITY_TREASURE.equals(id)) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.12f))
                        .add(magnetEnchantmentBook(registries)));
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.08f))
                        .add(magnetEnchantmentBook(registries, ModEnchantments.MAGNETIC_REACH)));
            }

            if (ModConfig.canFindInStrongholdLibrary && BuiltInLootTables.STRONGHOLD_LIBRARY.equals(id)) {
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.2f))
                        .add(magnetEnchantmentBook(registries)));
                tableBuilder.withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .when(LootItemRandomChanceCondition.randomChance(0.14f))
                        .add(magnetEnchantmentBook(registries, ModEnchantments.MAGNETIC_REACH)));
            }
        }));
    }
}
