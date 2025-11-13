package tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.machine;

import appeng.menu.MenuOpener;
import appeng.menu.locator.MenuLocators;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationProvider;
import com.gregtechceu.gtceu.api.capability.IOpticalComputationReceiver;
import com.gregtechceu.gtceu.api.capability.forge.GTCapability;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.fancyconfigurator.ButtonConfigurator;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiPart;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.misc.EnergyContainerList;
import com.gregtechceu.gtceu.utils.GTUtil;
import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.util.ClickData;
import com.lowdragmc.lowdraglib.gui.widget.ComponentPanelWidget;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSets;
import lombok.Getter;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import tech.luckyblock.mcmod.ctnhenergy.common.block.QuantumComputerCasingBlock;
import tech.luckyblock.mcmod.ctnhenergy.common.quantumcomputer.port.QuantumComputerMENetworkPortBlockEntity;
import tech.luckyblock.mcmod.ctnhenergy.registry.AEMenus;
import tech.luckyblock.mcmod.ctnhenergy.registry.CERecipeTypes;
import tech.luckyblock.mcmod.ctnhenergy.utils.button.CETextures;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.CN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.annotation.Prefix;

import javax.annotation.ParametersAreNonnullByDefault;
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

    @Getter
    private IOpticalComputationProvider computationContainer;
    private final int COMPUTATION2COPROCESSING = 1;

    protected LongSet qcCasings;

    private WorkStatus workStatus;

    Player player;
