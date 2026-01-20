package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import com.gregtechceu.gtceu.api.gui.fancy.FancyMachineUIWidget;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyUIProvider;
import com.gregtechceu.gtceu.integration.ae2.machine.MEBusPartMachine;
import com.gregtechceu.gtceu.integration.ae2.machine.feature.IGridConnectedMachine;
import com.gregtechceu.gtceu.integration.ae2.utils.KeyStorage;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import net.minecraft.network.chat.Component;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import java.util.List;

@Prefix("ui")
public class MEDualOutputConfigurator implements IFancyUIProvider {

    IGridConnectedMachine machine;
    private final KeyStorage internalBuffer;

    public MEDualOutputConfigurator(IGridConnectedMachine machine, KeyStorage buffer) {
        this.machine = machine;
        internalBuffer = buffer;
    }

    @Override
    public Widget createMainPage(FancyMachineUIWidget widget) {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 65);
        // ME Network status
        group.addWidget(new LabelWidget(5, 0, () -> machine.isOnline() ?
                "gtceu.gui.me_network.online" :
                "gtceu.gui.me_network.offline"));
        group.addWidget(new LabelWidget(5, 10, "gtceu.gui.waiting_list"));
        // display list
        group.addWidget(new MEDualOutputHatchPartMachine.Generic(5, 20, 3, this.internalBuffer));

        return group;
    }

    @Override
    public IGuiTexture getTabIcon() {
        return new ItemStackTexture(CEMachines.DUAL_OUTPUT_HATCH_ME.getItem());
    }

    @EN("ME Output")
    @CN("ME输出")
    static Lang dualOutput;
    @Override
    public Component getTitle() {
        return dualOutput.translate();
    }

    @EN("ME Output Catch")
    @CN("ME输出缓存")
    static Lang dualOutputTooltip;

    @Override
    public List<Component> getTabTooltips() {
        return List.of(
                dualOutputTooltip.translate()
        );
    }
}
