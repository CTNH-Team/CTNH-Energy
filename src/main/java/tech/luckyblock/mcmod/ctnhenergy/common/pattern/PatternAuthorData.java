package tech.luckyblock.mcmod.ctnhenergy.common.pattern;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import com.ctnhlang.CN;
import com.ctnhlang.EN;
import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class PatternAuthorData {

    private static final String DISPLAY = "display";
    private static final String LORE = "Lore";
    private static final String AUTHOR = "ctnhenergy_author";
    private static final String ENCODE_TIME = "ctnhenergy_encode_time";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @CN("由 %s 编码")
    @EN("Encoded by %s")
    static Lang patternEncodedBy;

    @CN("编码时间: %s")
    @EN("Encoded Time: %s")
    static Lang patternEncodedTime;

    private PatternAuthorData() {}

    /**
     * 给样板写入或更新署名人。
     * <p>
     * 重复调用会改写署名与编码时间，而不是保留原作者：修改已有样板时，署名必须改成当前编码者，
     * 同时移除旧的署名行，避免 Lore 里堆叠出多条“由 X 编码”。
     */
    public static void addAuthorLore(ItemStack stack, String playerName) {
        if (stack.isEmpty() || playerName == null || playerName.isBlank()) {
            return;
        }
        var rootTag = stack.getOrCreateTag();
        String previousAuthor = rootTag.getString(AUTHOR);
        rootTag.putString(AUTHOR, playerName);
        rootTag.putLong(ENCODE_TIME, Instant.now().toEpochMilli());

        var display = stack.getOrCreateTagElement(DISPLAY);
        var lore = display.contains(LORE, Tag.TAG_LIST) ? display.getList(LORE, Tag.TAG_STRING) : new ListTag();
        removeAuthorLines(lore, previousAuthor, playerName);
        lore.add(StringTag.valueOf(Component.Serializer.toJson(buildAuthorLine(playerName))));
        display.put(LORE, lore);
    }

    private static Component buildAuthorLine(String playerName) {
        return patternEncodedBy.translate(playerName)
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(style -> style.withItalic(false));
    }

    /** 移除这些署名者留下的署名行；重签同一玩家时也不会重复添加。 */
    private static void removeAuthorLines(ListTag lore, String... authors) {
        for (int i = lore.size() - 1; i >= 0; i--) {
            String plain = plainText(lore.getString(i));
            if (plain.isEmpty()) {
                continue;
            }
            for (String author : authors) {
                if (author != null && !author.isBlank() &&
                        plain.equals(patternEncodedBy.translate(author).getString())) {
                    lore.remove(i);
                    break;
                }
            }
        }
    }

    /** Lore 以组件 JSON 存放，比较前先还原成纯文本。 */
    private static String plainText(String json) {
        try {
            var component = Component.Serializer.fromJson(json);
            return component == null ? "" : component.getString();
        } catch (RuntimeException e) {
            return "";
        }
    }

    public static void addEncodedTimeLine(List<Component> tooltip, ItemStack stack) {
        if (stack.isEmpty() || !stack.hasTag()) {
            return;
        }
        var rootTag = stack.getTag();
        if (!rootTag.contains(ENCODE_TIME, Tag.TAG_LONG)) {
            return;
        }
        var localTime = Instant.ofEpochMilli(rootTag.getLong(ENCODE_TIME))
                .atZone(ZoneId.systemDefault())
                .format(TIME_FORMATTER);
        Component timeLine = patternEncodedTime.translate(localTime)
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(style -> style.withItalic(false));

        String author = rootTag.getString(AUTHOR);
        if (!author.isBlank()) {
            String authorLine = patternEncodedBy.translate(author).getString();
            for (int i = 0; i < tooltip.size(); i++) {
                if (tooltip.get(i).getString().equals(authorLine)) {
                    tooltip.add(i + 1, timeLine);
                    return;
                }
            }
        }
        tooltip.add(timeLine);
    }
}
