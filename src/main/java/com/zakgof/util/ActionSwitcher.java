package com.zakgof.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class ActionSwitcher<T> {

    private final Map<Class<? extends T>, Consumer<? extends T>> map = new HashMap<>();
    private Consumer<T> defaultHandler;

    public <C extends T> ActionSwitcher<T> withCase(Class<C> clazz, Consumer<C> caseResolver) {
        map.put(clazz, caseResolver);
        return this;
    }

    public void run(T def) {
        Consumer resolver = map.get(def.getClass());
        if (resolver == null)
            defaultHandler.accept(def);
        else
            resolver.accept(def);
    }

    public ActionSwitcher<T> withDefault(Consumer<T> defaultHandler) {
        this.defaultHandler = defaultHandler;
        return this;
    }
}
