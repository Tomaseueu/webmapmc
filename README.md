# webmapmc

A Fabric mod for Minecraft 26.1.2 that embeds a live Chromium browser (via [MCEF](https://github.com/Keksuccino/mcef)) directly inside the game.

Press **J** to open a fullscreen in-game browser. Press it again, or **Esc**, to close it. A small corner overlay - styled after Xaero's Minimap - also shows a zoomed-in live crop of the page in the top-right of your screen during normal gameplay.

## Features

- **Fullscreen browser (J key)** - loads a configured URL inside a real Minecraft `Screen`, with full mouse, keyboard, scroll, and text input forwarded to the page.
- **Persistent session** - closing the browser hides it without destroying it; page state, cookies, and JS execution keep running in the background, so reopening is instant.
- **Corner minimap overlay** - once the browser has been opened at least once, a small always-on HUD box in the top-right shows a live, zoomed view of the center of the page during normal gameplay. It automatically hides whenever any menu/screen is open.
- **Self-healing load check** - a few seconds after the browser is created, the mod verifies it actually navigated to the target page and forces a reload if it appears stuck on a blank placeholder.

## Configuration

Everything you'd want to change lives in one file: [`BrowserConfig.java`](src/client/java/eu/tomaseu/browsergui/client/BrowserConfig.java).

| Constant | Purpose |
|---|---|
| `TARGET_URL` | The page the browser opens |
| `TRANSPARENT` | Whether the browser surface renders with an alpha channel |
| `INITIAL_WIDTH` / `INITIAL_HEIGHT` | Fallback browser surface size before the first resize |
| `MINIMAP_SOURCE_REGION_SIZE` | Size (px) of the square cropped from the center of the page for the minimap |
| `MINIMAP_DISPLAY_SIZE` | On-screen size (px) of the minimap box - the crop is scaled/zoomed to fill it |
| `MINIMAP_MARGIN` | Distance (px) from the top-right corner of the screen |

## Architecture

| Class | Responsibility |
|---|---|
| `BrowserGuiClient` | Client entrypoint - registers the keybind and the minimap, closes the browser on shutdown |
| `Keybinds` | Registers **J**, edge-detects presses, opens/closes the browser screen |
| `BrowserScreen` | The fullscreen `Screen` - draws the browser texture each frame and forwards all input |
| `BrowserManager` | Owns the single shared `MCEFBrowser` instance: lazy creation, show/hide without destroying, resize, load verification, and final shutdown |
| `MinimapHud` | Registers a `HudElementRegistry` layer that draws the cropped/zoomed corner overlay |
| `BrowserConfig` | All user-facing configuration in one place |

Browser creation is **lazy** - it only happens the first time you press **J**, matching MCEF's own lifecycle (MCEF itself finishes initializing around the title screen, via its own `Minecraft` mixin, and is left to manage its own init/shutdown). The minimap simply shows nothing until that first press, then works continuously in the background afterward.

## Requirements

- Minecraft **26.1.2**
- [Fabric Loader](https://fabricmc.net/) 0.18.6+
- [Fabric API](https://modrinth.com/mod/fabric-api) 0.145.3+26.1.1
- [MCEF (Keksuccino's fork)](https://github.com/Keksuccino/mcef) 2.2.0-26.1.1, from `https://keksuccino.github.io/maven/`

## Building

```
./gradlew clean build
```

The built jar will be in `build/libs/`.

## Notes

This is a personal/hobby project built for a specific server's live map. All API usage was verified directly against MCEF's own source and Fabric's official current documentation rather than assumed, given how much Fabric/Loom tooling changed for Minecraft 26.1 (no more Yarn mappings, no more mod remapping, `HudRenderCallback` removed in favor of `HudElementRegistry`, etc.) - see the inline documentation in each class for the specifics of what was confirmed vs. inferred.
