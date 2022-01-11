package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

@Mixin(value = EntityRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), method = "renderWorld(IFJ)V")
    public void render(int p_renderWorld_1_, float p_renderWorld_2_, long p_renderWorld_3_, CallbackInfo ci) {
        
        for (Draw3D d : ImmutableSet.copyOf(FHud.renders)) {
            try {
                d.render();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
