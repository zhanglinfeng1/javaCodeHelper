package pers.zlf.plugin.util.lambda;

/**
 * @author zhanglinfeng
 * @date create in 2024/9/25 16:47
 */
@FunctionalInterface
public interface TriConsumer<T, U, R> {
    void accept(T t, U u, R r);
}
