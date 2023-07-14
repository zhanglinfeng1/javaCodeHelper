package pers.zlf.plugin.marker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;

import java.util.Collection;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/10 16:35
 */
public abstract class BaseLineMarkerProvider<T> extends RelatedItemLineMarkerProvider {

    public Collection<? super RelatedItemLineMarkerInfo<?>> result;
    public T element;

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (checkPsiElement(element) && element.isWritable()) {
            this.result = result;
            this.element = (T) element;
            dealPsiElement();
        }
    }

    /**
     * 校验元素
     *
     * @param element element
     * @return boolean
     */
    public abstract boolean checkPsiElement(PsiElement element);

    /**
     * 处理元素
     */
    public abstract void dealPsiElement();

    public void addLineMarker(PsiElement targets, PsiElement element) {
        result.add(NavigationGutterIconBuilder.create(Icon.LOGO).setTargets(targets).setTooltipText(Common.BLANK_STRING).createLineMarkerInfo(element));
    }

    public void addLineMarkerBoth(PsiElement targets, PsiElement element) {
        addLineMarker(targets, element);
        addLineMarker(element, targets);
    }
}
