package pers.zlf.plugin.marker;

import com.intellij.codeInsight.daemon.GutterIconNavigationHandler;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProviderDescriptor;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;
import pers.zlf.plugin.constant.Common;
import pers.zlf.plugin.constant.Icon;

import java.util.Collection;
import java.util.List;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/10 16:35
 */
public abstract class BaseLineMarkerProvider<T> extends LineMarkerProviderDescriptor {
    /** 处理结果 */
    private Collection<? super LineMarkerInfo<?>> result;
    /** 当前元素 */
    public T currentElement;

    @Override
    public String getName() {
        return null;
    }

    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        for(PsiElement element : elements){
            if (checkPsiElement(element) && element.isWritable()) {
                this.result = result;
                this.currentElement = (T) element;
                dealPsiElement();
            }
        }
    }

    /**
     * 校验元素
     *
     * @param element element
     * @return boolean
     */
    protected abstract boolean checkPsiElement(PsiElement element);

    /**
     * 处理元素
     */
    protected abstract void dealPsiElement();

    /**
     * 添加跳转标识
     *
     * @param element 跳转元素
     * @param targets 跳转目标
     */
    protected void addLineMarker(PsiElement element, PsiElement targets) {
        result.add(NavigationGutterIconBuilder.create(Icon.LOGO).setTargets(targets).setTooltipText(Common.BLANK_STRING).createLineMarkerInfo(element));
    }

    protected void addLineMarker(PsiElement element, List<? extends PsiElement> targets) {
        result.add(NavigationGutterIconBuilder.create(Icon.LOGO).setTargets(targets).setTooltipText(Common.BLANK_STRING).createLineMarkerInfo(element));
    }

    /**
     * 互相添加跳转标识
     *
     * @param element 跳转元素
     * @param targets 跳转目标
     */
    protected void addLineMarkerBoth(PsiElement element, PsiElement targets) {
        addLineMarker(targets, element);
        addLineMarker(element, targets);
    }

    /**
     * 处理图标点击事件
     *
     * @param method  待处理元素
     * @param handler 具体处理方法
     */
    protected void addLineMarkerInfo(PsiMethod method, GutterIconNavigationHandler<PsiMethod> handler) {
        //TODO 兼容2020.1.4及以下版本，后续使用result.add(new RelatedItemLineMarkerInfo<>(method, method.getTextRange(), Icon.LOGO_GREY, null, handler, GutterIconRenderer.Alignment.CENTER, ArrayList::new));
        result.add(new LineMarkerInfo<>(method, method.getTextRange(), Icon.LOGO_GREY, null, handler, GutterIconRenderer.Alignment.CENTER));
    }

}
