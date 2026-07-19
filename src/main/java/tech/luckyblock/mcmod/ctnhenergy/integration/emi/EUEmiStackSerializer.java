package tech.luckyblock.mcmod.ctnhenergy.integration.emi;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.stack.serializer.EmiStackSerializer;

public final class EUEmiStackSerializer implements EmiStackSerializer<EUEmiStack> {

    public static final EUEmiStackSerializer INSTANCE = new EUEmiStackSerializer();

    private EUEmiStackSerializer() {}

    @Override
    public String getType() {
        return "eu";
    }

    @Override
    public EmiStack create(ResourceLocation id, CompoundTag nbt, long amount) {
        return EUEmiStack.ID.equals(id) ? EUEmiStack.of(amount) : EmiStack.EMPTY;
    }
}
