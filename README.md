# Key Filter

A client-side Minecraft mod that lets you lock, hide, and disable key bindings.
Supports both Fabric and NeoForge.

## Features

For every key binding you can toggle three independent states:

- **Lock** — prevents the binding from being changed, so you don't overwrite it by accident.
- **Hide** — hides the entry from the vanilla Controls screen to keep the list clean.
- **Disable** — makes the game ignore the key entirely, even if another mod or feature would normally consume it.

## Usage

1. Press **F8** in-game to open the Key Filter config screen.
   (Default binding — it can be rebound from the vanilla Controls menu.)
2. In the list, use the buttons next to each binding to toggle its state:
   - `Lock` / `Unlock`
   - `Hide` / `Show`
   - `Disable` / `Enable`

Settings are stored in `config/keyfilter.json`.

![Configuration](https://cdn.modrinth.com/data/HVKQ0mcv/images/721b291f80e04dc551d55ef39cc49c739073958d.png)
