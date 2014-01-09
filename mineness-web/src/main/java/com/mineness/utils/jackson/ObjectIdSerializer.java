/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.utils.jackson;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by Bjorn Harvold
 * Date: 7/28/11
 * Time: 1:17 PM
 * Responsibility:
 */
public class ObjectIdSerializer extends SerializerBase<ObjectId> {
    public ObjectIdSerializer() {
        super(ObjectId.class);
    }

    @Override
    public void serialize(ObjectId objectId, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonGenerationException {
        jsonGenerator.writeString(objectId.toString());
    }

    @Override
    public JsonNode getSchema(SerializerProvider serializerProvider, Type type) throws JsonMappingException {
        throw new UnsupportedOperationException();
    }
}
