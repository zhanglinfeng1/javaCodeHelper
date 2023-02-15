package pers.zlf.plugin.util.lambda;

import pers.zlf.plugin.util.StringUtil;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public <U> Empty<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return new Empty<>(null);
        } else {
            return Empty.of(mapper.apply(value));
        }
    }

    public boolean isPresent() {
        return value != null;
    }

    public void isPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    public void isPresent(Runnable runnable) {
        if (value != null) {
            runnable.run();
        }
    }

    public <X extends Throwable> T ifEmptyThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value == null) {
            throw exceptionSupplier.get();
        }
        return value;
    }
}
