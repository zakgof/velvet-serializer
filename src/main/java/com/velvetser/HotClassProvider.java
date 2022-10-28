package com.velvetser;

public interface HotClassProvider {
    <T> HotClass<T> get(Class<T> clazz);
}
