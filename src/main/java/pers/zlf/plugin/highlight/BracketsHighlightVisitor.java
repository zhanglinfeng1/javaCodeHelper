package pers.zlf.plugin.highlight;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.JavaTokenType;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiImportHolder;
import com.intellij.psi.PsiJavaToken;
import com.intellij.psi.PsiReferenceParameterList;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.factory.ConfigFactory;
import pers.zlf.plugin.pojo.config.CommonConfig;
import pers.zlf.plugin.util.lambda.TriConsumer;

import java.util.Stack;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/19 13:37
 */
public class BracketsHighlightVisitor extends BaseVisitor {
    /** () */
    private final Stack<TextAttributesKey> parenthColorStack = new Stack<>();
    /** [] */
    private final Stack<TextAttributesKey> bracketColorStack = new Stack<>();
    /** {} */
    private final Stack<TextAttributesKey> braceColorStack = new Stack<>();
    /** <> */
    private final Stack<TextAttributesKey> angleBracketColorStack = new Stack<>();

    @NotNull
    @Override
    public BracketsHighlightVisitor clone() {
        return new BracketsHighlightVisitor();
    }

    @Override
    public boolean suitableForFile(@NotNull PsiFile file) {
        parenthColorStack.clear();
        bracketColorStack.clear();
        braceColorStack.clear();
        angleBracketColorStack.clear();
        return file instanceof PsiImportHolder && !InjectedLanguageManager.getInstance(file.getProject()).isInjectedFragment(file);
    }

    @Override
    public void visitJavaToken(@NotNull PsiJavaToken token) {
        IElementType tokenType = token.getNode().getElementType();
        CommonConfig config = ConfigFactory.getInstance().getCommonConfig();

        TriConsumer<IElementType, Stack<TextAttributesKey>, String> triConsumer = (otherTokenType, colorStack, key) -> {
            int length = colorStack.size() % 7 + 1;
            TextAttributesKey colorKey = TextAttributesKey.createTextAttributesKey(key + length);
            colorStack.add(colorKey);
            addHighlightInfo(colorKey, token);
            if (token.getParent().getLastChild() instanceof PsiJavaToken parentToken && parentToken.getNode().getElementType() == otherTokenType) {
                addHighlightInfo(colorKey, parentToken);
            }
        };

        if (tokenType == JavaTokenType.LBRACKET && config.isOpenBracket()) {
            triConsumer.accept(JavaTokenType.RBRACKET, bracketColorStack, Common.BRACKET_COLOR_KEY);
        } else if (tokenType == JavaTokenType.RBRACKET && config.isOpenBracket()) {
            addHighlightInfo(bracketColorStack, token);
        } else if (tokenType == JavaTokenType.LPARENTH && config.isOpenParenth()) {
            triConsumer.accept(JavaTokenType.RPARENTH, parenthColorStack, Common.PARENTH_COLOR_KEY);
        } else if (tokenType == JavaTokenType.RPARENTH && config.isOpenParenth()) {
            addHighlightInfo(parenthColorStack, token);
        } else if (tokenType == JavaTokenType.LBRACE && config.isOpenBrace()) {
            triConsumer.accept(JavaTokenType.RBRACE, braceColorStack, Common.BRACE_COLOR_KEY);
        } else if (tokenType == JavaTokenType.RBRACE && config.isOpenBrace()) {
            addHighlightInfo(braceColorStack, token);
        } else if (tokenType == JavaTokenType.LT && config.isOpenAngleBracket()) {
            triConsumer.accept(JavaTokenType.GT, angleBracketColorStack, Common.ANGLE_BRACKET_COLOR_KEY);
        } else if (tokenType == JavaTokenType.GT && config.isOpenAngleBracket()) {
            addHighlightInfo(angleBracketColorStack, token);
        }
    }
}