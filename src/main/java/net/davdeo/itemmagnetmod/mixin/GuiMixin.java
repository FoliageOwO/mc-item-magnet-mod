package net.davdeo.itemmagnetmod.mixin;

import net.davdeo.itemmagnetmod.ItemMagnetModClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "setOverlayMessage", at = @At("HEAD"))
    private void itemmagnetmod$trackDebugOverlay(Component component, boolean tinted, CallbackInfo ci) {
        if (ItemMagnetModClient.isDebugOverlayMessage(component.getString())) {
            ItemMagnetModClient.suppressActiveOverlay();
        }
    }
}
