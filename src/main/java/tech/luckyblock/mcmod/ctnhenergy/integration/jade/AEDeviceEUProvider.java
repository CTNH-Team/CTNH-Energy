package tech.luckyblock.mcmod.ctnhenergy.integration.jade;

import com.gregtechceu.gtceu.api.GTValues;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import appeng.api.networking.IGrid;
import appeng.api.networking.security.IActionHost;
import appeng.api.parts.IPartHost;
import appeng.api.upgrades.IUpgradeableObject;
import appeng.blockentity.crafting.PatternProviderBlockEntity;
import appeng.blockentity.misc.InterfaceBlockEntity;
import appeng.helpers.InterfaceLogicHost;
import appeng.helpers.patternprovider.PatternProviderLogic;
import appeng.helpers.patternprovider.PatternProviderLogicHost;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;
import tech.luckyblock.mcmod.ctnhenergy.common.item.DynamoCardItem;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEItems;
import tech.luckyblock.mcmod.ctnhenergy.utils.CEUtil;

public class AEDeviceEUProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    private static final String HAS_DATA = "ce_has_data";
    private static final String NETWORK_TIER = "ce_network_tier";
    private static final String DYNAMO_TIER = "ce_dynamo_tier";

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        var data = accessor.getServerData();
        if (!data.getBoolean(HAS_DATA)) return;

        int networkTier = data.getInt(NETWORK_TIER);
        if (isValidTier(networkTier)) {
            tooltip.add(Component.translatable("ctnhenergy.jade.ae_eu.network_voltage",
                    formatTier(networkTier)));
        }

        int dynamoTier = data.getInt(DYNAMO_TIER);
        if (isValidTier(dynamoTier)) {
            tooltip.add(Component.translatable("ctnhenergy.jade.ae_eu.dynamo_voltage",
                    formatTier(dynamoTier))
                    .withStyle(ChatFormatting.YELLOW));
        }
    }

    @Override
    public void appendServerData(CompoundTag tag, BlockAccessor accessor) {
        var target = findTarget(accessor);
        if (target == null) return;

        int networkTier = CEUtil.getGridTier(target.grid());
        int dynamoTier = getDynamoTier(target.upgradeable());
        if (!isValidTier(networkTier) && !isValidTier(dynamoTier)) return;

        tag.putBoolean(HAS_DATA, true);
        tag.putInt(NETWORK_TIER, networkTier);
        tag.putInt(DYNAMO_TIER, dynamoTier);
    }

    @Override
    public ResourceLocation getUid() {
        return CTNHEnergy.id("ae_eu_info");
    }

    private static @Nullable Target findTarget(BlockAccessor accessor) {
        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity instanceof InterfaceBlockEntity interfaceBlock) {
            return fromInterfaceHost(interfaceBlock);
        }
        if (blockEntity instanceof PatternProviderBlockEntity patternProviderBlock) {
            return fromPatternProviderHost(patternProviderBlock);
        }
        if (blockEntity instanceof IPartHost partHost) {
            var selected = partHost.selectPartWorld(accessor.getHitResult().getLocation());
            if (selected.part instanceof InterfaceLogicHost interfaceHost) {
                return fromInterfaceHost(interfaceHost);
            }
            if (selected.part instanceof PatternProviderLogicHost patternProviderHost) {
                return fromPatternProviderHost(patternProviderHost);
            }
        }
        return null;
    }

    private static Target fromInterfaceHost(InterfaceLogicHost host) {
        IGrid grid = host instanceof IActionHost actionHost && actionHost.getActionableNode() != null ?
                actionHost.getActionableNode().getGrid() : null;
        return new Target(grid, host);
    }

    private static Target fromPatternProviderHost(PatternProviderLogicHost host) {
        PatternProviderLogic logic = host.getLogic();
        return new Target(logic.getGrid(), logic instanceof IUpgradeableObject upgradeable ? upgradeable : null);
    }

    private static int getDynamoTier(@Nullable IUpgradeableObject upgradeable) {
        if (upgradeable == null || upgradeable.getUpgrades() == null) return -1;

        int tier = -1;
        for (ItemStack stack : upgradeable.getUpgrades()) {
            if (!stack.is(CEItems.DYNAMO_CARD.asItem()) || !stack.hasTag()) continue;

            var tag = stack.getTag();
            if (tag != null && tag.contains(DynamoCardItem.VOLTAGE)) {
                int cardTier = tag.getInt(DynamoCardItem.VOLTAGE);
                if (isValidTier(cardTier)) {
                    tier = cardTier;
                }
            }
        }
        return tier;
    }

    private static Component formatTier(int tier) {
        return Component.literal(GTValues.VNF[tier]).withStyle(ChatFormatting.GOLD);
    }

    private static boolean isValidTier(int tier) {
        return tier >= GTValues.ULV && tier <= GTValues.MAX;
    }

    private record Target(@Nullable IGrid grid, @Nullable IUpgradeableObject upgradeable) {}
}
