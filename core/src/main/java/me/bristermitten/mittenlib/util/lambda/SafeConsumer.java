package me.bristermitten.mittenlib.util.lambda;

import me.bristermitten.mittenlib.util.Errors;

import java.util.function.Consumer;

@FunctionalInterface
public interface SafeConsumer<T> {
    void consume(T t) throws Throwable;

    default Consumer<T> asConsumer() {
        return t -> {
            try {
                consume(t);
            } catch (Throwable e) {
                Errors.sneakyThrow(e);
            }
        };
    }
}
