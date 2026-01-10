package tech.luckyblock.mcmod.ctnhenergy.common.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.gregtechceu.gtceu.api.GTValues;
import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

import java.util.List;

public class VoltageKey extends AEKey {
    @Getter
    private final int tier;

    private static final VoltageKey[] V = new VoltageKey[15];

    public static final VoltageKey ULV = register(0);
    public static final VoltageKey LV  = register(1);
    public static final VoltageKey MV  = register(2);
    public static final VoltageKey HV  = register(3);
    public static final VoltageKey EV  = register(4);
    public static final VoltageKey IV  = register(5);
    public static final VoltageKey LuV = register(6);
    public static final VoltageKey ZPM = register(7);
    public static final VoltageKey UV  = register(8);
    public static final VoltageKey UHV = register(9);
    public static final VoltageKey UEV = register(10);
    public static final VoltageKey UIV = register(11);
    public static final VoltageKey UXV = register(12);
    public static final VoltageKey OpV = register(13);
    public static final VoltageKey MAX = register(14);

    private static VoltageKey register(int tier) {
        VoltageKey key = new VoltageKey(tier);
        V[tier] = key;
        return key;
    }


    private VoltageKey(int t){
        tier = t;
    }

    public static VoltageKey of(int tier) {
        if (tier < 0 || tier >= V.length) {
            throw new IllegalArgumentException("Tier out of range: " + tier);
        }
        return V[tier];
    }

    @Override
    public AEKeyType getType() {
        return null;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("tier", tier);
        return tag;
    }

    @Override
    public Object getPrimaryKey() {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return CTNHEnergy.id("voltage");
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
        data.writeVarInt(tier);
    }

    @Override
    protected Component computeDisplayName() {
        return Component.literal(GTValues.VNF[tier]);
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {
    }
}
