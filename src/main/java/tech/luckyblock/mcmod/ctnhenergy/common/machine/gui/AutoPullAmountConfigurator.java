package tech.luckyblock.mcmod.ctnhenergy.common.machine.gui;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.common.data.GTItems;

import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

import net.minecraft.network.chat.Component;

import tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch.MEStokingInputMachine;

public class AutoPullAmountConfigurator implements IFancyConfigurator {

    private final MEStokingInputMachine machine;

    public AutoPullAmountConfigurator(MEStokingInputMachine machine) {
        this.machine = machine;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("gtceu.gui.adv_stocking_config.title");
    }

    @Override
    public IGuiTexture getIcon() {
        return new ItemStackTexture(GTItems.TOOL_DATA_STICK.asStack());
    }

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 90, 70);

        group.addWidget(new LabelWidget(4, 2, AmountSetWidget.min.key()));
        group.addWidget(new IntInputWidget(4, 12, 81, 14, machine::getMinStackSize,
                machine::setMinStackSize));
        group.addWidget(new LabelWidget(4, 36, AmountSetWidget.max.key()));
        group.addWidget(new IntInputWidget(4, 46, 81, 14, machine::getMaxStackSize,
                machine::setMaxStackSize));

        return group;
    }
}
