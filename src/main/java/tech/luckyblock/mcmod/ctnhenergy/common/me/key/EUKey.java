package tech.luckyblock.mcmod.ctnhenergy.common.me.key;

import appeng.api.stacks.AEKey;
import appeng.api.stacks.AEKeyType;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy;

import java.util.List;

public class EUKey extends AEKey {

    private EUKey() {}

    public static EUKey EU = new EUKey();
    public static Component EU_NAME = Component.literal("EU").withStyle(ChatFormatting.YELLOW);

    @Override
    public AEKeyType getType() {
        return EUKeyType.INSTANCE;
    }

    @Override
    public AEKey dropSecondary() {
        return this;
    }

    @Override
    public CompoundTag toTag() {
        return new CompoundTag();
    }

    @Override
    public Object getPrimaryKey() {
        return EURecipeCapability.CAP;
    }

    @Override
    public ResourceLocation getId() {
        return CTNHEnergy.id("eu");
    }

    @Override
    public void writeToPacket(FriendlyByteBuf data) {
    }

    @Override
    protected Component computeDisplayName() {
        //TODO: change by network tier
        return EU_NAME;
    }

    @Override
    public void addDrops(long amount, List<ItemStack> drops, Level level, BlockPos pos) {}

    @Override
    public String toString() {
        return "EUKey{}";
    }
}
