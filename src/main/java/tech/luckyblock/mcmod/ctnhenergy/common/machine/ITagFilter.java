package tech.luckyblock.mcmod.ctnhenergy.common.machine;

public interface ITagFilter {
    String getWhiteList();
    void setWhiteList(String whiteList);
    String getBlackList();
    void setBlackList(String blackList);
}
