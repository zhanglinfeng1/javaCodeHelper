package pers.zlf.plugin.util.lambda;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/13 14:17
 */
public final class Equals<T> {
    private Boolean run;
    private final T obj;

    private Equals(Boolean run, T obj) {
        this.run = run;
        this.obj = obj;
    }

    public static <T> Equals<T> of(boolean run) {
        return new Equals<>(run, null);
    }

    public static <T> Equals<T> of(T obj) {
        return new Equals<>(null, obj);
    }

    public Equals<T> and(Predicate<? super T> predicate) {
        this.run = null == run ? predicate.test(obj) : (run && predicate.test(obj));
        return this;
    }

    public Equals<T> and(boolean bool) {
        this.run = null == run ? bool : (run && bool);
        return this;
    }

    public Equals<T> or(Predicate<? super T> predicate) {
        this.run = null == run ? predicate.test(obj) : (run || predicate.test(obj));
        return this;
    }

    public Equals<T> or(boolean bool) {
        this.run = null == run ? bool : (run || bool);
        return this;
    }

    public void ifTrue(Runnable runnable) {
        if (run) {
            runnable.run();
        }
    }

    public <X extends Throwable> T ifTrueThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (run) {
            throw exceptionSupplier.get();
        }
        return obj;
    }

    public <X extends Throwable> T ifFalseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (!run) {
            throw exceptionSupplier.get();
        }
        return obj;
    }

    public void ifFalse(Runnable runnable) {
        if (!run) {
            runnable.run();
        }
    }

}