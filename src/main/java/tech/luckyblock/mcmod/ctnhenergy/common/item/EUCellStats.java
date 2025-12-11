package tech.luckyblock.mcmod.ctnhenergy.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.List;

@Prefix("eucell")
public class EUCellStats extends ElectricStats {
    protected EUCellStats(long maxCharge, int tier, boolean chargeable, boolean dischargeable) {
        super(maxCharge, tier, chargeable, dischargeable);
    }

    @CN("释能模式：")
    @EN("Discharge mode: ")
    static Lang discharge_mode;
    @CN("已启用")
    @EN("Enabled")
    static Lang discharge_enable;
    @CN("已禁用")
    @EN("Disabled ")
    static Lang discharge_disable;

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        IElectricItem electricItem = GTCapabilityHelper.getElectricItem(stack);
        if (electricItem == null) return;
        addCurrentChargeTooltip(tooltipComponents, electricItem.getCharge(), electricItem.getMaxCharge(),
                electricItem.getTier(), false);

        if (electricItem.canProvideChargeExternally()) {
            tooltipComponents.add(discharge_mode.translate().append(
                    isInDischargeMode(stack) ?
                            discharge_enable.translate().withStyle(ChatFormatting.GREEN) :
                            discharge_disable.translate().withStyle(ChatFormatting.DARK_RED)
            ));
            tooltipComponents.add(Component.translatable("metaitem.electric.discharge_mode.tooltip"));
        }
    }

    private static boolean isInDischargeMode(ItemStack itemStack) {
        var tagCompound = itemStack.getTag();
        return tagCompound != null && tagCompound.getBoolean("DischargeMode");
    }

    public static EUCellStats createCell(int tier){
        return new EUCellStats(1000000L * (1L << 2 *(tier - GTValues.LV)), tier, true, true);
    }
}
