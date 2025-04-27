package pers.zlf.plugin.util.lambda;

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @author zhanglinfeng
 * @date create in 2023/1/13 14:17
 */
public final class Equals<T> {
    private Boolean run;
    private final T OBJ;

    private Equals(Boolean run, T obj) {
        this.run = run;
        this.OBJ = obj;
    }

    public static <T> Equals<T> of(boolean run) {
        return new Equals<>(run, null);
    }

    public static <T> Equals<T> of(T obj) {
        return new Equals<>(null, obj);
    }

    public Equals<T> and(Predicate<? super T> predicate) {
        this.run = null == run ? predicate.test(OBJ) : (run && predicate.test(OBJ));
        return this;
    }

    public Equals<T> and(boolean bool) {
        this.run = null == run ? bool : (run && bool);
        return this;
    }

    public Equals<T> or(Predicate<? super T> predicate) {
        this.run = null == run ? predicate.test(OBJ) : (run || predicate.test(OBJ));
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

    public void ifTrue(Consumer<? super T> action) {
        if (run) {
            action.accept(OBJ);
        }
    }

    public <X extends Throwable> T ifTrueThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (run) {
            throw exceptionSupplier.get();
        }
        return OBJ;
    }

    public void ifFalse(Runnable runnable) {
        if (!run) {
            runnable.run();
        }
    }

    public void ifFalse(Consumer<? super T> action) {
        if (!run) {
            action.accept(OBJ);
        }
    }

    public <X extends Throwable> T ifFalseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (!run) {
            throw exceptionSupplier.get();
        }
        return OBJ;
    }

    public void then(Consumer<? super T> trueAction, Consumer<? super T> falseAction) {
        if (run) {
            trueAction.accept(OBJ);
        } else {
            falseAction.accept(OBJ);
        }
    }
}