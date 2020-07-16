package xyz.wagyourtail.jsmacros.events.mixins;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import xyz.wagyourtail.jsmacros.events.HeldItemCallback;
import xyz.wagyourtail.jsmacros.reflector.ItemStackHelper;

@Mixin(InGameHud.class)
class jsmacros_InGameHudMixin {

    @Shadow
    @Final
    private MinecraftClient client;
    
    @Inject(at = @At(value="CONSTANT", args="intValue=40"), method="tick")
    public void jsmacros_tick(CallbackInfo info) {
        HeldItemCallback.EVENT.invoker().interact(new ItemStackHelper(client.player.inventory.getMainHandStack()));
    }
}