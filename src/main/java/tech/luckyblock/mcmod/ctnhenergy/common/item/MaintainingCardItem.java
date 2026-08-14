package tech.luckyblock.mcmod.ctnhenergy.common.item;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.utils.FormattingUtil;

import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.ButtonWidget;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.TextFieldWidget;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import appeng.items.materials.UpgradeCardItem;
import com.ctnhlang.CN;
import com.ctnhlang.EN;
import org.jetbrains.annotations.Nullable;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.util.List;

public class MaintainingCardItem extends UpgradeCardItem implements HeldItemUIFactory.IHeldItemUIHolder {

    public static final String MAINTAINING_AMOUNT = "MaintainingAmount";
    public static final long DEFAULT_MAINTAINING_AMOUNT = 64;
    private static final long MIN_MAINTAINING_AMOUNT = 1;

    @CN("库存维持数量")
    @EN("Stocking Amount")
    static Lang stockingAmount;

    @CN("维持卡设置")
    @EN("Maintaining Card Settings")
    static Lang settingsTitle;

    @CN("当前维持数量: %s")
    @EN("Current stocking amount: %s")
    static Lang currentAmountTooltip;

    @CN("手持时右键以设置维持数量")
    @EN("Right-click while held to set the stocking amount")
    static Lang configureTooltip;

    public MaintainingCardItem(Properties properties) {
        super(properties);
    }

    public static long getMaintainingAmount(ItemStack stack) {
        var tag = stack.getTag();
        if (tag == null || !tag.contains(MAINTAINING_AMOUNT)) {
            return DEFAULT_MAINTAINING_AMOUNT;
        }
        return Math.max(MIN_MAINTAINING_AMOUNT, tag.getLong(MAINTAINING_AMOUNT));
    }

    private static void setMaintainingAmount(HeldItemUIFactory.HeldItemHolder holder, long amount) {
        holder.getHeld().getOrCreateTag().putLong(MAINTAINING_AMOUNT, Math.max(MIN_MAINTAINING_AMOUNT, amount));
        holder.markAsDirty();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            HeldItemUIFactory.INSTANCE.openUI(serverPlayer, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public ModularUI createUI(Player player, HeldItemUIFactory.HeldItemHolder holder) {
        return new ModularUI(176, 80, holder, player)
                .background(GuiTextures.BACKGROUND)
                .widget(new LabelWidget(8, 8, settingsTitle.key()))
                .widget(new LabelWidget(8, 29, stockingAmount.key()))
                .widget(new TextFieldWidget(78, 26, 90, 16,
                        () -> String.valueOf(getMaintainingAmount(holder.getHeld())),
                        value -> updateMaintainingAmount(holder, value))
                        .setMaxStringLength(19))
                .widget(createAdjustButton(8, 50, "-64", -64, holder))
                .widget(createAdjustButton(48, 50, "-1", -1, holder))
                .widget(createAdjustButton(88, 50, "+1", 1, holder))
                .widget(createAdjustButton(128, 50, "+64", 64, holder));
    }

    private static ButtonWidget createAdjustButton(int x, int y, String label, long adjustment,
                                                   HeldItemUIFactory.HeldItemHolder holder) {
        return new ButtonWidget(x, y, 36, 16,
                new GuiTextureGroup(GuiTextures.VANILLA_BUTTON, new TextTexture(label)), clickData -> {
                    if (!clickData.isRemote) {
                        setMaintainingAmount(holder, saturatingAdd(getMaintainingAmount(holder.getHeld()), adjustment));
                    }
                });
    }

    private static void updateMaintainingAmount(HeldItemUIFactory.HeldItemHolder holder, String value) {
        try {
            setMaintainingAmount(holder, Long.parseLong(value));
        } catch (NumberFormatException ignored) {
            // Keep the last valid value while the player is editing.
        }
    }

    private static long saturatingAdd(long value, long adjustment) {
        if (adjustment > 0 && value > Long.MAX_VALUE - adjustment) {
            return Long.MAX_VALUE;
        }
        return Math.max(MIN_MAINTAINING_AMOUNT, value + adjustment);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents,
                                TooltipFlag isAdvanced) {
        tooltipComponents.add(currentAmountTooltip.translate(
                FormattingUtil.formatNumbers(getMaintainingAmount(stack))));
        tooltipComponents.add(configureTooltip.translate());
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
