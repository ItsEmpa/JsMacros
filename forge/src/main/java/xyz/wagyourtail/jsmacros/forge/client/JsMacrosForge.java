package xyz.wagyourtail.jsmacros.forge.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.classes.TextBuilder;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FKeyBind;
import xyz.wagyourtail.jsmacros.client.gui.editor.highlighting.impl.TextStyleCompiler;
import xyz.wagyourtail.jsmacros.client.tick.TickBasedEvents;
import xyz.wagyourtail.jsmacros.forge.client.api.classes.TextBuilderForge;
import xyz.wagyourtail.jsmacros.forge.client.gui.editor.highlighting.impl.TextStyleCompilerForge;
import xyz.wagyourtail.wagyourgui.BaseScreen;

import java.io.File;

@Mod(modid = JsMacros.MOD_ID, version = "@VERSION@", guiFactory = "xyz.wagyourtail.jsmacros.forge.client.JsMacrosModConfigFactory")
public class JsMacrosForge {
    public static final File configFolder = new File(Minecraft.getInstance().runDirectory, "config/jsMacros");

    public JsMacrosForge() {
        // needs to be earlier because forge does this too late and Core.instance ends up null for first sound event
        JsMacros.onInitialize();
    }

    @Mod.EventHandler
    public void onInitialize(FMLInitializationEvent event) {

        // initialize loader-specific stuff
        MinecraftForge.EVENT_BUS.register(this);
        TextStyleCompiler.getTextStyleCompiler = TextStyleCompilerForge::new;
        TextBuilder.getTextBuilder = TextBuilderForge::new;
        ClientRegistry.registerKeyBinding(JsMacros.keyBinding);

        // load fabric-style plugins
        FakeFabricLoader.instance.loadEntries();
    }

    @Mod.EventHandler
    public void onInitializeClient(FMLPostInitializationEvent event) {
        JsMacros.onInitializeClient();

        // load fabric-style plugins
        FakeFabricLoader.instance.loadClientEntries();
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            TickBasedEvents.onTick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent keyEvent) {
        if (Keyboard.getEventKeyState() ^ FKeyBind.pressedKeys.contains(Keyboard.getEventKey()))
            new EventKey(Keyboard.getEventKey(), 0, Keyboard.getEventKeyState() ? 1 : 0, BaseScreen.createModifiers());
    }

    @SubscribeEvent
    public void onMouse(InputEvent.MouseInputEvent mouseEvent) {
        if (Mouse.getEventButtonState() ^ FKeyBind.pressedKeys.contains(Mouse.getEventButton() - 100))
            new EventKey(Mouse.getEventButton() - 100, 0, Mouse.getEventButtonState() ? 1 : 0, BaseScreen.createModifiers());
    }
}
