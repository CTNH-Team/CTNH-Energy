package tech.luckyblock.mcmod.ctnhenergy.common.machine.energyhatch;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.api.gui.widget.LongInputWidget;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.ArrayUtils;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.Arrays;
import java.util.List;

@Prefix("ui")
public class MEEnergyInputConfigurator implements IFancyUIProvider {

    IGridConnectedMachine machine;
    MEEnergyPartMachine.MEEnergyContainer energyContainer;

    public MEEnergyInputConfigurator(IGridConnectedMachine machine, MEEnergyPartMachine.MEEnergyContainer energyContainer){
        this.machine = machine;
        this.energyContainer = energyContainer;
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        WidgetGroup configGroup = new WidgetGroup(0, 0, 100, 80);
        configGroup.addWidgets(
                new LabelWidget(0, 2, () -> machine.isOnline() ? "gtceu.gui.me_network.online" : "gtceu.gui.me_network.offline"),
                new LabelWidget(35, 16, "gtceu.creative.energy.voltage"),
                new SelectorWidget(25, 28, 50, 20, Arrays.stream(GTValues.VNF).limit(15).toList(), -1)
                        .setOnChanged(tier -> {
                            energyContainer.setTier(ArrayUtils.indexOf(GTValues.VNF, tier));
                        })
                        .setSupplier(() -> GTValues.VNF[energyContainer.getTier()])
                        .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                        .setBackground(ColorPattern.BLACK.rectTexture())
                        .setValue(GTValues.VNF[energyContainer.getTier()])
                        .setIsUp(true),
                new LabelWidget(35, 52, "gtceu.creative.energy.amperage"),
                new LongInputWidget(0, 64, 100, 20, energyContainer::getInputAmperage, energyContainer::setInputAmperage)
                        .setMax(64L)
        );
        return configGroup;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(CEMachines.ENERGY_INPUT_HATCH_ME.getItem());
    }

    @EN("ME Energy Config")
    @CN("ME能源设置")
    static Lang energyInput;
    @Override
    public Component getTitle() {
        return energyInput.translate();
    }

    @EN("ME Energy Input")
    @CN("ME能源输入")
    static Lang energyInputTooltip;
    @Override
    public List<Component> getTabTooltips() {
        return List.of(energyInputTooltip.translate());
    }
}
