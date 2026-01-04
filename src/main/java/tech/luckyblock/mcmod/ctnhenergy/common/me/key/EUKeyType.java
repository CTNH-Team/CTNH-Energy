package tech.luckyblock.mcmod.ctnhenergy.common.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.CEConfig;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

public class EUKeyType extends AEKeyType {
    private EUKeyType() {
        super(CTNHEnergy.id("eu"), EUKey.class, EUKey.EU_NAME);
    }

    public static EUKeyType INSTANCE = new EUKeyType();

    @Override
    public @Nullable AEKey readFromPacket(FriendlyByteBuf input) {
        return EUKey.EU;
    }

    @Override
    public @Nullable AEKey loadKeyFromTag(CompoundTag tag) {
        return EUKey.EU;
    }

    @Override
    public int getAmountPerByte() {
        return CEConfig.INSTANCE.appeu.amountPerByte;
    }

    @Override
    public int getAmountPerOperation() {
        return Integer.MAX_VALUE;
    }

    @Override
    public @Nullable String getUnitSymbol() {
        return "EU";
    }
}
