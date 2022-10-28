package com.zakgof.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;


public class Switcher<T, R> {

    private final Map<Class<? extends T>, Function<? extends T, R>> map = new HashMap<>();
    private Supplier<R> defaultHandler;

    public <C extends T> Switcher<T, R> withCase(Class<C> clazz, Function<C, R> caseResolver) {
        map.put(clazz, caseResolver);
        return this;
    }

    public R run(T def) {
        Function resolver = map.get(def.getClass());
        return (resolver == null) ? defaultHandler.get() : (R)resolver.apply(def);
    }

    public Switcher<T, R> withDefault(Supplier<R> defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }
}
