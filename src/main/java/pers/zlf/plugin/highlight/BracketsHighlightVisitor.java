package pers.zlf.plugin.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.JavaHighlightInfoTypes;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportHolder;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;

import java.util.Stack;
import java.util.function.BiFunction;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/19 13:37
 */
public class BracketsHighlightVisitor extends JavaElementVisitor implements HighlightVisitor {
    /** () */
    private final Stack<TextAttributesKey> parenthColorStack = new Stack<>();
    /** [] */
    private final Stack<TextAttributesKey> bracketColorStack = new Stack<>();
    /** {} */
    private final Stack<TextAttributesKey> braceColorStack = new Stack<>();
    /** <> */
    private final Stack<TextAttributesKey> angleBracketColorStack = new Stack<>();

    private HighlightInfoHolder myHolder;

    @NotNull
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public BracketsHighlightVisitor clone() {
        return new BracketsHighlightVisitor();
    }

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        return file instanceof PsiImportHolder && !InjectedLanguageManager.getInstance(file.getProject()).isInjectedFragment(file);
    }

    @Override
    public void visit(@NotNull PsiElement element) {
        element.accept(this);
    }

    @Override
    public boolean analyze(@NotNull PsiFile file, boolean updateWholeFile, @NotNull HighlightInfoHolder holder, @NotNull Runnable highlight) {
        try {
            myHolder = holder;
            highlight.run();
        } finally {
            myHolder = null;
        }
        return true;
    }

    @Override
    public void visitJavaToken(@NotNull PsiJavaToken token) {
        IElementType elementType = token.getNode().getElementType();
        TextAttributesKey color;
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();
        BiFunction<Stack<TextAttributesKey>, String, TextAttributesKey> function = (colorStack, key) -> {
            int length = colorStack.size();
            if (length > 7) {
                length = length % 7;
            }
            TextAttributesKey colorKey = TextAttributesKey.createTextAttributesKey(key + length);
            colorStack.push(colorKey);
            return colorKey;
        };
        if (elementType == JavaTokenType.LBRACKET && config.isOpenBracket()) {
            color = function.apply(bracketColorStack, Common.BRACKET_COLOR);
        } else if (elementType == JavaTokenType.LPARENTH && config.isOpenParenth()) {
            color = function.apply(parenthColorStack, Common.PARENTH_COLOR);
        } else if (elementType == JavaTokenType.LBRACE && config.isOpenBrace()) {
            color = function.apply(braceColorStack, Common.BRACE_COLOR);
        } else if (elementType == JavaTokenType.GT && token.getParent() instanceof PsiReferenceParameterList && config.isOpenAngleBracket()) {
            color = function.apply(angleBracketColorStack, Common.ANGLE_BRACKET_COLOR);
        } else if (elementType == JavaTokenType.LT && token.getParent() instanceof PsiReferenceParameterList && config.isOpenAngleBracket()) {
            color = angleBracketColorStack.pop();
        } else if (elementType == JavaTokenType.RBRACKET && config.isOpenBracket()) {
            color = bracketColorStack.pop();
        } else if (elementType == JavaTokenType.RPARENTH && config.isOpenParenth()) {
            color = parenthColorStack.pop();
        } else if (elementType == JavaTokenType.RBRACE && config.isOpenBrace()) {
            color = braceColorStack.pop();
        } else {
            return;
        }
        HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(JavaHighlightInfoTypes.JAVA_KEYWORD).range(token.getTextRange());
        builder.textAttributes(color);
        myHolder.add(builder.createUnconditionally());
    }
}
