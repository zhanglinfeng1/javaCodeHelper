package pers.zlf.plugin.util.lambda;

import pers.zlf.plugin.util.StringUtil;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/30 14:17
 */
public final class Empty<T> {
    private final T value;

    private Empty(T value) {
        this.value = StringUtil.isEmpty(value) ? null : value;
    }

    public static <T> Empty<T> of(T value) {
        return new Empty<>(value);
    }

    public <U> Empty<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!ifPresent()) {
            return new Empty<>(null);
        } else {
            return Empty.of(mapper.apply(value));
        }
    }

    public boolean ifPresent() {
        return value != null;
    }

    public void ifPresent(Consumer<? super T> action) {
        Optional.ofNullable(value).ifPresent(action);
    }

    public void ifPresent(Runnable runnable) {
        Optional.ofNullable(value).ifPresent(y -> runnable.run());
    }

    public <X extends Throwable> T ifEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return Optional.ofNullable(value).orElseThrow(exceptionSupplier);
    }

    public T orElse(T other) {
        return value != null ? value : other;
    }

    public T get() {
        return value;
    }
}
