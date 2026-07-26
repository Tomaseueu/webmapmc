package eu.tomaseu.browsergui.client;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BrowserManager {

    private static final Logger LOGGER = LoggerFactory.getLogger("browsergui");

    private static MCEFBrowser browser;
    private static boolean visible = false;
    private static int currentWidth = BrowserConfig.INITIAL_WIDTH;
    private static int currentHeight = BrowserConfig.INITIAL_HEIGHT;
    private static boolean loadVerified = false;

    private BrowserManager() {
    }

    public static synchronized boolean ensureBrowserReady() {
        if (browser != null) {
            return true;
        }

        if (!MCEF.isInitialized()) {
            LOGGER.debug("MCEF is not initialized yet; browser cannot be created.");
            return false;
        }

        try {
            browser = MCEF.createBrowser(
                    BrowserConfig.TARGET_URL,
                    BrowserConfig.TRANSPARENT,
                    currentWidth,
                    currentHeight
            );
        } catch (RuntimeException exception) {
            LOGGER.error("Failed to create browser instance", exception);
            return false;
        }

        loadVerified = false;
        if (browser != null) {
            scheduleLoadVerification();
        }

        return browser != null;
    }

    private static void scheduleLoadVerification() {
        Minecraft client = Minecraft.getInstance();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
                return;
            }
            client.execute(BrowserManager::verifyLoadedOrReload);
        }, "browsergui-load-verify").start();
    }

    public static synchronized void verifyLoadedOrReload() {
        if (loadVerified || browser == null) {
            return;
        }
        loadVerified = true;

        try {
            String currentUrl = browser.getURL();
            boolean looksCorrect = currentUrl != null && currentUrl.startsWith(BrowserConfig.TARGET_URL);
            if (!looksCorrect) {
                LOGGER.warn("Browser did not navigate to target URL as expected (was: {}); forcing reload.", currentUrl);
                browser.loadURL(BrowserConfig.TARGET_URL);
            }
        } catch (RuntimeException exception) {
            LOGGER.error("Error while verifying browser load state", exception);
        }
    }

    public static synchronized boolean isVisible() {
        return visible;
    }

    public static synchronized void show() {
        visible = true;
        if (browser != null) {
            browser.setFocus(true);
        }
    }

    public static synchronized void hide() {
        visible = false;
        if (browser != null) {
            browser.setFocus(false);
        }
    }

    public static synchronized void resize(int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        currentWidth = width;
        currentHeight = height;
        if (browser != null) {
            browser.resize(width, height);
        }
    }

    public static synchronized int getCurrentWidth() {
        return currentWidth;
    }

    public static synchronized int getCurrentHeight() {
        return currentHeight;
    }

    public static synchronized MCEFBrowser getBrowser() {
        return browser;
    }

    public static synchronized void shutdown() {
        if (browser != null) {
            try {
                browser.close();
            } catch (RuntimeException exception) {
                LOGGER.error("Error while closing browser instance", exception);
            }
            browser = null;
        }
        visible = false;
        loadVerified = false;
    }
}
