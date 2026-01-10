package tech.luckyblock.mcmod.ctnhenergy.utils;

import it.unimi.dsi.fastutil.ints.AbstractIntList;

public final class FakeSizedIntList extends AbstractIntList {

    private final int size;
    private final int value;

    private FakeSizedIntList(int size, int value) {
        this.size = size;
        this.value = value;
    }

    public static FakeSizedIntList ofSize(int size) {
        return new FakeSizedIntList(size, -1);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public int getInt(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return value;
    }
}

