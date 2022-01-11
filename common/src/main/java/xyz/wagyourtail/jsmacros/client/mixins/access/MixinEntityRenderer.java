package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.IMixinEntity;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer {

    @Inject(method = "getOutlineColor", at = @At("HEAD"), cancellable = true)
    public void onGetTeamColor(Entity entity, CallbackInfoReturnable<Integer> cir) {
        int glowingColor = ((IMixinEntity) entity).jsmacros_getGlowingColor();
        if(glowingColor != -1) {
            cir.setReturnValue(glowingColor);
            cir.cancel();
        }
    }

}
