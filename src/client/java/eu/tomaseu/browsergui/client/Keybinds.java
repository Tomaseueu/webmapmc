package eu.tomaseu.browsergui.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public final class Keybinds {

    private static final KeyMapping TOGGLE_BROWSER_KEY = new KeyMapping(
            "key.browsergui.toggle",
            InputConstants.KEY_J,
            KeyMapping.Category.MISC
    );

    private static boolean wasDownLastTick = false;

    private Keybinds() {
    }

    public static void register() {
        KeyMappingHelper.registerKeyMapping(TOGGLE_BROWSER_KEY);
        ClientTickEvents.START_CLIENT_TICK.register(Keybinds::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        boolean isDownNow = TOGGLE_BROWSER_KEY.isDown();
        if (isDownNow && !wasDownLastTick) {
            onToggle(client);
        }
        wasDownLastTick = isDownNow;
    }

    private static void onToggle(Minecraft client) {
        if (BrowserManager.isVisible()) {
            client.setScreen(null);
            BrowserManager.hide();
            return;
        }

        if (!BrowserManager.ensureBrowserReady()) {
            return;
        }

        client.setScreen(new BrowserScreen());
    }
}
