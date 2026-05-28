package tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.standard;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.IDataStickInteractable;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeHandlerList;

import com.lowdragmc.lowdraglib.gui.modular.ModularUI;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.Tag;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.machine.patternbuffer.ProgrammableProxySlotRecipeHandler;
import tech.luckyblock.mcmod.ctnhenergy.registry.CEMachines;

import java.lang.ref.WeakReference;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MEPatternBufferProxyPartMachine extends TieredIOPartMachine
                                             implements IMachineLife, IDataStickInteractable {

    @Getter
    protected final ProgrammableProxySlotRecipeHandler proxySlotRecipeHandler;

    @Persisted
    @Getter
    @DescSynced
    private @Nullable BlockPos bufferPos;

    private @NotNull WeakReference<MEPatternBufferPartMachine> buffer = new WeakReference<>(null);

    public MEPatternBufferProxyPartMachine(IMachineBlockEntity holder) {
        this(holder, GTValues.LuV, MEPatternBufferPartMachine.MAX_PATTERN_COUNT);
    }

    public MEPatternBufferProxyPartMachine(IMachineBlockEntity holder, int tier, int maxPatternCount) {
        super(holder, tier, IO.IN);
        proxySlotRecipeHandler = new ProgrammableProxySlotRecipeHandler(this, maxPatternCount);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel level) {
            level.getServer().tell(new TickTask(0, () -> this.setBuffer(bufferPos)));
        }
    }

    @Override
    public List<RecipeHandlerList> getRecipeHandlers() {
        return proxySlotRecipeHandler.getProxySlotHandlers();
    }

    public void setBuffer(@Nullable BlockPos pos) {
        var level = getLevel();
        if (level == null || pos == null) {
            buffer = new WeakReference<>(null);
        } else
            if (MetaMachine.getMachine(level, pos) instanceof MEPatternBufferPartMachine machine && isBuffer(machine)) {
                bufferPos = pos;
                buffer = new WeakReference<>(machine);
                machine.addProxy(this);
                if (!isRemote()) updateProxy(machine);
            } else {
                buffer = new WeakReference<>(null);
            }
    }

    public void updateProxy(MEPatternBufferPartMachine machine) {
        proxySlotRecipeHandler.updateProxy(machine);
    }

    public boolean isBuffer(MetaMachine machine) {
        return machine.getDefinition() == CEMachines.ME_PATTERN_BUFFER;
    }

    @Nullable
    public MEPatternBufferPartMachine getBuffer() {
        if (buffer.get() == null || buffer.get().getHolder().self().isRemoved()) {
            setBuffer(bufferPos);
        }
        return buffer.get();
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return getBuffer() != null;
    }

    @Override
    public ModularUI createUI(Player entityPlayer) {
        assert getBuffer() != null; // UI should never be able to be opened when buffer is null
        return getBuffer().createUI(entityPlayer);
    }

    @Override
    public void onMachineRemoved() {
        var buf = getBuffer();
        if (buf != null) {
            buf.removeProxy(this);
            clearProxy();
        }
    }

    public void clearProxy() {
        proxySlotRecipeHandler.clearProxy();
    }

    @Override
    public InteractionResult onDataStickUse(Player player, ItemStack dataStick) {
        if (dataStick.hasTag()) {
            assert dataStick.getTag() != null;
            if (dataStick.getTag().contains("pos", Tag.TAG_INT_ARRAY)) {
                var posArray = dataStick.getOrCreateTag().getIntArray("pos");
                var bufferPos = new BlockPos(posArray[0], posArray[1], posArray[2]);
                setBuffer(bufferPos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
