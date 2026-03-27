# Mini Music Tweaks

<a href='https://files.minecraftforge.net'><img alt="forge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/forge_vector.svg"></a>
<a href='https://fabricmc.net'><img alt="fabric" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/fabric_vector.svg"></a>
<a href='https://neoforged.net/'><img alt="neoforge" height="56" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/neoforge_vector.svg"></a>

Mini Music Tweaks is a client-side mod that enhances Minecraft's music by adding dynamic combat
music, the music toast notifications from 1.21.6, and overhauled jukebox audio.

## Features

* **Dynamic Combat Music**
    * Play configurable combat music when you take damage or when a specified number of hostile mobs pursue you. The
      music naturally fades out after a configurable amount of time after the battle ends.
    * Disabled by default.
* **Music Toasts ("Now Playing")**
    * Whenever background music or a music disc starts playing, a toast notification will
      slide in showing the track's title and author.
    * This is a backported feature from 1.21.6.
* **Better Jukeboxes**
    * Vanilla music now smoothly crossfades with jukebox music, based on distance from the jukebox.
    * Replaces the default jukebox audio falloff with a system that smoothly crossfades and adjusts
      volume based on your distance from the jukebox.
    * Jukebox music now plays in mono, fixing the annoying "orbital" effect when not facing the jukebox directly.
* **Music Frequency**
    * Added a config option to change the frequency of in-game background music (default, frequent, or constant).
    * This is another backported feature from 1.21.6.
* **Vanilla Sound Options Integration**
    * Easily adjust music frequency, toggle music toasts, and adjust jukebox distance directly from the vanilla sound
      options screen.

## Modpack / Resource Pack Support

Modpack developers and resource pack makers can fully customize the metadata displayed in Music Toasts. You can provide
titles and authors for any track by including a `music_tweaks/musics.json` file.

## License

[![Code license (MIT)](https://img.shields.io/badge/code%20license-MIT-green.svg?style=flat-square)](https://github.com/evanbones/Mini-Music-Tweaks/blob/1.20.1/LICENSE)

---

[![discord-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/discord-plural_vector.svg)](https://discord.com/invite/6twDUSQBc4) [![github-plural](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/social/github-plural_vector.svg)](https://github.com/evanbones/Mini-Music-Tweaks)