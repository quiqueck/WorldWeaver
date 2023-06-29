package org.betterx.wover.util.function;

/**
 * Functional interfaces that provides a call without parameters and without return value.
 */
@FunctionalInterface
public interface VoidFunction {
    /**
     * the call method.
     */
    void call();
}
