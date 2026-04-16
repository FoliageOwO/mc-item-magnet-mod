package net.davdeo.itemmagnetmod.mixin;

import net.davdeo.itemmagnetmod.config.ModConfig;
import net.davdeo.itemmagnetmod.item.ModItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin extends ItemCombinerMenu {
    @Shadow
    private int repairItemCountCost;

    @Shadow
    private DataSlot cost;

    @Shadow
    private boolean onlyRenaming;

    @Shadow
    private String itemName;

    protected AnvilMenuMixin() {
        super(null, 0, null, null, null);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void itemMagnetMod$createCustomRepairResult(CallbackInfo ci) {
        ItemStack leftStack = this.inputSlots.getItem(0);
        ItemStack rightStack = this.inputSlots.getItem(1);

        if (leftStack.isEmpty() || leftStack.getItem() != ModItems.ITEM_MAGNET || rightStack.isEmpty()) {
            return;
        }

        int repairAmount = getRepairAmount(leftStack, rightStack);
        if (repairAmount <= 0 || !leftStack.isDamageableItem() || !leftStack.isDamaged()) {
            return;
        }

        ItemStack resultStack = leftStack.copy();
        resultStack.setDamageValue(Math.max(0, resultStack.getDamageValue() - repairAmount));

        this.repairItemCountCost = 1;
        this.onlyRenaming = false;
        this.cost.set(1);
        this.resultSlots.setItem(0, resultStack);
        ci.cancel();
    }

    private int getRepairAmount(ItemStack leftStack, ItemStack rightStack) {
        if (rightStack.getItem() == ModItems.MAGNET_CORE) {
            return leftStack.getDamageValue();
        }

        if (rightStack.getItem() == Items.IRON_INGOT) {
            return Math.max(1, ModConfig.ironIngotRepairAmount);
        }

        return 0;
    }
}
