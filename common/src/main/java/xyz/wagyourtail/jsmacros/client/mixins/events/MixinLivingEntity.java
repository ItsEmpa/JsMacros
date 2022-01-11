package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventEntityDamaged;

@Mixin(EntityLivingBase.class)
public abstract class MixinLivingEntity {
    @Shadow public abstract float getHealth();

    @Inject(at = @At("TAIL"), method = "damage")
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
        new EventEntityDamaged((Entity)(Object) this, amount);
    }

}
