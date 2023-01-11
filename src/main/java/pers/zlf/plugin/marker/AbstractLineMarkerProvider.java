package pers.zlf.plugin.marker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.COMMON;
import pers.zlf.plugin.constant.ICON;

import java.util.Collection;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/10 16:35
 */
public abstract class AbstractLineMarkerProvider<T> extends RelatedItemLineMarkerProvider {

    public Collection<? super RelatedItemLineMarkerInfo<?>> result;
    public T element;

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (checkPsiElement(element)) {
            this.result = result;
            this.element = (T) element;
            dealPsiElement();
        }
    }

    public abstract boolean checkPsiElement(PsiElement element);

    public abstract void dealPsiElement();

    public void addLineMarker(PsiElement targets, PsiElement element) {
        result.add(NavigationGutterIconBuilder.create(ICON.BO_LUO_SVG_16).setTargets(targets).setTooltipText(COMMON.BLANK_STRING).createLineMarkerInfo(element));
    }

    public void addLineMarkerBoth(PsiElement targets, PsiElement element) {
        addLineMarker(targets, element);
        addLineMarker(element, targets);
    }
}
