package tech.luckyblock.mcmod.ctnhenergy.common;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;

import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.behaviors.GenericSlotCapacities;
import appeng.api.features.P2PTunnelAttunement;
import appeng.api.networking.GridServices;
import appeng.api.networking.security.IActionHost;
import appeng.api.stacks.AEKeyTypes;
import appeng.api.storage.StorageCells;
import appeng.api.upgrades.Upgrades;
import appeng.core.definitions.AEBlocks;
import appeng.core.definitions.AEItems;
import appeng.core.definitions.AEParts;
import appeng.core.localization.GuiText;
import com.glodblock.github.extendedae.common.EPPItemAndBlock;
import io.github.lounode.ae2cs.common.init.AECSBlocks;
import io.github.lounode.ae2cs.common.init.AECSParts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.client.ponder.CTNHEnergyPonderPlugin;
import tech.luckyblock.mcmod.ctnhenergy.common.me.MEMachineEUHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.me.cell.EuCellHandler;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKeyType;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.VoltageKeyType;
import tech.luckyblock.mcmod.ctnhenergy.common.me.service.EnergyDistributeService;
import tech.luckyblock.mcmod.ctnhenergy.common.stats.CEStats;
import tech.luckyblock.mcmod.ctnhenergy.common.me.strategy.EUContainerItemStrategy;
import tech.luckyblock.mcmod.ctnhenergy.data.CEDatagen;
import tech.luckyblock.mcmod.ctnhenergy.registry.*;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;
import tech.vixhentx.mcmod.ctnhlib.client.ponder.CTNHPonderLang;

@SuppressWarnings({ "UnstableApiUsage", "removal" })
public class CommonProxy {

