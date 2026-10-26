### Added

- Added support for Minecraft 26.3 (Fabric/ NeoForge).
- 1.20.1 now has every 1.21.1 feature: the `/music_tweaks dump` command, the reworked music toast and its options, missing-artist logging, and playing music while paused.
- 26.x now has the optional server-side jukebox sync, the `does_not_trigger_combat_music` damage type tag, and the 1.21.1 fixes for combat music overlap and music stopping after switching dimensions.

### Changed

- All versions are now built from a single codebase.
- Switched from Cloth Config and Kaleido to YACL. The config is now `config/music_tweaks.json` on every version, so 26.x settings need to be set again.
- The jukebox option is now split into "Mono Jukeboxes" and "Jukebox Music Fade Out" on every version.

### Fixed

- Fixed jukeboxes on 1.20.1 always resuming from the start of the song when synchronising.
- Fixed duplicate Music Frequency and music toast buttons with Vanilla Backport installed. Its Music Frequency option now takes over.
- Invalid combat music sound IDs in the config are now logged instead of crashing the game.