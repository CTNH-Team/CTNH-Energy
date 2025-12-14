package tech.luckyblock.mcmod.ctnhenergy.data.lang;

import tech.vixhentx.mcmod.ctnhlib.registrate.lang.RegistrateCNLangProvider;

public class ChineseLangHandler {
    public static void init(RegistrateCNLangProvider provider){
        provider.add("gui.ctnhenergy.blocking_type.title", "阻挡类型");
        provider.add("gui.ctnhenergy.blocking_type.all", "全部");
        provider.add("gui.ctnhenergy.blocking_type.all.details", "容器内存在任何材料时暂停发送。");
        provider.add("gui.ctnhenergy.blocking_type.default", "默认");
        provider.add("gui.ctnhenergy.blocking_type.default.details", "容器内存在任一样板所需的合成材料时暂停发送。");
        provider.add("gui.ctnhenergy.blocking_type.smart", "智能");
        provider.add("gui.ctnhenergy.blocking_type.smart.details.1", "容器内存在当前样板所需的合成材料之外的材料时暂停发送。");
        provider.add("gui.ctnhenergy.blocking_type.smart.details.2", "（忽略编程电路）");

        provider.add("gui.ctnhenergy.enable_circuit", "启用编程电路");
        provider.add("gui.ctnhenergy.enable_circuit.tooltip", "在样板输入中加入编程电路");
        provider.add("gui.ctnhenergy.disable_circuit", "禁用编程电路");
        provider.add("gui.ctnhenergy.disable_circuit.tooltip", "忽略配方中的编程电路");
        provider.add("ctnhenergy.copyright.info", "§6由CTNH Energy添加§r");

        provider.add("config.jade.plugin_ctnhenergy.ad_me_pattern_buffer_proxy", "高级ME样板总成");
        provider.add("config.jade.plugin_ctnhenergy.ad_me_pattern_buffer", "高级ME样板总成镜像");
    }
}
