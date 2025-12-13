package tech.luckyblock.mcmod.ctnhenergy.common.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

public class VoltageKeyType extends AEKeyType {
    private VoltageKeyType() {
        super(CTNHEnergy.id("voltage"), VoltageKey.class, Component.literal("Voltage"));
    }

    public static VoltageKeyType INSTANCE = new VoltageKeyType();

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf input) {
        return VoltageKey.of(input.readVarInt());
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag tag) {
        return VoltageKey.of(tag.getInt("tier"));
    }

    @Override
    public int getAmountPerByte() {
        return 0;
    }

}