    public CommonProxy() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.register(this);
        CEConfig.init();
        CommonProxy.init();
    }

    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        CTNHEnergy.REGISTRATE.registerRegistrate();
        AEMenus.DR.register(eventBus);

        CENetWorking.init();

        CEDatagen.init();
        CECreativeModeTabs.init();
        CEStats.init();
        eventBus.addGenericListener(GTRecipeType.class, CommonProxy::registerRecipeTypes);
        eventBus.addGenericListener(MachineDefinition.class, CommonProxy::registerMachines);

        GenericSlotCapacities.register(EUKeyType.INSTANCE, (long) Integer.MAX_VALUE);
        StorageCells.addCellHandler(EuCellHandler.HANDLER);

        MinecraftForge.EVENT_BUS.addGenericListener(BlockEntity.class, (AttachCapabilitiesEvent<BlockEntity> event) -> {
            var blockEntity = event.getObject();
            event.addCapability(CTNHEnergy.id("generic_eu_wrapper"), new ICapabilityProvider() {

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability,
                                                                  @Nullable Direction direction) {
                    if (capability == GTCapability.CAPABILITY_ENERGY_CONTAINER) {
                        var upgradeable = CEUtil.getUpgradeable(blockEntity, direction);
                        if (upgradeable instanceof IActionHost host) {
                            return LazyOptional.of(() -> new MEMachineEUHandler(host::getActionableNode, upgradeable))
                                    .cast();
                        }
                    }
                    return LazyOptional.empty();
                }
            });
        });

        // JadePriorityManager.registerBlockData(new AdMEPatternBufferProvider(), BlockEntity.class, 2901,
        // "ad_me_pattern_buffer_data");
        // JadePriorityManager.registerBlockData(new AdMEPatternBufferProxyProvider(), BlockEntity.class, 3001,
        // "ad_me_pattern_buffer_proxy_data");
        // JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProvider(), Block.class, 2901,
        // "ad_me_pattern_buffer_component");
        // JadePriorityManager.registerBlockComponent(new AdMEPatternBufferProxyProvider(), Block.class, 3001,
        // "ad_me_pattern_buffer_proxy_component");
    }

    public static void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        CEMachines.init();
        CEMultiblock.init();
    }

    public static void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        CERecipeTypes.init();
    }

    @SubscribeEvent
    public void registerEUKey(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.BLOCK)) {
            AEKeyTypes.register(EUKeyType.INSTANCE);
            AEKeyTypes.register(VoltageKeyType.INSTANCE);
        }
    }

    @SubscribeEvent
    public void initAE2(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GridServices.register(EnergyDistributeService.class, EnergyDistributeService.class);
            ContainerItemStrategy.register(EUKeyType.INSTANCE, EUKey.class, new EUContainerItemStrategy());
            registerCellUpgrades(CEItems.EU_CELL);

            Upgrades.add(AEItems.FUZZY_CARD, AEBlocks.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(AEItems.FUZZY_CARD, AEParts.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(AEItems.FUZZY_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, 1,
                    GuiText.CraftingInterface.getTranslationKey());

            Upgrades.add(CEItems.DYNAMO_CARD, AEBlocks.INTERFACE, 1, GuiText.Interface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, AEParts.INTERFACE, 1, GuiText.Interface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, EPPItemAndBlock.EX_INTERFACE, 1,
                    GuiText.Interface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, EPPItemAndBlock.EX_INTERFACE_PART, 1,
                    GuiText.Interface.getTranslationKey());

            Upgrades.add(CEItems.DYNAMO_CARD, AEBlocks.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, AEParts.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.DYNAMO_CARD, EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, 1,
                    GuiText.CraftingInterface.getTranslationKey());

            Upgrades.add(CEItems.DYNAMO_CARD, AECSParts.SIMPLE_PATTERN_PROVIDER_PART, 1,
                    "block.ae2cs.simple_pattern_provider");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSBlocks.SIMPLE_PATTERN_PROVIDER_BLOCK, 1,
                    "block.ae2cs.simple_pattern_provider");

            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), AEBlocks.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), AEParts.PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), EPPItemAndBlock.EX_PATTERN_PROVIDER, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), EPPItemAndBlock.EX_PATTERN_PROVIDER_PART, 1,
                    GuiText.CraftingInterface.getTranslationKey());
            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), AECSParts.SIMPLE_PATTERN_PROVIDER_PART, 1,
                    "block.ae2cs.simple_pattern_provider");
            Upgrades.add(CEItems.PROGRAMMED_CIRCUIT_CARD.get(), AECSBlocks.SIMPLE_PATTERN_PROVIDER_BLOCK, 1,
                    "block.ae2cs.simple_pattern_provider");

            Upgrades.add(CEItems.DYNAMO_CARD, AECSParts.ENDER_INTERFACE_PART, 1, "block.ae2cs.ender_interface");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSBlocks.ENDER_INTERFACE_BLOCK, 1, "block.ae2cs.ender_interface");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSParts.EX_ENDER_INTERFACE_PART, 1, "block.ae2cs.ender_interface");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSBlocks.EX_INTEGRATED_INTERFACE_BLOCK, 1,
                    "block.ae2cs.ender_interface");

            Upgrades.add(CEItems.DYNAMO_CARD, AECSParts.RESONATING_PATTERN_PROVIDER_PART, 1,
                    "block.ae2cs.resonating_pattern_provider");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSBlocks.RESONATING_PATTERN_PROVIDER_BLOCK, 1,
                    "block.ae2cs.resonating_pattern_provider");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSParts.EX_RESONATING_PATTERN_PROVIDER_PART, 1,
                    "block.ae2cs.resonating_pattern_provider");
            Upgrades.add(CEItems.DYNAMO_CARD, AECSBlocks.EX_RESONATING_PATTERN_PROVIDER_BLOCK, 1,
                    "block.ae2cs.resonating_pattern_provider");

            Upgrades.add(CEItems.MAINTAINING_CARD, AEParts.IMPORT_BUS, 1, GuiText.IOBuses.getTranslationKey());
            Upgrades.add(CEItems.MAINTAINING_CARD, AEParts.EXPORT_BUS, 1, GuiText.IOBuses.getTranslationKey());

            P2PTunnelAttunement.registerAttunementTag(CEItems.EU_P2P);
        });
    }

    @SubscribeEvent
    public void gatherData(GatherDataEvent event) {
        if (event.includeClient()) {
            CTNHPonderLang.init(new CTNHEnergyPonderPlugin());
        }
    }

    private static void registerCellUpgrades(ItemLike... cells) {
        for (var cell : cells) {
            Upgrades.add(AEItems.VOID_CARD, cell, 1, GuiText.StorageCells.getTranslationKey());
        }
    }
}
