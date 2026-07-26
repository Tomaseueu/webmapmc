package eu.tomaseu.browsergui.client;

import com.cinemamod.mcef.MCEFBrowser;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class BrowserScreen extends Screen {

    public BrowserScreen() {
        super(Component.literal("Browser"));
    }

    @Override
    protected void init() {
        super.init();
        resizeBrowser();
        BrowserManager.show();
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        resizeBrowser();
    }

    private void resizeBrowser() {
        double guiScale = minecraft.getWindow().getGuiScale();
        int pixelWidth = (int) (this.width * guiScale);
        int pixelHeight = (int) (this.height * guiScale);
        BrowserManager.resize(pixelWidth, pixelHeight);
    }

    @Override
    public void onClose() {
        BrowserManager.hide();
        super.onClose();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partial) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partial);
        renderBrowserTexture(guiGraphics);
    }

    private void renderBrowserTexture(GuiGraphicsExtractor guiGraphics) {
        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null || !browser.isTextureReady()) {
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF101010);
            return;
        }

        Identifier textureLocation = browser.getTextureIdentifier();
        if (textureLocation == null) {
            guiGraphics.fill(0, 0, this.width, this.height, 0xFF101010);
            return;
        }

        guiGraphics.blit(
                RenderPipelines.GUI_TEXTURED,
                textureLocation,
                0,
                0,
                0.0F,
                0.0F,
                this.width,
                this.height,
                this.width,
                this.height
        );
    }

    private int mouseXPixels(double mouseX) {
        return (int) (mouseX * minecraft.getWindow().getGuiScale());
    }

    private int mouseYPixels(double mouseY) {
        return (int) (mouseY * minecraft.getWindow().getGuiScale());
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        boolean handled = super.mouseClicked(event, isDoubleClick);
        if (handled) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        browser.sendMousePress(mouseXPixels(event.x()), mouseYPixels(event.y()), event.button());
        browser.setFocus(true);
        return true;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        boolean handled = super.mouseReleased(event);
        if (handled) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        browser.sendMouseRelease(mouseXPixels(event.x()), mouseYPixels(event.y()), event.button());
        browser.setFocus(true);
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser != null) {
            browser.sendMouseMove(mouseXPixels(mouseX), mouseYPixels(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        boolean handled = super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
        if (handled) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        browser.sendMouseWheel(mouseXPixels(mouseX), mouseYPixels(mouseY), scrollY, 0);
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (super.keyPressed(event)) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        browser.sendKeyPress(event.key(), event.scancode(), event.modifiers());
        browser.setFocus(true);
        return true;
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (super.keyReleased(event)) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        browser.sendKeyRelease(event.key(), event.scancode(), event.modifiers());
        browser.setFocus(true);
        return true;
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (super.charTyped(event)) {
            return true;
        }

        MCEFBrowser browser = BrowserManager.getBrowser();
        if (browser == null) {
            return false;
        }

        if (event.codepoint() == 0) {
            return false;
        }

        browser.sendKeyTyped((char) event.codepoint(), 0);
        browser.setFocus(true);
        return true;
    }

    // NOTE: no isPauseScreen()/shouldCloseOnEsc() override here. This
    // version's exact Screen method name for "don't pause the game
    // behind this screen" could not be confirmed from the available
    // MCEF source, so this intentionally relies on Screen's default
    // behavior rather than guess at a method name that might not
    // compile. ESC closing the screen is handled by Screen's default
    // key handling calling onClose(), which already hides the browser
    // via BrowserManager.hide() above.
}
