# VoidTrade

Reverses Minecraft bug **MC-50614** ("Villager trading window is not closed
when villager leaves interaction range") for **Minecraft 1.21.4 (Fabric)**.

Only the interaction-range condition Mojang added in 1.21.4's
`AbstractVillager#stillValid(Player)` is touched. Nothing else about
villager trading, AI, or any other 1.21.4 behavior is changed.

## What it does

- Villager walks out of the normal ~4-block trading interaction range while
  a player has the trading screen open → **screen stays open** (this is the
  restored pre-fix / 1.21.3 behavior).
- Villager dies while trading → screen still closes (untouched).
- A different player can't hijack an open trade → untouched.

See `src/main/java/com/neil/voidtrade/mixin/AbstractVillagerMixin.java` for
the full before/after diff and reasoning in the doc comment.

## Before you build

This project was assembled without network access, so **the Gradle wrapper
jar (`gradle/wrapper/gradle-wrapper.jar`) is not included** — a wrapper jar
is a binary file I can't produce without downloading it. Generate it
yourself the first time:

```
gradle wrapper --gradle-version 8.10
```

(If you don't have a system-wide `gradle` install, download one temporarily
from https://gradle.org/releases/ just to run that one command — after that
`gradlew`/`gradlew.bat` in this project work standalone.)

Also double-check `gradle.properties` — `loader_version` and `fabric_version`
against https://fabricmc.net/develop and https://modrinth.com/mod/fabric-api
before building, since these move fast and the values here were only current
as of when this project was put together.

## Build

```
gradlew.bat build          # Windows
./gradlew build            # Linux/macOS
```

Output jar: `build/libs/voidtrade-1.0.0.jar`

## Install

Drop the built jar into your `.minecraft/mods` folder alongside:
- Fabric Loader (matching `loader_version` in `gradle.properties`)
- Fabric API (matching `fabric_version` in `gradle.properties`)

## Test plan

1. Open trade with a villager, walk >4 blocks away → screen should **stay open**.
2. Open trade, `/kill` the villager → screen should **close**.
3. Open trade, let the chunk unload / change dimension → not fully verified
   in this project; test in your environment (see mixin doc comment for the
   `isRemoved()` caveat).
