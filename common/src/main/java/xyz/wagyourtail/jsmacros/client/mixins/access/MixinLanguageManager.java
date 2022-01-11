package xyz.wagyourtail.jsmacros.client.mixins.access;


import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.JsMacrosJsonLangFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(LanguageManager.class)
public class MixinLanguageManager {
    
    @Shadow private String field_135048_c;
    
    @Shadow @Final protected static Locale field_135049_a;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraftforge/fml/common/registry/LanguageRegistry;mergeLanguageTable(Ljava/util/Map;Ljava/lang/String;)V", shift = At.Shift.AFTER, remap = false), method = "reload")
    public void onLanguageLoad(IResourceManager resourceManager, CallbackInfo ci) {
        Map<String, String> translations = new LinkedHashMap<>();
        List<String> langs = Lists.newArrayList("en_US");
        if (!field_135048_c.equals("en_US")) langs.add(field_135048_c);
        for (String lang : langs) {
            Set<String> res = JsMacrosJsonLangFile.getLangResources(lang);
            for (String r : res) {
                JsonObject ts = null;
                try (Reader reader = new InputStreamReader(JsMacrosJsonLangFile.class.getResourceAsStream(r))) {
                    ts = new JsonParser().parse(reader).getAsJsonObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ts.entrySet().forEach((e) -> translations.putIfAbsent(e.getKey(), e.getValue().getAsString()));
            }
        }
        translations.forEach(((MixinLocale)field_135049_a).getProperties()::putIfAbsent);
    }
}
