package com.velvetser.impl;

import com.velvetser.HotClass;
import com.velvetser.HotClassProvider;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
public class CachedHotClassProvider implements HotClassProvider {

    private final Map<String, HotClass<?>> cache = new ConcurrentHashMap<>();

    private final CachedHotClassProvider provider;

    @Override
    public <T> HotClass<T> get(Class<T> clazz) {
        return (HotClass<T>) cache.computeIfAbsent(clazz.getName(), name -> provider.get(clazz));
    }
}
