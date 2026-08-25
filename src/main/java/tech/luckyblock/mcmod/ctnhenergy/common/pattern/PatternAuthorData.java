package tech.luckyblock.mcmod.ctnhenergy.common.pattern;

import com.ctnhlang.CN;
import com.ctnhlang.EN;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import tech.vixhentx.mcmod.ctnhlib.langprovider.Lang;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class PatternAuthorData {

    private static final String DISPLAY = "display";
    private static final String LORE = "Lore";
    private static final String AUTHOR = "ctnhenergy_author";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @CN("由 %s 在 %s 编码")
    @EN("Encoded by %s at %s")
    static Lang patternEncodedBy;

    private PatternAuthorData() {}

    public static void addAuthorLore(ItemStack stack, String playerName) {
        if (stack.isEmpty() || playerName == null || playerName.isBlank()) {
            return;
        }
        var rootTag = stack.getOrCreateTag();
        if (rootTag.contains(AUTHOR, Tag.TAG_STRING)) {
            return;
        }
        rootTag.putString(AUTHOR, playerName);

        var encodedTime = ZonedDateTime.now(ZoneId.systemDefault()).format(TIME_FORMATTER);
        var display = stack.getOrCreateTagElement(DISPLAY);
        if (!display.contains(LORE, Tag.TAG_LIST)) {
            display.put(LORE, new ListTag());
        }
        var lore = display.getList(LORE, Tag.TAG_STRING);
        Component authorLine = patternEncodedBy.translate(playerName, encodedTime)
                .withStyle(ChatFormatting.DARK_GRAY)
                .withStyle(style -> style.withItalic(false));
        lore.add(StringTag.valueOf(Component.Serializer.toJson(authorLine)));
    }
}
