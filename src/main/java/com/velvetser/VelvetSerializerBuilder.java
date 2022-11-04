package com.velvetser;

import com.velvetser.impl.*;

public class VelvetSerializerBuilder {

    private HotClassProvider hotClassProvider = new UnsafeHotClassProvider();
    private SchemaProvider schemaProvider = new ReflectionSchemaProvider(hotClassProvider);
    private SerializerRegistry serializerProvider = new SerializerRegistry();

    public static VelvetSerializerBuilder create() {
        return new VelvetSerializerBuilder();
    }

    private VelvetSerializerBuilder() {
        
    }

    public VelvetSerializer build() {
        return new DefaultVelvetSerializer(hotClassProvider, schemaProvider, serializerProvider);
    }
}
