package eu.tomaseu.browsergui.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side entrypoint for the Browser GUI mod. Registers the toggle
 * keybind and the minimap HUD overlay, and ensures the embedded browser
 * is cleanly closed when the game exits, so no browser-held resources
 * are leaked. MCEF itself handles its own shutdown (see
 * {@link BrowserManager}'s class-level documentation), so this mod
 * only needs to close its own browser instance, not shut MCEF down.
 * <p>
 * {@code ClientLifecycleEvents.CLIENT_STOPPING} was not directly
 * observed in the MCEF source distribution used to verify the rest of
 * this project, but it is long-standing, stable Fabric API (part of
 * {@code fabric-lifecycle-events-v1}, included in the Fabric API
 * dependency already required by this mod) that has not changed shape
 * across recent Minecraft versions, unlike the rendering/input APIs
 * that needed direct source verification.
 * <p>
 * <b>Browser creation is lazy</b> - it happens the first time the
 * player presses J, inside {@link BrowserScreen#init()}, exactly as in
 * the original confirmed-working design. An eager, tick-based creation
 * scheme was tried so the minimap could show a live view from game
 * launch, but that change coincided with an unresolved intermittent
 * white-screen rendering issue, so it was reverted. See
 * {@link BrowserManager}'s class-level documentation for the full
 * history. Practical effect: {@link MinimapHud} will not show live
 * content until the player has opened the browser with J at least once
 * in the current session.
 */
public final class BrowserGuiClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("browsergui");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Browser GUI client");

        Keybinds.register();
        MinimapHud.register();

        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            LOGGER.info("Closing browser");
            BrowserManager.shutdown();
        });
    }
}
