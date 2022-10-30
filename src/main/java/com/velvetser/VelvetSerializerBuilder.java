package com.velvetser;

import com.velvetser.impl.*;

public class VelvetSerializerBuilder {

    private HotClassProvider hotCache = new CachedHotClassProvider(new ReflectionHotClassProvider());
    private SchemaProvider schemaCache = new CachedSchemaProvider(new ReflectionSchemaProvider(hotCache));

    public static VelvetSerializerBuilder create() {
        return new VelvetSerializerBuilder();
    }

    private VelvetSerializerBuilder() {
        
    }

    public VelvetSerializer build() {
        return new DefaultVelvetSerializer(hotCache, schemaCache);
    }
}
