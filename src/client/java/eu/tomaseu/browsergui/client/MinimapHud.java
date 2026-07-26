package eu.tomaseu.browsergui.client;

import com.cinemamod.mcef.MCEFBrowser;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public final class MinimapHud {

    private MinimapHud() {
    }

    public static void register() {
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.fromNamespaceAndPath("browsergui", "minimap"),
                MinimapHud::render
        );
    }

    private static void render(GuiGraphicsExtractor graphics, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();

        if (client.screen != null) {
            return;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null || !browser.isTextureReady()) {
            return;
        }

        Identifier textureLocation = browser.getTextureIdentifier();
        if (textureLocation == null) {
            return;
        }

        int sourceWidth = BrowserManager.getCurrentWidth();
        int sourceHeight = BrowserManager.getCurrentHeight();

        int regionSize = BrowserConfig.MINIMAP_SOURCE_REGION_SIZE;
        int clampedRegionWidth = Math.min(regionSize, sourceWidth);
        int clampedRegionHeight = Math.min(regionSize, sourceHeight);

        float centerU = (sourceWidth - clampedRegionWidth) / 2.0F;
        float centerV = (sourceHeight - clampedRegionHeight) / 2.0F;

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int displaySize = BrowserConfig.MINIMAP_DISPLAY_SIZE;
        int margin = BrowserConfig.MINIMAP_MARGIN;

        int destX = screenWidth - displaySize - margin;
        int destY = margin;

        int borderThickness = 1;
        graphics.fill(
                destX - borderThickness,
                destY - borderThickness,
                destX + displaySize + borderThickness,
                destY + displaySize + borderThickness,
                0xFF000000
        );

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                textureLocation,
                destX,
                destY,
                centerU,
                centerV,
                displaySize,
                displaySize,
                clampedRegionWidth,
                clampedRegionHeight,
                sourceWidth,
                sourceHeight
        );
    }
}
