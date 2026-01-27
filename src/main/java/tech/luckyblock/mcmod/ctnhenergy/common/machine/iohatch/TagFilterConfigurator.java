package tech.luckyblock.mcmod.ctnhenergy.common.machine.iohatch;

import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfigurator;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ItemStackTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.ITagFilter;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

@Prefix("gui")
public class TagFilterConfigurator implements IFancyConfigurator {

    private final ITagFilter machine;

    @EN("Configure Tag Filter")
    @CN("设置标签过滤器")
    static Lang tag_filter;

    public TagFilterConfigurator(ITagFilter machine){
        this.machine = machine;
    }

    @Override
    public Component getTitle() {
        return tag_filter.translate();
    }

    @Override
    public IGuiTexture getIcon() {
        return new ItemStackTexture(Items.NAME_TAG);
    }

    @CN("白名单过滤器")
    static Lang white_list;

    @EN("黑名单过滤器")
    static Lang black_list;

    @Override
    public Widget createConfigurator() {
        var group = new WidgetGroup(0, 0, 176, 70);
        group.addWidget(new LabelWidget(4, 2, white_list.translate()));
        group.addWidget(new TextFieldWidget(4, 12, 160, 14, machine::getWhiteList, machine::setWhiteList));
        group.addWidget(new LabelWidget(4, 36, black_list.translate()));
        group.addWidget(new TextFieldWidget(4, 46, 160, 14, machine::getBlackList, machine::setBlackList));
        return group;
    }
}
