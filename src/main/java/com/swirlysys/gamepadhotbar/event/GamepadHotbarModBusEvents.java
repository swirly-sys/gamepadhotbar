package com.swirlysys.gamepadhotbar.event;

import com.mojang.blaze3d.platform.InputConstants;
import com.swirlysys.gamepadhotbar.GamepadHotbar;
import net.minecraft.client.KeyMapping;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = GamepadHotbar.MODID)
public class GamepadHotbarModBusEvents {
    public static KeyMapping HOTBAR_LEFT;
    public static KeyMapping HOTBAR_RIGHT;
    public static KeyMapping HOTBAR_UP;
    public static KeyMapping HOTBAR_DOWN;
    public static KeyMapping HOTBAR_WEAPON;

    @SubscribeEvent
    public static void modKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(HOTBAR_LEFT = new KeyMapping("key.gamepadhotbar.cyclehotbar_0",
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.gamepadhotbar")
        );
        event.register(HOTBAR_UP = new KeyMapping("key.gamepadhotbar.cyclehotbar_1",
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.gamepadhotbar")
        );
        event.register(HOTBAR_RIGHT = new KeyMapping("key.gamepadhotbar.cyclehotbar_2",
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.gamepadhotbar")
        );
        event.register(HOTBAR_DOWN = new KeyMapping("key.gamepadhotbar.cyclehotbar_3",
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.gamepadhotbar")
        );
        event.register(HOTBAR_WEAPON = new KeyMapping("key.gamepadhotbar.cyclehotbar_4",
                InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.gamepadhotbar")
        );
    }
}
