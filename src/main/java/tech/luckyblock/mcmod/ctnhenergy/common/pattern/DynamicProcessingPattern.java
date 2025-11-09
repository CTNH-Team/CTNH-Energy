package tech.luckyblock.mcmod.ctnhenergy.common.pattern;

import appeng.api.crafting.IPatternDetails;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.GenericStack;
import appeng.api.stacks.KeyCounter;
import appeng.crafting.pattern.AEProcessingPattern;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import yuuki1293.pccard.wrapper.IAEPattern;

import static com.mojang.text2speech.Narrator.LOGGER;

/**
 * 支持动态倍数扩展的处理型 Pattern。
 * 可通过 multiplyInPlace(long) 方法原地修改输入输出数量。
 */
public class DynamicProcessingPattern implements IPatternDetails, IAEPattern {

    private final AEItemKey definition;
    private final GenericStack[] sparseInputs, sparseOutputs;
    private final Input[] inputs;
    private final GenericStack[] condensedOutputs;
    private int pCCard$number = 0;

    public DynamicProcessingPattern(AEProcessingPattern pattern) {
        this.definition = pattern.getDefinition();
        this.sparseInputs = pattern.getSparseInputs().clone();
        this.sparseOutputs = pattern.getSparseOutputs().clone();
        this.inputs = new Input[pattern.getInputs().length];
        for (int i = 0; i < inputs.length; ++i) {
            inputs[i] = new Input(pattern.getInputs()[i]);
        }
        this.condensedOutputs = pattern.getOutputs().clone();
        if(pattern instanceof IAEPattern iaePattern)
            pCCard$setNumber(iaePattern.pCCard$getNumber());
    }

    @Override
    public int hashCode() {
        return definition.hashCode();
    }

    @Override
    public AEItemKey getDefinition() {
        return definition;
    }

    @Override
    public IInput[] getInputs() {
        return inputs;
    }

    @Override
    public GenericStack[] getOutputs() {
        return condensedOutputs;
    }

    public GenericStack[] getSparseInputs() {
        return sparseInputs;
    }

    public GenericStack[] getSparseOutputs() {
        return sparseOutputs;
    }

    @Override
    public void pushInputsToExternalInventory(KeyCounter[] inputHolder, PatternInputSink inputSink) {
        if (sparseInputs.length == inputs.length) {
            IPatternDetails.super.pushInputsToExternalInventory(inputHolder, inputSink);
            return;
        }

        var allInputs = new KeyCounter();
        for (var counter : inputHolder) {
            allInputs.addAll(counter);
        }

        for (var sparseInput : sparseInputs) {
            if (sparseInput == null) continue;

            var key = sparseInput.what();
            var amount = sparseInput.amount();
            long available = allInputs.get(key);

            if (available < amount) {
                throw new RuntimeException("Expected at least %d of %s when pushing pattern, but only %d available"
                        .formatted(amount, key, available));
            }

            inputSink.pushInput(key, amount);
            allInputs.remove(key, amount);
        }
    }

    // ===========================================================
    // ✅ 新增功能：就地倍乘（不创建新对象）
    // ===========================================================

    /**
     * 将当前 DynamicProcessingPattern 的所有输入输出数量乘以指定倍数。
     * 该方法会直接修改当前实例。
     *
     * @param factor 倍数（必须 > 0）
     */
    public DynamicProcessingPattern multiplyInPlace(long factor) {
        if (factor <= 0) {
            //throw new IllegalArgumentException("factor must be > 0");
            LOGGER.debug("样板倍数小于0！");
            factor = 1;
        }

        scaleStacksInPlace(this.sparseInputs, factor);
        scaleStacksInPlace(this.sparseOutputs, factor);
        scaleStacksInPlace(this.condensedOutputs, factor);

        for (Input input : this.inputs) {
            input.multiplyInPlace(factor);
        }

        return this;
    }

    /** 工具方法：直接修改数组中的堆叠数量 */
    private static void scaleStacksInPlace(GenericStack[] arr, long factor) {
        if (arr == null) return;
        for (int i = 0; i < arr.length; i++) {
            GenericStack s = arr[i];
            if (s == null) continue;
            arr[i] = new GenericStack(s.what(), s.amount() * factor);
        }
    }

    @Override
    public void pCCard$setNumber(int number) {
        pCCard$number = number;
    }

    @Override
    public int pCCard$getNumber() {
        return pCCard$number;
    }

    // ===========================================================
    // 内部类 Input：支持倍乘
    // ===========================================================

    private static class Input implements IInput {
        private GenericStack[] template;
        private long multiplier;

        private Input(IInput iInput) {
            this.template = iInput.getPossibleInputs().clone();
            this.multiplier = iInput.getMultiplier();
        }

        @Override
        public GenericStack[] getPossibleInputs() {
            return template;
        }

        @Override
        public long getMultiplier() {
            return multiplier;
        }

        @Override
        public boolean isValid(AEKey input, Level level) {
            return input.matches(template[0]);
        }

        @Nullable
        @Override
        public AEKey getRemainingKey(AEKey template) {
            return null;
        }

        /** ✅ 原地倍乘：直接修改数量与倍数 */
        public void multiplyInPlace(long factor) {
            this.multiplier *= factor;
        }
    }
}
