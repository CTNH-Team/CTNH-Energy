# CTNH-Energy KNOWLEDGE BASE

## OVERVIEW
CTNH-Energy adds AE2/energy integration, pattern buffer machinery, quantum computer systems, AE2 mixins, EMI/Jade integration, and generated resources under mod id `ctnhenergy`.

## WHERE TO LOOK
- Mod entry: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CTNHEnergy.java`. Forge mod initialization.
- GT addon: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CTNHEnergyGTAddon.java`. GTCEu integration.
- Config/settings: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CEConfig.java`, `common/CESettings.java`. Module configuration and settings.
- AE2 logic: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/me/`. ME strategy/context logic.
- Pattern buffer: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/machine/patternbuffer/`. Buffer machines and proxy parts.
- Quantum computer: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/quantumcomputer/`. CPU/job/menu logic.
- Registries: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/registry/`. Items and multiblocks.
- Ponder/client: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/client/ponder/`. Energy-owned AE2 Ponder plugin, scene/tag registrations, adapter builder, and scene implementations.
- Mixins: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/mixin/`, `src/main/resources/ctnhenergy.mixins.json`. Mostly AE2 integration points.

## REGISTRATION ENTRYPOINTS
- Registrate/root: `registry/CERegistrate.java`; mod/addon entrypoints are `CTNHEnergy.java` and `CTNHEnergyGTAddon.java`.
- Items/blocks: `registry/CEItems.java`, `registry/CEBlocks.java`.
- Machines/multiblocks: `registry/CEMachines.java`, `registry/CEMultiblock.java`; registered from `common/CommonProxy.registerMachines()`.
- Recipe types: `registry/CERecipeTypes.java`; registered from `common/CommonProxy.registerRecipeTypes()`.
- AE/menu/network: `registry/AEMenus.java`, `registry/CENetWorking.java`.
- Creative tabs/datagen: `registry/CECreativeModeTabs.java`, `data/CEDatagen.java`.
- Ponder: `client/ponder/CTNHEnergyPonderPlugin.java` registers `CTNHEnergyPonderScenes` and `CTNHEnergyPonderTags`; scenes live in `client/ponder/ae2/` and use `scene.title(..., en, cn)` / `scene.showText(..., en, cn)` with text embedded directly in scene files. Datagen wires `common/CommonProxy.gatherData()` on the mod event bus and calls CTNH-Lib's `CTNHPonderLang.init(new CTNHEnergyPonderPlugin())` to extract Ponder language entries.
- Recipes are mostly integration behavior here; put broad crafting/processing recipes in Core unless they are Energy-only AE2 setup.

## CONVENTIONS
- Namespace is `tech.luckyblock.mcmod.ctnhenergy`; registry prefixes generally use `CE`.
- AE2 mixins are central to behavior; inspect target class assumptions before changing signatures.
- `src/generated/resources` is produced by `:modules:CTNH-Energy:runData`.
- Ponder `CTNHEnergyPonderSceneBuilder` is a thin adapter around CTNH-Lib's shared builder; `AE2CablePonderHelper` stays in Energy because it depends on AE2 cable bus internals.

## COMMANDS
```bash
./gradlew :modules:CTNH-Energy:build
./gradlew :modules:CTNH-Energy:runData
./gradlew :modules:CTNH-Energy:spotlessCheck
```

## ANTI-PATTERNS
- Do not change AE2 mixins without checking both mixin JSON and the target AE2 behavior.
- Do not treat quantum computer/menu updates as server-only; UI progress sync is part of the module.
- Do not move `client/ponder/ae2/AE2CablePonderHelper.java` to CTNH-Lib; it is AE2-specific visualization code.
