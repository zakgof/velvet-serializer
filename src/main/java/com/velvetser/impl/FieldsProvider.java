package com.velvetser.impl;

import java.lang.reflect.Field;

public interface FieldsProvider<T> {
    Field[] fields();
}
