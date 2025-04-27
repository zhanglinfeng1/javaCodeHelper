package pers.zlf.plugin.highlight;

import com.intellij.ide.highlighter.JavaFileHighlighter;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.pom.java.LanguageLevel;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.MyIcon;
import pers.zlf.plugin.pojo.brackets.AngleBracket;
import pers.zlf.plugin.pojo.brackets.BaseBrackets;
import pers.zlf.plugin.pojo.brackets.Brace;
import pers.zlf.plugin.pojo.brackets.Bracket;
import pers.zlf.plugin.pojo.brackets.Parenth;

import javax.swing.Icon;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/20 17:32
 */
public class BracketsColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTOR_ARR = new AttributesDescriptor[28];

    private static final Map<String, TextAttributesKey> TAG_MAP = new HashMap<>();

    private static final StringBuilder DEMO_TEXT = new StringBuilder();

    public BracketsColorSettingsPage() {
        BiConsumer<BaseBrackets, Integer> consumer = (brackets, length) -> {
            StringBuilder frontText = new StringBuilder();
            StringBuilder afterText = new StringBuilder();
            int num;
            for (int i = length - 7; i < length; i++) {
                num = i % 7 + 1;
                TextAttributesKey colorKey = TextAttributesKey.createTextAttributesKey(brackets.getExternalName() + num);
                DESCRIPTOR_ARR[i] = new AttributesDescriptor(brackets.getDisplayName() + num, colorKey);
                String tagName = brackets.getTagName() + num;
                TAG_MAP.put(tagName, colorKey);
                frontText.append(String.format(Common.COLOR_TAG_STR, tagName, brackets.getLBrackets(), tagName));
                afterText.insert(0, String.format(Common.COLOR_TAG_STR, tagName, brackets.getRBrackets(), tagName));
            }
            DEMO_TEXT.append(frontText).append(afterText).append(Common.LINE_BREAK);
        };
        consumer.accept(new AngleBracket(), 7);
        consumer.accept(new Parenth(), 14);
        consumer.accept(new Bracket(), 21);
        consumer.accept(new Brace(), 28);
    }

    @Override
    public @Nullable Icon getIcon() {
        return MyIcon.LOGO;
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new JavaFileHighlighter(LanguageLevel.HIGHEST);
    }

    @Override
    public @NonNls @NotNull String getDemoText() {
        return DEMO_TEXT.toString();
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return TAG_MAP;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTOR_ARR;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return Common.RAINBOW_BRACKET;
    }

}