//    private int tickDelay = 0;

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
        //List<IEnergyContainer> energyContainers = new ArrayList<>();
        for (IMultiPart part : getParts()) {
            part.self().holder.self()
                    .getCapability(GTCapability.CAPABILITY_COMPUTATION_PROVIDER)
                    .ifPresent(provider -> this.computationContainer = provider);

//            part.self().holder.self()
//                    .getCapability(GTCapability.CAPABILITY_ENERGY_CONTAINER)
//                    .ifPresent(energyContainers::add);

        }
        //this.energyContainer = new EnergyContainerList(energyContainers);
        if (computationContainer == null || !findMENetworkBlockEntity()) {
            onStructureInvalid();
        }

        this.storageKilobyte = getMultiblockState().getMatchContext().getOrPut("StorageKb", 0);
        this.workStatus = WorkStatus.SUSPEND;
        qcCasings = getMultiblockState().getMatchContext().getOrDefault("qcCasings", LongSets.emptySet());
        //onLoad();
    }

    private boolean findMENetworkBlockEntity(){
        int[][] offsets = new int[][]{
                new int[]{6,6,0},
                new int[]{0,6,6},
                new int[]{-6,6,0},
                new int[]{0,6,-6}
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
        this.computationContainer = null;

        if(meNetworkPortBlockEntity != null)
        {
            this.meNetworkPortBlockEntity.multiBlockBreak();
            this.meNetworkPortBlockEntity = null;
        }
        updateCasings(QuantumComputerCasingBlock.State.GREY);
        qcCasings = null;
        //onUnload();
    }


    @CN({
            "工作状态：",
            "耗电量：",
            "总内存：",
            "空闲内存：",
            "最大算力：",
            "最大可用并行：",
            "当前并行：",
            "按住shift可翻倍/减半"
    })
    @EN({
            "Work Status: ",
            "Power: ",
            "Total Memory: ",
            "Remaining memory: ",
            "Total Computation: ",
            "Total Co-Processing: ",
            "Current Co-Processing: ",
            "Hold Shift to double/halve."
    })
    static Lang[] jiuzhang_tooltip;

    @Override
    public void addDisplayText(List<Component> textList) {
        if(isRemote()) return;
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
            int energyTier = GTUtil.getFloorTierByVoltage(energyContainer.getInputVoltage());
            long maxVoltage = GTValues.V[energyTier];
            textList.add(jiuzhang_tooltip[1].translate()
                            .append(Component.literal(calculateEnergyUsage + "/" + maxVoltage + " EU/t (" + GTValues.VNF[energyTier] + ")")
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
                    .append(String.valueOf(getMaxCoProcessing())));
            //当前并行：[-] [+]
            textList.add(jiuzhang_tooltip[6].translate().withStyle(
                    style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, jiuzhang_tooltip[7].translate()))
                    )
                    .append(ComponentPanelWidget.withButton(Component.literal("[-]"), "sub"))
                    .append(" ")
                    .append(Component.literal(coprocessing + "")
                            .withStyle(coprocessing > getMaxCoProcessing() ? ChatFormatting.DARK_RED : ChatFormatting.WHITE))
                    .append(" ")
                    .append(ComponentPanelWidget.withButton(Component.literal("[+]"), "add")));
        }else{
            super.addDisplayText(textList);
        }
    }

    @CN("打开合成界面")
    @EN("Open Crafting UI")
    static Lang craft_ui;

    @Override
    public void attachConfigurators(ConfiguratorPanel configuratorPanel) {

        configuratorPanel.attachConfigurators(new ButtonConfigurator(
                new GuiTextureGroup(GuiTextures.BUTTON, CETextures.CRAFT), this::openCPU)
                .setTooltips(List.of(craft_ui.translate())));
        super.attachConfigurators(configuratorPanel);

    }

    @Override
    public InteractionResult tryToOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        this.player = player;
        return super.tryToOpenUI(player, hand, hit);
    }

    private void openCPU(ClickData clickData){
        if(!isRemote())
            MenuOpener.open(AEMenus.QUANTUM_COMPUTER.get(), player, MenuLocators.forBlockEntity(meNetworkPortBlockEntity));
    }

    public int getMaxCWUt() {
        return computationContainer!= null? computationContainer.getMaxCWUt() : 0;
    }

    public int getMaxCoProcessing() {
        return computationContainer.getMaxCWUt() / COMPUTATION2COPROCESSING;
    }

    public void handleDisplayClick(String componentData, ClickData clickData) {
        if (!clickData.isRemote) {
            if (clickData.isShiftClick)
                this.coprocessing = Mth.clamp((int)(this.coprocessing * (componentData.equals("add") ? 2 : 0.5)), 0, getMaxCoProcessing());
            else
                this.coprocessing = Mth.clamp(this.coprocessing + (componentData.equals("add") ? 1 : -1), 0, getMaxCoProcessing());
        }
    }

    public void setWorkStatus(WorkStatus newStatus) {
        if(workStatus != newStatus)
        {
            workStatus = newStatus;
            if(workStatus == WorkStatus.SUSPEND)
                updateCasings(QuantumComputerCasingBlock.State.GREY);
            if(workStatus == WorkStatus.WORKING)
                updateCasings(QuantumComputerCasingBlock.State.BLUE);
            if(workStatus == WorkStatus.NOT_ENOUGH_COMPUTATION)
                updateCasings(QuantumComputerCasingBlock.State.PURPLE);
            if(workStatus == WorkStatus.NOT_ENOUGH_ENERGY)
                updateCasings(QuantumComputerCasingBlock.State.RED);
        }
    }

    public void updateCasings(QuantumComputerCasingBlock.State state){
        if(qcCasings != null){
            for(long pos : qcCasings){
                var blockPos = BlockPos.of(pos);
                var blockState = getLevel().getBlockState(blockPos);
                if (blockState.hasProperty(QuantumComputerCasingBlock.STATE)) {
                    var newState = blockState.setValue(QuantumComputerCasingBlock.STATE, state);
                    if (newState != blockState) {
                        getLevel().setBlock(blockPos, newState, Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE);
                    }
                }
            }
        }
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

    @Override
    public IOpticalComputationProvider getComputationProvider() {
        return computationContainer;
    }

    @Override
    protected RecipeLogic createRecipeLogic(Object... args) {
        return new QCRecipeLogic(this);
    }

    @Override
    public QCRecipeLogic getRecipeLogic() {
        return (QCRecipeLogic)recipeLogic;
    }

    public class QCRecipeLogic extends RecipeLogic{

        public QCRecipeLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public void serverTick() {
            if(getRecipeType() == CERecipeTypes.QUANTUM_COMPUTER)
            {
                if (isIdle()){
                    setWorkStatus(WorkStatus.SUSPEND);
                    if(meNetworkPortBlockEntity!=null) {
                        meNetworkPortBlockEntity.suspend();
                    }
                    return;
                }
                if (!consumeEnergy()) {
                    setWorkStatus(WorkStatus.NOT_ENOUGH_ENERGY);
                    if(meNetworkPortBlockEntity!=null){
                        meNetworkPortBlockEntity.suspend();
                    }
                    return;
                }
                if (!consumeComputation()) {
                    setWorkStatus(WorkStatus.NOT_ENOUGH_COMPUTATION);
                    if(meNetworkPortBlockEntity!=null) {
                        meNetworkPortBlockEntity.suspend();
                    }
                    return;
                }
                setWorkStatus(WorkStatus.WORKING);
                meNetworkPortBlockEntity.active();
            }
            else
                super.serverTick();
        }

        @Override
        public void setWorkingEnabled(boolean workingEnabled) {
            if(getRecipeType() == CERecipeTypes.QUANTUM_COMPUTER)
                setStatus(workingEnabled ? Status.WORKING : Status.IDLE);
            else
                super.setWorkingEnabled(workingEnabled);
        }

        @Override
        public boolean isWorkingEnabled() {
            if(getRecipeType() == CERecipeTypes.QUANTUM_COMPUTER)
                return isWorking();
            return super.isWorkingEnabled();
        }
    }

    @CN({
            "运行中",
            "能量不足",
            "算力不足",
            "暂停"
    })
    @EN({
            "Working",
            "Not Enough Energy",
            "Not Enough Computation",
            "Suspend"
    })
    static Lang[] work_status;


    public enum WorkStatus{
        WORKING(work_status[0].translate()),
        NOT_ENOUGH_ENERGY(work_status[1].translate()),
        NOT_ENOUGH_COMPUTATION(work_status[2].translate()),
        SUSPEND(work_status[3].translate());

        public Component localize;

        WorkStatus(Component localize) {
            this.localize = localize;
        }
    }
}
