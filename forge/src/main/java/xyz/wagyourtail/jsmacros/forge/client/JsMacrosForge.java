package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraft.client.MinecraftClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.CommandBuilder;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.CommandBuilderForge;

import java.io.File;

@Mod(JsMacros.MOD_ID)
public class JsMacrosForge {
    public static final File configFolder = new File(MinecraftClient.getInstance().runDirectory, "config/jsMacros");

    public JsMacrosForge() {

        System.setProperty("jnr.ffi.provider", "cause.class.not.found.please");

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitialize);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onInitializeClient);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, parent) -> {
            JsMacros.prevScreen.setParent(parent);
            return JsMacros.prevScreen;
        });

        // needs to be earlier because forge does this too late and Core.instance ends up null for first sound event
        JsMacros.onInitialize();
    }

    public void onInitialize(FMLCommonSetupEvent event) {

        // initialize loader-specific stuff
        CommandBuilder.createNewBuilder = CommandBuilderForge::new;
        MinecraftForge.EVENT_BUS.addListener(this::onTick);
        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);

        // load fabric-style plugins
        FakeFabricLoader.instance.loadEntries();
    }

    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(MinecraftClient.getInstance());
        }
    }

    public void onInitializeClient(FMLClientSetupEvent event) {
        JsMacros.onInitializeClient();

        // load fabric-style plugins
        FakeFabricLoader.instance.loadClientEntries();
    }
}
