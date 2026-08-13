package tech.luckyblock.mcmod.ctnhenergy.common.circuit;

import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.item.IntCircuitBehaviour;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

import static tech.luckyblock.mcmod.ctnhenergy.api.ICircuitPattern.NO_CIRCUIT;

/** Serialization and extraction of programmed-circuit metadata from AE2 patterns. */
public final class CircuitPatternData {

    public static final String CIRCUIT = "ctnhenergy_circuit";
    private static final String LEGACY_CIRCUIT = "circuit";

    private CircuitPatternData() {}

    public static int read(CompoundTag tag) {
        if (tag == null) return NO_CIRCUIT;
        if (tag.contains(CIRCUIT, Tag.TAG_INT)) return tag.getInt(CIRCUIT);
        return tag.contains(LEGACY_CIRCUIT, Tag.TAG_INT) ? tag.getInt(LEGACY_CIRCUIT) : NO_CIRCUIT;
    }

    public static void write(CompoundTag tag, int number) {
        if (number == NO_CIRCUIT) tag.remove(CIRCUIT);
        else tag.putInt(CIRCUIT, number);
    }

    public static Optional<Integer> findCircuit(ListTag inputs) {
        for (Tag entry : inputs) {
            if (!(entry instanceof CompoundTag compound) || !compound.contains("id")) continue;
            if (!compound.getString("id").equals(GTItems.PROGRAMMED_CIRCUIT.getId().toString())) continue;
            if (!compound.contains("tag", Tag.TAG_COMPOUND)) return Optional.of(NO_CIRCUIT);
            var stackTag = compound.getCompound("tag");
            if (stackTag.contains("Configuration", Tag.TAG_INT)) {
                return Optional.of(stackTag.getInt("Configuration"));
            }
        }
        return Optional.empty();
    }

    public static void removeCircuit(ListTag inputs) {
        inputs.removeIf(entry -> entry instanceof CompoundTag compound && compound.contains("id") &&
                compound.getString("id").equals(GTItems.PROGRAMMED_CIRCUIT.getId().toString()));
    }

    public static ItemStack stack(int number) {
        var stack = GTItems.PROGRAMMED_CIRCUIT.asStack();
        IntCircuitBehaviour.setCircuitConfiguration(stack, number);
        return stack;
    }
}
