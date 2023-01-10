package lineMarker;

import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/10 16:35
 */
public abstract class AbstractLineMarkerProvider<T> extends RelatedItemLineMarkerProvider {

    @Override
    protected void collectNavigationMarkers(@NotNull PsiElement element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result) {
        if (checkPsiElement(element)) {
            addLineMarker((T) element, result);
        }

    }

    public abstract boolean checkPsiElement(PsiElement element);

    public abstract void addLineMarker(T element, @NotNull Collection<? super RelatedItemLineMarkerInfo<?>> result);
}
