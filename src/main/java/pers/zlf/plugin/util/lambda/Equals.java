package pers.zlf.plugin.util.lambda;

import pers.zlf.plugin.util.function.Execution;

/**
 * @Author zhanglinfeng
 * @Date create in 2023/1/13 14:17
 */
public final class Equals {
    private final boolean run;

    private Equals(Boolean run) {
        this.run = run;
    }

    public static Equals of(Boolean value) {
        return new Equals(value);
    }

    public void ifTrue(Execution execution) {
        if (run) {
            execution.execution();
        }
    }

    public void ifFalse(Execution execution) {
        if (!run) {
            execution.execution();
        }
    }

}
