# DeathQuotes

## Preamble

I wanted to use this mod on version `1.18.2`, but couldn't find it for that version. So I ported mod to newer versions
with some enhancements.

Credits for the mod go to the original authors
on [curseforge](https://www.curseforge.com/minecraft/mc-mods/deathquotes-death-quotes).

## Ported mod to versions:

- `1.16.5`
- `1.17.1`
- `1.18.2`
- `1.19.2`-`1.19.4`
- `1.20.1`

## Supported mod loaders:

- `Forge`
- `Fabric`

> **Note**
>
> For `Fabric` version you will need `fabric-api` mod!

## Changes/Enhancements:

- File `deathquotes.txt` now loaded and saved using `UTF-8` encoding
    - As fallback encodings: default encoding and the old one (for Windows this could be `windows-1252` or `ANSI`) for
      your system and `UTF-16` are also checked
- Added config file creation
    - For `Forge` in path `./config/deathquotes-common.toml`
    - For `Fabric` in path `./config/deathquotes.toml`
- For versions `1.18+` mod now uses `Xoroshiro128++` PRNG (implemented by Mojang) instead of default Java
  LCG - `java.util.Random`
    - This provides getting more random quotes
- Added command to reload file `deathquotes.txt` in game - `/deathquotes reloadQuotes` (only available for server
  operators or when cheats are enabled)
- Added a method to prevent quotes from repeating so often and options for it in config file
- Added options to replace certain symbols with player name and next line symbol (also configurable)
- Added option to display death quotes based on the value of gamerule `showDeathMessages` or display regardless of it
- Added options to change format of send quotes:
    - Enable/disable quotation marks
    - Enable/disable italics
    - Enable/disable processing of http(s) links (when enabled - they're clickable and look like a [link]())