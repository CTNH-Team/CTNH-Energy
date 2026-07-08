package tech.luckyblock.mcmod.ctnhenergy.common.machine.utils;

import com.lowdragmc.lowdraglib.syncdata.IContentChangeAware;
import com.lowdragmc.lowdraglib.syncdata.ITagSerializable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import lombok.Getter;
import lombok.Setter;
import lombok.With;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Multi-slot GenericStack storage handler, similar to ItemStackHandler.
 */
public class GenericStackHandler implements ITagSerializable<CompoundTag>, IContentChangeAware {

    private static final String NBT_ENTRIES = "Entries";
    private static final String NBT_SLOT = "Slot";
    private static final String NBT_SIZE = "Size";

    protected Entry[] entries;

    @Getter
    @Setter
    private Runnable onContentsChanged = () -> {};

    public GenericStackHandler(int size) {
        entries = new Entry[size];
    }

    public int getSlots() {
        return entries.length;
    }

    public @Nullable GenericStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        Entry entry = entries[slot];
        return entry == null ? null : entry.stack();
    }

    public @Nullable AEKey getKeyInSlot(int slot) {
        validateSlotIndex(slot);
        Entry entry = entries[slot];
        return entry == null ? null : entry.key();
    }

    public int getMinAmount(int slot) {
        validateSlotIndex(slot);
        Entry entry = entries[slot];
        return entry == null ? 0 : entry.minAmount();
    }

    public int getMaxAmount(int slot) {
        validateSlotIndex(slot);
        Entry entry = entries[slot];
        return entry == null ? 0 : entry.maxAmount();
    }

    public @Nullable Entry getEntry(int slot) {
        validateSlotIndex(slot);
        return entries[slot];
    }

    public void setEntry(int slot, @Nullable Entry entry) {
        validateSlotIndex(slot);
        entries[slot] = entry;
        onContentsChanged(slot);
    }

    public void setStackInSlot(int slot, @Nullable GenericStack stack) {
        validateSlotIndex(slot);
        if (entries[slot] != null) {
            entries[slot] = entries[slot].withStack(stack);
            onContentsChanged(slot);
        }
    }

    public void setKeyInSlot(int slot, @Nullable AEKey key) {
        validateSlotIndex(slot);
        if (key == null) {
            entries[slot] = null;
        } else {
            entries[slot] = new Entry(key, 0, 0, null);
        }
    }

    public void setMinAmount(int slot, int amount) {
        validateSlotIndex(slot);
        if (entries[slot] != null) {
            entries[slot] = entries[slot].withMinAmount(amount);
        }
    }

    public void setMaxAmount(int slot, int amount) {
        validateSlotIndex(slot);
        if (entries[slot] != null) {
            entries[slot] = entries[slot].withMaxAmount(amount);
        }
    }

    public void updateStacks(KeyCounter keyCounter) {
        Runnable original = onContentsChanged;
        MutableBoolean changed = new MutableBoolean(false);
        onContentsChanged = changed::setTrue;
        for (int slot = 0; slot < entries.length; slot++) {
            var entry = entries[slot];
            if (entry != null) {
                long storage = keyCounter.get(entry.key);
                if (storage != 0 && storage >= entry.minAmount) {
                    int maxAmount = entry.maxAmount == 0 ? Integer.MAX_VALUE : entry.maxAmount;
                    var stack = new GenericStack(entry.key, Math.min(storage, maxAmount));
                    setStackInSlot(slot, stack);
                } else {
                    setStackInSlot(slot, null);
                }
            }
        }
        onContentsChanged = original;
        if (changed.booleanValue()) {
            onContentsChanged.run();
        }
    }

    /**
     * Refresh configuration with Top-K keys (by amount) from the ME storage.
     * Only keys matching the filter are considered.
     *
     * @param source ME storage source
     * @param filter key filter
     */
    public void autoPull(KeyCounter source, int maxStackSize, Filter filter) {
        int slots = getSlots();
        if (slots == 0) return;

        // Top-K via PriorityQueue: keep the highest 'slotCount' entries by amount
        var topEntries = new PriorityQueue<>(Comparator.comparingLong(Object2LongMap.Entry<AEKey>::getLongValue));

        for (var entry : source) {
            AEKey key = entry.getKey();
            long amount = entry.getLongValue();

            if (!filter.test(key, amount)) {
                continue;
            }

            assert topEntries.peek() != null;
            if (topEntries.size() < slots) {
                // If the heap is not full, add the entry
                topEntries.offer(entry);
            } else if (topEntries.peek().getLongValue() < amount) {
                // If the heap is full but the current entry has a higher amount, replace the smallest entry
                topEntries.poll();
                topEntries.offer(entry);
            }
        }

        // Suppress listener; batch updates with a single notification
        Runnable original = onContentsChanged;
        MutableBoolean changed = new MutableBoolean(false);
        onContentsChanged = changed::setTrue;
        // Fill slots from highest to lowest
        for (int i = slots - 1; i >= 0; i--) {
            // Pad with null if no entry available
            if (i >= topEntries.size()) {
                setEntry(i, null);
                continue;
            }
            var entry = topEntries.poll();
            var stack = new GenericStack(entry.getKey(), Math.min(entry.getLongValue(), maxStackSize));
            setEntry(i, new Entry(entry.getKey(), 0, 0, stack));
        }
        // Restore listener and emit once if changed
        onContentsChanged = original;
        if (changed.booleanValue()) {
            onContentsChanged.run();
        }
    }

    public void clear() {
        entries = new Entry[entries.length];
    }

    public GenericStackHandler copy() {
        GenericStackHandler copy = new GenericStackHandler(entries.length);
        System.arraycopy(entries, 0, copy.entries, 0, entries.length);
        return copy;
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= entries.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + entries.length + ")");
    }

    protected void onContentsChanged(int slot) {
        onContentsChanged.run();
    }

    public boolean hasKey(AEKey aeKey) {
        return Arrays.stream(entries).anyMatch(e -> e != null && e.key.equals(aeKey));
    }

    @Override
    public CompoundTag serializeNBT() {
        var nbt = new CompoundTag();
        var entriesTag = new ListTag();
        for (int i = 0; i < entries.length; i++) {
            var entry = entries[i];
            if (entry != null) {
                var tag = entry.serializeNBT();
                tag.putInt(NBT_SLOT, i);
                entriesTag.add(tag);
            }
        }
        nbt.put(NBT_ENTRIES, entriesTag);
        nbt.putInt(NBT_SIZE, entries.length);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        int size = nbt.contains(NBT_SIZE, Tag.TAG_INT) ? nbt.getInt(NBT_SIZE) : entries.length;
        entries = new Entry[size];

        var entriesTag = nbt.getList(NBT_ENTRIES, Tag.TAG_COMPOUND);
        for (int i = 0; i < entriesTag.size(); i++) {
            var tag = entriesTag.getCompound(i);

            int slot = tag.getInt(NBT_SLOT);
            if (slot < 0 || slot >= entries.length) {
                continue;
            }

            entries[slot] = Entry.deserializeNBT(tag);
        }
    }

    @With
    public record Entry(@NotNull AEKey key, int minAmount, int maxAmount, @Nullable GenericStack stack) {

        private static final String NBT_KEY = "Key";
        private static final String NBT_MIN_AMOUNT = "MinAmount";
        private static final String NBT_MAX_AMOUNT = "MaxAmount";
        private static final String NBT_STACK = "Stack";

        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.put(NBT_KEY, key.toTagGeneric());
            if (minAmount != 0) tag.putInt(NBT_MIN_AMOUNT, minAmount);
            if (maxAmount != 0) tag.putInt(NBT_MAX_AMOUNT, maxAmount);
            if (stack != null) tag.put(NBT_STACK, GenericStack.writeTag(stack));
            return tag;
        }

        public static Entry deserializeNBT(CompoundTag tag) {
            var key = AEKey.fromTagGeneric(tag.getCompound(NBT_KEY));
            if (key == null) return null;
            int minAmount = tag.contains(NBT_MIN_AMOUNT, Tag.TAG_INT) ? tag.getInt(NBT_MIN_AMOUNT) : 0;
            int maxAmount = tag.contains(NBT_MAX_AMOUNT, Tag.TAG_INT) ? tag.getInt(NBT_MAX_AMOUNT) : 0;
            var stack = tag.contains(NBT_STACK, Tag.TAG_COMPOUND) ? GenericStack.readTag(tag.getCompound(NBT_STACK)) :
                    null;
            return new Entry(key, minAmount, maxAmount, stack);
        }
    }

    public interface Filter {

        boolean test(AEKey key, long amount);
    }
}
