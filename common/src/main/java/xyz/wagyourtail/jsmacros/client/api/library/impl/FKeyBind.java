package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.*;

/**
 *
 * Functions for getting and modifying key pressed states.
 * 
 * An instance of this class is passed to scripts as the {@code KeyBind} variable.
 * 
 * @author Wagyourtail
 */
 @Library("KeyBind")
 @SuppressWarnings("unused")
 public class FKeyBind extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getInstance();
    /**
     * Don't modify
     */
    public static final Set<Integer> pressedKeys = new HashSet<>();

    //    /**
    //     * Dont use this one... get the raw minecraft keycode class.
    //     *
    //     * @param keyName
    //     * @return the raw minecraft keycode class
    //     */
    //    public int getKeyCode(String keyName) {
    //        try {
    //            return InputUtil.fromName(keyName);
    //        } catch (Exception e) {
    //            return InputUtil.UNKNOWN_KEYCODE;
    //        }
    //        return 0;
    //    }

    /**
     * @since 1.2.2
     *
     * @return A {@link java.util.Map Map} of all the minecraft keybinds.
     */
    public Map<String, Integer> getKeyBindings() {
        Map<String, Integer> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.options.keysAll)) {
            keys.put(key.getTranslationKey(), key.getCode());
        }
        return keys;
    }

    /**
     * Sets a minecraft keybind to the specified key.
     *
     * @since 1.2.2
     *
     * @param bind
     * @param key
     */
    public void setKeyBind(String bind, int key) {
        for (KeyBinding keybind : mc.options.keysAll) {
            if (keybind.getTranslationKey().equals(bind)) {
                keybind.setCode(key);
                return;
            }
        }
    }

    /**
     * Set a key-state for a key.
     *
     * @param keyBind
     * @param keyState
     */
    public void key(int keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind);
        KeyBinding.setKeyPressed(keyBind, keyState);

        // add to pressed keys list
        if (keyState) pressedKeys.add(keyBind);
        else pressedKeys.remove(keyBind);
    }
    
    /**
     * Set a key-state using the name of the keybind rather than the name of the key.
     *
     * This is probably the one you should use.
     *
     * @since 1.2.2
     *
     * @param keyBind
     * @param keyState
     */
    public void keyBind(String keyBind, boolean keyState) {
        for (KeyBinding key : mc.options.keysAll) {
            if (key.getTranslationKey().equals(keyBind)) {
                if (keyState) KeyBinding.onKeyPressed(key.getCode());
                KeyBinding.setKeyPressed(key.getCode(), keyState);

                // add to pressed keys list
                if (keyState) pressedKeys.add(key.getCode());
                else pressedKeys.remove(key.getCode());
                return;
            }
        }
    }

    /**
     * Don't use this one... set the key-state using the raw minecraft keybind class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(KeyBinding keyBind, boolean keyState) {
        if (keyState) KeyBinding.onKeyPressed(keyBind.getCode());
        KeyBinding.setKeyPressed(keyBind.getCode(), keyState);

        // add to pressed keys list
        if (keyState) pressedKeys.add(keyBind.getCode());
        else pressedKeys.remove(keyBind.getCode());
    }

    /**
     * @since 1.2.6
     *
     * @return a list of currently pressed keys.
     */
    public List<Integer> getPressedKeys() {
        synchronized (pressedKeys) {
            return new ArrayList<>(pressedKeys);
        }
    }
}
