package com.velvetser;

import com.velvetser.impl.*;

public class VelvetSerializerBuilder {

    private HotClassProvider hotCache = new ReflectionHotClassProvider();
    private SchemaProvider schemaCache = new CachedSchemaProvider(new ReflectionSchemaProvider((ReflectionHotClassProvider) hotCache));

    public static VelvetSerializerBuilder create() {
        return new VelvetSerializerBuilder();
    }

    private VelvetSerializerBuilder() {
        
    }

    public VelvetSerializer build() {
        return new DefaultVelvetSerializer(hotCache, schemaCache);
    }
}
