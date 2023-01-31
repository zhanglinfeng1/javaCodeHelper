package pers.zlf.plugin.util.lambda;

import pers.zlf.plugin.util.StringUtil;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/30 14:17
 */
public final class Empty<T> {
    private final T value;

    private Empty(T value) {
        this.value = StringUtil.isEmpty(value) ? null : value;
    }

    public static <T> Empty<T> of(T value) {
        return new Empty<>(value);
    }

    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public <X extends Throwable> T ifEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }
}
