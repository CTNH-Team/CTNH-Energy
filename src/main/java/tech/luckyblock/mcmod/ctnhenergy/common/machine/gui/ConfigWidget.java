package tech.luckyblock.mcmod.ctnhenergy.common.machine.gui;

import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.Position;
import com.lowdragmc.lowdraglib.utils.Size;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.utils.GenericStackHandler;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ConfigWidget extends WidgetGroup {

    protected static final int UPDATE_ID = 1000;

    protected final GenericStackHandler config;
    protected GenericStackHandler cached;
    private boolean init = false;
    protected final Int2ObjectMap<GenericStackHandler.Entry> changeMap = new Int2ObjectOpenHashMap<>();
    protected AmountSetWidget amountSetWidget;

    private static final int SLOTS_PER_ROW = 8;
    private static final int SLOT_SIZE = 18;
    private static final int ROW_SPACING = 2;
    private final Predicate<AEKey> keyPredicate;

    private final Supplier<Boolean> autoPullProvider;

    public ConfigWidget(int x, int y, GenericStackHandler handler, Predicate<AEKey> predicate,
                        Supplier<Boolean> autoPull) {
        super(new Position(x, y), new Size(handler.getSlots() / 2 * 18, 18 * 4 + 2));
        config = handler;
        keyPredicate = predicate;
        autoPullProvider = autoPull;
        cached = new GenericStackHandler(config.getSlots());
        for (int index = 0; index < this.config.getSlots(); index++) {
            int line = index / SLOTS_PER_ROW;
            addWidget(new AEConfigSlotWidget((index - line * SLOTS_PER_ROW) * SLOT_SIZE,
                    line * (SLOT_SIZE * 2 + ROW_SPACING), this, index));
        }
        amountSetWidget = new AmountSetWidget(16, -50, this);
        addWidget(amountSetWidget);
        disableAmount();
    }

    public boolean isAutoPull() {
        return autoPullProvider.get();
    }

    public boolean isKeyValid(AEKey key) {
        return !config.hasKey(key) && keyPredicate.test(key);
    };

    @OnlyIn(Dist.CLIENT)
    public void enableAmountClient(int slotIndex) {
        amountSetWidget.setSlotIndexClient(slotIndex);
        setAmountVisible(true);
    }

    @OnlyIn(Dist.CLIENT)
    public void disableAmountClient() {
        amountSetWidget.setSlotIndexClient(-1);
        setAmountVisible(false);
    }

    public void enableAmount(int slotIndex) {
        amountSetWidget.setSlotIndex(slotIndex);
        setAmountVisible(true);
    }

    public void disableAmount() {
        amountSetWidget.setSlotIndex(-1);
        setAmountVisible(false);
    }

    private void setAmountVisible(boolean visible) {
        amountSetWidget.setVisible(visible);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (amountSetWidget.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        for (Widget widget : widgets) {
            if (widget instanceof AEConfigSlotWidget slot) {
                slot.setSelect(false);
            }
        }
        disableAmountClient();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        changeMap.clear();
        for (int index = 0; index < config.getSlots(); index++) {
            GenericStackHandler.Entry newEntry = config.getEntry(index);
            GenericStackHandler.Entry oldEntry = cached.getEntry(index);
            if (!init || !areEntriesEqual(newEntry, oldEntry)) {
                changeMap.put(index, newEntry);
                cached.setEntry(index, newEntry);
                gui.holder.markAsDirty();
            }
        }
        init = true;
        if (!changeMap.isEmpty()) {
            writeUpdateInfo(UPDATE_ID, buf -> {
                buf.writeVarInt(changeMap.size());
                for (int index : changeMap.keySet()) {
                    buf.writeVarInt(index);
                    writeEntry(buf, changeMap.get(index));
                }
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void readUpdateInfo(int id, FriendlyByteBuf buffer) {
        super.readUpdateInfo(id, buffer);
        if (id == UPDATE_ID) {
            int size = buffer.readVarInt();
            for (int i = 0; i < size; i++) {
                int index = buffer.readVarInt();
                config.setEntry(index, readEntry(buffer));
            }
        }
    }

    @Override
    public void handleClientAction(int id, FriendlyByteBuf buffer) {
        super.handleClientAction(id, buffer);
    }

    public final GenericStackHandler getConfigHandler() {
        return config;
    }

    public final @Nullable GenericStackHandler.Entry getEntry(int index) {
        return config.getEntry(index);
    }

    public final @Nullable AEKey getKey(int index) {
        return config.getKeyInSlot(index);
    }

    public final void setKey(int index, @Nullable AEKey key) {
        config.setKeyInSlot(index, key);
    }

    public final @Nullable GenericStack getStack(int index) {
        return config.getStackInSlot(index);
    }

    public final int getMinAmount(int index) {
        return config.getMinAmount(index);
    }

    public final int getMaxAmount(int index) {
        return config.getMaxAmount(index);
    }

    public final void setMinAmount(int index, int amount) {
        config.setMinAmount(index, amount);
    }

    public final void setMaxAmount(int index, int amount) {
        config.setMaxAmount(index, amount);
    }

    protected boolean areEntriesEqual(@Nullable GenericStackHandler.Entry first,
                                      @Nullable GenericStackHandler.Entry second) {
        if (first == second) return true;
        if (first == null || second == null) return false;
        return first.minAmount() == second.minAmount() &&
                first.maxAmount() == second.maxAmount() &&
                Objects.equals(first.key(), second.key()) &&
                areAEStackCountsEqual(first.stack(), second.stack());
    }

    protected boolean areAEStackCountsEqual(@Nullable GenericStack first, @Nullable GenericStack second) {
        if (first == second) return true;
        if (first != null && second != null) {
            return first.amount() == second.amount() && first.what().matches(second);
        }
        return false;
    }

    private static void writeEntry(FriendlyByteBuf buf, @Nullable GenericStackHandler.Entry entry) {
        buf.writeNbt(entry == null ? null : entry.serializeNBT());
    }

    private static @Nullable GenericStackHandler.Entry readEntry(FriendlyByteBuf buf) {
        var tag = buf.readNbt();
        if (tag == null) return null;
        return GenericStackHandler.Entry.deserializeNBT(tag);
    }
}
