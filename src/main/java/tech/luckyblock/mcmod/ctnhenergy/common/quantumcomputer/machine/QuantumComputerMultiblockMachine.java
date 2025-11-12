package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationReceiver;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableComputationContainer;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

import static tech.luckyblock.mcmod.ctnhenergy.CTNHEnergy.REGISTRATE;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@Prefix("machine")
public class QuantumComputerMultiblockMachine extends WorkableElectricMultiblockMachine implements IOpticalComputationReceiver {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            QuantumComputerMultiblockMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    private int storageKilobyte = 0;

    @Getter
    @Persisted
    private int coprocessing = 0;
    private IOpticalComputationProvider computationProvider;
    private final int COMPUTATION2COPROCESSING = 4;

    private WorkStatus workStatus;
//    private int tickDelay = 0;
    @Nullable
    protected TickableSubscription tickSubs;

    public QuantumComputerMultiblockMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    private QuantumComputerMENetworkPortBlockEntity meNetworkPortBlockEntity;

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        List<IEnergyContainer> energyContainers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            part.self().holder.self()
                    .getCapability(GTCapability.CAPABILITY_COMPUTATION_PROVIDER)
                    .ifPresent(provider -> this.computationProvider = provider);

            part.self().holder.self()
                    .getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER)
                    .ifPresent(energyContainers::add);

        }
        this.energyContainer = new EnergyContainerList(energyContainers);
        if (computationProvider == null || !findMENetworkBlockEntity()) {
            onStructureInvalid();
        }
        this.coprocessing = 0;
        this.storageKilobyte = getMultiblockState().getMatchContext().getOrPut("StorageKb", 0);
        this.workStatus = WorkStatus.WORKING;

        onLoad();
    }

    private boolean findMENetworkBlockEntity(){
        int[][] offsets = new int[][]{
                new int[]{0,2,0},
                new int[]{2,2,2},
                new int[]{2,2,-2},
                new int[]{-2,2,2},
                new int[]{-2,2,-2},
                new int[]{2,4,0},
                new int[]{-2,4,0},
                new int[]{0,4,2},
                new int[]{0,4,-2},
                new int[]{4,2,0},
                new int[]{-4,2,0},
                new int[]{0,2,4},
                new int[]{0,2,-4},
        };
        BlockPos controllerPos = getPos();
        for (int[] offset : offsets) {
            BlockEntity blockEntity = getLevel().getBlockEntity(new BlockPos(controllerPos.getX() + offset[0],
                    controllerPos.getY() + offset[1],
                    controllerPos.getZ() + offset[2]));
            if(blockEntity instanceof QuantumComputerMENetworkPortBlockEntity){
                this.meNetworkPortBlockEntity = (QuantumComputerMENetworkPortBlockEntity) blockEntity;
                this.meNetworkPortBlockEntity.setMachine(this);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.storageKilobyte = 0;
        this.coprocessing = 0;
        this.computationProvider = null;
        this.meNetworkPortBlockEntity.multiBlockBreak();
        this.meNetworkPortBlockEntity = null;
        this.energyContainer = new EnergyContainerList(new ArrayList<>());
        onUnload();
    }


//    @Override
//    public boolean onWorking() {
//        DivineCreation.LOGGER.debug("onWorking()");
//        if (energyContainer.getEnergyStored() >= calculateEnergyUsage()) {
//            energyContainer.changeEnergy(-calculateEnergyUsage());
//
//            return true;
//        }
//        return false;
//    }

//    @Override
//    public List<IMultiPart> getParts() {
//        DivineCreation.LOGGER.debug("getParts()");
//        return super.getParts();
//    }
    @CN({
            "工作状态：",
            "耗电量：",
            "总内存：",
            "空闲内存：",
            "最大算力：",
            "最大可用并行：",
            "当前并行："
    })
    @EN({
            "Work Status: ",
            "Power: ",
            "Total Memory: ",
            "Remaining memory: ",
            "Total Computation: ",
            "Total Co-Processing: ",
            "Current Co-Processing: "
    })
    static Lang[] jiuzhang_tooltip;

    @Override
    public void addDisplayText(List<Component> textList) {
        if (isFormed()) {
            //工作状态：
            if(this.workStatus!=null){
                if(!isWorkingEnabled()){
                    textList.add(jiuzhang_tooltip[0].translate()
                            .append(WorkStatus.SUSPEND.localize));
                }else{
                    textList.add(jiuzhang_tooltip[0].translate()
                            .append(this.workStatus.localize));
                }
            }
            //功率：
            int calculateEnergyUsage = calculateEnergyUsage();
            long maxVoltage = GTValues.V[getEnergyTier()];
            textList.add(jiuzhang_tooltip[1].translate()
                            .append(Component.literal(calculateEnergyUsage + "/" + maxVoltage + " EU/t (" + GTValues.VNF[getEnergyTier()] + ")")
                                    .withStyle(calculateEnergyUsage <= maxVoltage ? ChatFormatting.GREEN : ChatFormatting.DARK_RED)
                            ));
            //总内存：${StorageKilobyte} KB
            textList.add(jiuzhang_tooltip[2].translate()
                    .append(storageKilobyte + " KB"));
            //剩余内存：
            textList.add(jiuzhang_tooltip[3].translate()
                    .append((this.meNetworkPortBlockEntity==null? this.storageKilobyte: this.meNetworkPortBlockEntity.getRemainingStorage()) + " KB"));
            //总算力：
            textList.add(jiuzhang_tooltip[4].translate()
                    .append(getMaxCWUt() + " CWU/t"));
            //最大并行：
            textList.add(jiuzhang_tooltip[5].translate()
                    .append(String.valueOf(getMaxCWUt() / COMPUTATION2COPROCESSING)));
            //当前并行：[-] [+]
            textList.add(jiuzhang_tooltip[6].translate()
                            .append(Component.literal(coprocessing + "")
                                    .withStyle(coprocessing > getMaxCWUt() / COMPUTATION2COPROCESSING ? ChatFormatting.DARK_RED : ChatFormatting.WHITE))
                    .append(" ")
                    .append(ComponentPanelWidget.withButton(Component.literal("[-]"), "sub"))
                    .append(" ")
                    .append(ComponentPanelWidget.withButton(Component.literal("[+]"), "add")));
        }else{
            super.addDisplayText(textList);
        }
    }

    public int getEnergyTier() {
        var energyContainer = this.getCapabilitiesFlat(IO.IN, EURecipeCapability.CAP);
        if (energyContainer == null) return this.tier;
        var energyCont = new EnergyContainerList(energyContainer.stream().filter(IEnergyContainer.class::isInstance)
                .map(IEnergyContainer.class::cast).toList());

        return Math.min(this.tier + 1, Math.max(this.tier, GTUtil.getFloorTierByVoltage(energyCont.getInputVoltage())));
    }

    public int getMaxCWUt() {
        var provider = getComputationProvider();
        if (provider == null) return 0;
        return provider.getMaxCWUt();
    }

    @Nullable
    @Override
    public IOpticalComputationProvider getComputationProvider() {
        if (computationProvider instanceof NotifiableComputationContainer notifiableContainer) {
            return notifiableContainer.getComputationProvider();
        } else if (computationProvider instanceof MachineTrait trait) {
            /*代码来自 NotifiableComputationContainer::getComputationProvider()*/
            for (Direction direction : GTUtil.DIRECTIONS) {
                BlockEntity blockEntity = trait.getMachine().getLevel().getBlockEntity(trait.getMachine().getPos().relative(direction));
                if (blockEntity == null) continue;
                // noinspection DataFlowIssue can be null just fine.
                IOpticalComputationProvider provider = blockEntity.getCapability(GTCapability.CAPABILITY_COMPUTATION_PROVIDER, direction.getOpposite()).orElse(null);
                // noinspection ConstantValue can be null because above.
                if (provider != null && provider != this) {
                    return provider;
                }
            }
        }
        return null;
    }

    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
//            DivineCreation.LOGGER.debug("handleDisplayClick({})", componentData);
            this.coprocessing = Mth.clamp(this.coprocessing + (componentData.equals("add") ? 1 : -1), 0, computationProvider.getMaxCWUt() / COMPUTATION2COPROCESSING);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (getLevel() instanceof ServerLevel serverLevel) {
            serverLevel.getServer().tell(new TickTask(0, this::updateTickSubscription));
        }
        this.workStatus = WorkStatus.WORKING;
    }

    @Override
    public void onUnload() {
        super.onUnload();
        if (tickSubs != null) {
            tickSubs.unsubscribe();
            tickSubs = null;
        }
        this.workStatus = WorkStatus.SUSPEND;
    }

    protected void updateTickSubscription() {
        if (isFormed) {
            tickSubs = subscribeServerTick(tickSubs, this::tick);
        } else if (tickSubs != null) {
            tickSubs.unsubscribe();
            tickSubs = null;
        }
    }

    public void tick() {


        if (!isWorkingEnabled()){
            if(this.meNetworkPortBlockEntity!=null) {
                meNetworkPortBlockEntity.suspend();
            }
            return;
        }
        if (!consumeEnergy()) {

            this.workStatus = WorkStatus.NOT_ENOUGH_ENERGY;
            if(this.meNetworkPortBlockEntity!=null){
                meNetworkPortBlockEntity.suspend();
            }

            return;
        }
        if (!consumeComputation()) {

            this.workStatus = WorkStatus.NOT_ENOUGH_COMPUTATION;
            if(this.meNetworkPortBlockEntity!=null) {
                meNetworkPortBlockEntity.suspend();
            }

            return;
        }

        this.workStatus = WorkStatus.WORKING;
        getRecipeLogic().setStatus(RecipeLogic.Status.WORKING);
        this.meNetworkPortBlockEntity.active();

    }

    private boolean consumeEnergy() {
        int energyToConsume = calculateEnergyUsage();

        if (this.energyContainer != null && this.energyContainer.getEnergyStored() >= energyToConsume) {
            long consumed = this.energyContainer.removeEnergy(energyToConsume);
            if (consumed == energyToConsume) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private int calculateEnergyUsage() {
        return GTValues.VA[GTValues.LV] * storageKilobyte + GTValues.VA[GTValues.EV] * coprocessing;
    }

    private boolean consumeComputation() {
        if (checkComputation(true)) {
            if (checkComputation(false)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    private boolean checkComputation(boolean simulate) {
        int requestComputation = coprocessing * COMPUTATION2COPROCESSING;
        if (requestComputation <= 0) return true;
        int requestedCWUt = getComputationProvider().requestCWUt(requestComputation, simulate);
        return requestedCWUt >= requestComputation;
    }


    public enum WorkStatus{
        WORKING(REGISTRATE.addLang("machine", "work_status_working", "Working", "运行中")),
        NOT_ENOUGH_ENERGY(REGISTRATE.addLang("machine", "work_status_not_enough_energy", "Not Enough Energy","能量不足")),
        NOT_ENOUGH_COMPUTATION(REGISTRATE.addLang("machine", "work_status_not_enough_computation", "Not Enough Computation","算力不足")),
        SUSPEND(REGISTRATE.addLang("machine", "work_status_suspend", "Suspend","暂停"));

        public Component localize;

        WorkStatus(Component localize) {
            this.localize = localize;
        }
    }
}
