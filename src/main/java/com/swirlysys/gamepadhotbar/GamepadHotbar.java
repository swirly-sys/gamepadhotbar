package com.swirlysys.gamepadhotbar;

import com.swirlysys.gamepadhotbar.config.GamepadHotbarClientConfig;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(GamepadHotbar.MOD_ID)
public class GamepadHotbar {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "gamepadhotbar";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public GamepadHotbar(ModContainer modContainer) {
        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.CLIENT, GamepadHotbarClientConfig.SPEC);
    }
}
