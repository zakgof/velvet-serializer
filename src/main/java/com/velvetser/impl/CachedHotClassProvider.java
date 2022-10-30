package com.velvetser.impl;

import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

public class CachedHotClassProvider implements HotClassProvider {

    private final Map<Class<?>, HotClass<?>> cache = new IdentityHashMap<>();
    private final Function<Class<?>, HotClass<?>> providerGetFunc;

    public CachedHotClassProvider(HotClassProvider provider) {
        this.providerGetFunc = provider::get;
    }

    @Override
    public <T> HotClass<T> get(Class<T> clazz) {
        return (HotClass<T>) cache.computeIfAbsent(clazz, providerGetFunc);
    }
}
