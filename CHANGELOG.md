# Changelog

## [6.0.1] - 2026-09-03

### Fixed

- Register custom renderers only when the corresponding Jukebox, Brewing Stand, or Furnace Enhancements option is enabled, improving compatibility with other renderer mods (such as Amendments). The affected settings now indicate that they require a restart.
- Resolve texture namespaces correctly so modded wooden fuels render with their own textures inside enhanced furnaces.
- Send brewing stand, furnace, and jukebox inventory updates only when their contents change or a new player starts observing them, eliminating per-tick packet spam.
- Restore normal jukebox behavior during record playback. Visual Overhaul previously replaced the vanilla jukebox ticker with its inventory-sync ticker, preventing the vanilla tick logic from running.
- Make the colored lily-pad setting work independently from biome-based item colors on NeoForge.
- Corrected Fabric loader, API, and Java requirements and NeoForge dependency declarations.

## [6.0.0] - 2026-08-24

Minecraft 1.21.1 backport of [upstream Visual Overhaul v6.0.0], relative to [v5.2.1].

### Added

- Support biome-aware potion coloring on tipped arrows.
- Add Traditional Chinese (`zh_tw`) localization.

### Changed

- Replace the bundled per-disc round-model resource pack with runtime generation based on each disc item’s model texture. This automatically supports discs added by most mods and data packs, with a fallback texture for unresolved models.
- Improve resource-pack support for customizable jukebox-top decorations and button icons.
- Use explicit, readable `[VO:L]` names for the bundled No Brewing Bottles, Fancy Furnaces, and Colored Water Buckets resource packs.
- Identify the backport as **Visual Overhaul: Legacy**, target Minecraft 1.21.1 on Fabric and NeoForge, and point project and issue links to the backport repository.

### Fixed

- Restore biome-colored water in water and aquatic-mob buckets.
- Use the correct alpha channel for biome-aware item and potion tints.
- Darken disabled button icons consistently and prevent their hover-zoom effect.
- Defer icon initialization for pressable buttons and sliders, avoiding initialization with a partially constructed widget.

[6.0.1]: https://github.com/Sythiex/VisualOverhaul/commit/144d4c5caf1fffa0c715ae1ac9c56528c7c27c51
[6.0.0]: https://github.com/Sythiex/VisualOverhaul/commit/4dbaf55b90e68343afb0a665219a89ef8843bfc3
[upstream Visual Overhaul v6.0.0]: https://github.com/TeamMidnightDust/VisualOverhaul/commit/f583f515f4503b373c0242ca4610cd70cac3491f
[v5.2.1]: https://github.com/TeamMidnightDust/VisualOverhaul/commit/51c354c53313504df80056b6900214fa22d21ca9
