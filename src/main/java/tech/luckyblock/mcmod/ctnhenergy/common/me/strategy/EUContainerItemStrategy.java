package tech.luckyblock.mcmod.ctnhenergy.common.me.strategy;

import appeng.api.behaviors.ContainerItemStrategy;
import appeng.api.config.Actionable;
import appeng.api.stacks.GenericStack;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IElectricItem;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.api.EUItemContext;
import tech.luckyblock.mcmod.ctnhenergy.common.me.key.EUKey;
import tech.luckyblock.mcmod.ctnhenergy.common.me.strategy.context.CarriedContextEU;
import tech.luckyblock.mcmod.ctnhenergy.common.me.strategy.context.PlayerInvContextEU;


@SuppressWarnings("UnstableApiUsage")
public class EUContainerItemStrategy implements ContainerItemStrategy<EUKey, EUItemContext> {

    /* --------------------------------------------
     * 读取物品当前包含的 EU
     * -------------------------------------------- */
    @Override
    public @Nullable GenericStack getContainedStack(ItemStack stack) {
        IElectricItem electric = GTCapabilityHelper.getElectricItem(stack);
        if (electric != null) {
            long charge = electric.getCharge();
            if (charge > 0) {
                return new GenericStack(EUKey.EU, charge);
            }
        }
        return null;
    }

    /* --------------------------------------------
     * Context 查找（鼠标携带）
     * -------------------------------------------- */
    @Override
    public @Nullable EUItemContext findCarriedContext(Player player, AbstractContainerMenu menu) {

        if (GTCapabilityHelper.getElectricItem(menu.getCarried()) != null) {
            return new CarriedContextEU(player, menu);
        }
        return null;
    }

    /* --------------------------------------------
     * Context 查找（玩家背包槽位）
     * -------------------------------------------- */
    @Override
    public @Nullable EUItemContext findPlayerSlotContext(Player player, int slot) {

        ItemStack stack = player.getInventory().getItem(slot);
        if (GTCapabilityHelper.getElectricItem(stack) != null) {
            return new PlayerInvContextEU(player, slot);
        }
        return null;
    }

    /* --------------------------------------------
     * 从物品中“抽取” EU（放电）
     * -------------------------------------------- */
    @Override
    public long extract(
            EUItemContext context,
            EUKey what,
            long amount,
            Actionable mode
    ) {
        ItemStack stack = context.getStack();
        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);

        IElectricItem electric = GTCapabilityHelper.getElectricItem(copy);
        if (electric == null || !electric.canProvideChargeExternally()) {
            return 0;
        }

        long extracted = electric.discharge(
                amount,
                context.getTier(),
                true,                   // ignore transfer limit（由 GT 内部控制）
                true,                   // externally = true
                mode.isSimulate()
        );

        if (mode == Actionable.MODULATE && extracted > 0) {
            stack.shrink(1);
            context.addOverflow(copy);
        }

        return extracted;
    }

    /* --------------------------------------------
     * 向物品中“注入” EU（充电）
     * -------------------------------------------- */
    @Override
    public long insert(
            EUItemContext context,
            EUKey what,
            long amount,
            Actionable mode
    ) {
        ItemStack stack = context.getStack();
        ItemStack copy = ItemHandlerHelper.copyStackWithSize(stack, 1);

        IElectricItem electric = GTCapabilityHelper.getElectricItem(copy);
        if (electric != null && electric.chargeable()) {
            long filled = electric.charge(
                    amount,
                    context.getTier(),
                    true,                   // ignore transfer limit
                    mode.isSimulate()
            );

            if (mode == Actionable.MODULATE && filled > 0) {
                stack.shrink(1);
                context.addOverflow(copy);
            }

            return filled;
        }

        var feCap = GTCapabilityHelper.getForgeEnergyItem(copy);
        if (feCap != null && feCap.canReceive()) {

            long insertedEu = FeCompat.insertEu(
                    feCap,
                    amount,
                    mode.isSimulate()
            );

            if (mode == Actionable.MODULATE && insertedEu > 0) {
                stack.shrink(1);
                context.addOverflow(copy);
            }
            return insertedEu;
        }

        return 0;

    }

    /* --------------------------------------------
     * 音效（EU 默认无）
     * -------------------------------------------- */
    @Override
    public void playFillSound(Player player, EUKey what) {
        // NO-OP
    }

    @Override
    public void playEmptySound(Player player, EUKey what) {
        // NO-OP
    }

    /* --------------------------------------------
     * 可被抽取的内容（用于 ME 预览）
     * -------------------------------------------- */
    @Override
    public @Nullable GenericStack getExtractableContent(EUItemContext context) {
        long canExtract = extract(context, EUKey.EU, Long.MAX_VALUE, Actionable.SIMULATE);
        if (canExtract > 0) {
            return new GenericStack(EUKey.EU, canExtract);
        }
        return null;
    }
}
