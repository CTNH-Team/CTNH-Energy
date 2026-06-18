# CTNH-Energy KNOWLEDGE BASE

## OVERVIEW
CTNH-Energy adds AE2/energy integration, pattern buffer machinery, quantum computer systems, AE2 mixins, EMI/Jade integration, and generated resources under mod id `ctnhenergy`.

## WHERE TO LOOK
- Mod entry: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CTNHEnergy.java`. Forge mod initialization.
- GT addon: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CTNHEnergyGTAddon.java`. GTCEu integration.
- Config/settings: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/CEConfig.java`, `common/CESettings.java`. Module configuration and settings.
- AE2/EU logic: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/me/`. `EUKey`, `VoltageKey`, EU cells, EU container strategy, ME machine EU handler, EU P2P tunnel, and energy distribution service.
- Pattern buffer: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/machine/patternbuffer/`. Buffer machines and proxy parts.
- AE2 machines/hatches: `common/machine/iohatch/`, `common/machine/energy/`, `common/machine/multiblock/`. ME stocking/tag stocking buses, energy hatches, dynamos, and EU-aware GT machine integrations.
- Quantum computer: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/common/quantumcomputer/`. CPU/job/menu logic.
- Registries: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/registry/`. Items and multiblocks.
- XEI/Jade: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/integration/`. EMI plugin plus AE2/ME pattern buffer Jade providers.
- Ponder/client: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/client/ponder/`. Energy-owned AE2 Ponder plugin, scene/tag registrations, adapter builder, and scene implementations.
- Mixins: `src/main/java/tech/luckyblock/mcmod/ctnhenergy/mixin/`, `src/main/resources/ctnhenergy.mixins.json`. AE2, AE2CS/AECS, Better P2P, GTM, Omni Cells, PCC, and ME Requester integration points.

## REGISTRATION ENTRYPOINTS
- Registrate/root: `registry/CERegistrate.java`; mod/addon entrypoints are `CTNHEnergy.java` and `CTNHEnergyGTAddon.java`.
- GT addon hooks: `CTNHEnergyGTAddon.initializeAddon()` initializes `CEBlocks` and `CEItems`; broader AE2/EU wiring is in `common/CommonProxy.java`.
- Common proxy init: `CommonProxy.init()` initializes config, registrate, AE menus, networking, datagen, gatherData listener, creative tabs, and AE key type registration.
- Common setup: registers `EnergyDistributeService`, EU container strategy, EU cell handler/upgrades, pattern-provider upgrade cards, and EU P2P attunement.
- GT capability bridge: `CommonProxy.attachCapabilities()` adds `generic_eu_wrapper` through `common/me/MEMachineEUHandler.java`.
- Items/blocks: `registry/CEItems.java`, `registry/CEBlocks.java`; EU cell item logic lives under `common/item/`.
- Machines/multiblocks: `registry/CEMachines.java`, `registry/CEMultiblock.java`; registered from `common/CommonProxy.registerMachines()`.
- Recipe types: `registry/CERecipeTypes.java`; registered from `common/CommonProxy.registerRecipeTypes()`.
- AE/menu/network: `registry/AEMenus.java`, `registry/CENetWorking.java`.
- AE keys/cells/P2P: `common/me/key/`, `common/me/cell/`, `common/me/parts/p2p/`.
- Jade providers: `integration/jade/CTNHEnergyJadePlugin.java`, `AdMEPatternBufferProvider.java`, `AdMEPatternBufferProxyProvider.java`, `AEDeviceEUProvider.java`.
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
- Do not register EU key/cell behavior only in item code; AE2 key types, storage cell handler, container strategy, upgrades, and P2P attunement are separate CommonProxy hooks.
