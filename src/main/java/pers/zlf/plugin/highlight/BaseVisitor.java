package pers.zlf.plugin.highlight;

import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.codeInsight.daemon.impl.HighlightVisitor;
import com.intellij.codeInsight.daemon.impl.JavaHighlightInfoTypes;
import com.intellij.codeInsight.daemon.impl.analysis.HighlightInfoHolder;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.JavaElementVisitor;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaToken;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/25 18:14
 */
public abstract class BaseVisitor extends JavaElementVisitor implements HighlightVisitor {
    protected HighlightInfoHolder myHolder;

    @NotNull
    @Override
    public abstract BaseVisitor clone();

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

    protected void addHighlightInfo(Stack<TextAttributesKey> colorStack, PsiJavaToken token) {
        if (colorStack.empty()) {
            return;
        }
        TextAttributesKey colorKey = colorStack.pop();
        if (token.getParent().getLastChild() instanceof PsiJavaToken) {
            return;
        }
        addHighlightInfo(colorKey, token);
    }

    protected void addHighlightInfo(TextAttributesKey colorKey, PsiJavaToken token) {
        HighlightInfo.Builder builder = HighlightInfo.newHighlightInfo(JavaHighlightInfoTypes.JAVA_KEYWORD).range(token.getTextRange());
        builder.textAttributes(colorKey);
        myHolder.add(builder.createUnconditionally());
    }
}
