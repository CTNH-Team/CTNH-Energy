package tech.luckyblock.mcmod.ctnhenergy.network.syncdata;

import com.lowdragmc.lowdraglib.syncdata.payload.ObjectTypedPayload;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import appeng.api.stacks.AEKey;
import org.jetbrains.annotations.Nullable;

public class AEKeyPayLoad extends ObjectTypedPayload<AEKey> {

    @Override
    public @Nullable Tag serializeNBT() {
        if (payload == null) return null;
        return payload.toTagGeneric();
    }

    @Override
    public void deserializeNBT(Tag tag) {
        if (tag instanceof CompoundTag compoundTag) {
            payload = AEKey.fromTagGeneric(compoundTag);
        }
    }

    @Override
    public void writePayload(FriendlyByteBuf buf) {
        if (payload != null) {
            AEKey.writeKey(buf, payload);
        }
    }

    @Override
    public void readPayload(FriendlyByteBuf buf) {
        payload = AEKey.readKey(buf);
    }
}
