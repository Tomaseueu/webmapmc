package eu.tomaseu.browsergui.client;

/**
 * Static configuration for the embedded browser.
 * <p>
 * Set {@link #TARGET_URL} to whatever page you want the in-game browser
 * to open. This class intentionally contains no logic beyond holding
 * configuration values, so the target can be changed without touching
 * any other file in the mod.
 */
public final class BrowserConfig {

    public static final String TARGET_URL = "https://origin.badhub.cz";

    public static final boolean TRANSPARENT = false;

    public static final int INITIAL_WIDTH = 1920;

    public static final int INITIAL_HEIGHT = 1080;

    /**
     * Side length, in pixels, of the square region cropped from the
     * center of the browser page for the minimap overlay.
     */
    public static final int MINIMAP_SOURCE_REGION_SIZE = 250;

    /**
     * Side length, in pixels (GUI-scaled screen pixels), of the minimap
     * box drawn on screen. The cropped {@link #MINIMAP_SOURCE_REGION_SIZE}
     * region is scaled/zoomed to fill this box.
     */
    public static final int MINIMAP_DISPLAY_SIZE = 128;

    /**
     * Distance, in GUI-scaled screen pixels, from the top and right
     * edges of the screen to the minimap box.
     */
    public static final int MINIMAP_MARGIN = 6;

    private BrowserConfig() {
    }
}
