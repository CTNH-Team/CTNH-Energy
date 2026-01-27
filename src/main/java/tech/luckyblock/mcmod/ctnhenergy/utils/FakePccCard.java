package tech.luckyblock.mcmod.ctnhenergy.utils;

import appeng.api.crafting.IPatternDetails;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import yuuki1293.pccard.wrapper.IPatternProviderLogicMixin;

import java.util.List;

public class FakePccCard implements IPatternProviderLogicMixin {
    public static final FakePccCard INSTANCE = new FakePccCard();


    @Override
    public void pCCard$setPCNumber(IPatternDetails iPatternDetails) {

    }

    @Override
    public boolean pCCard$hasPCCard() {
        return true;
    }

    @Override
    public List<BlockPos> pCCard$getSendPos() {
        return List.of();
    }

    @Override
    public Direction pCCard$getSendDirection() {
        return null;
    }

    @Override
    public void pCCard$setSendDirection(Direction direction) {

    }

    @Override
    public BlockEntity pCCard$getBlockEntity() {
        return null;
    }

    @Override
    public Level pCCard$getLevel() {
        return null;
    }
}
