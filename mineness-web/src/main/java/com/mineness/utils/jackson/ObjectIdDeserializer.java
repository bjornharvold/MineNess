/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.utils.jackson;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.std.StdDeserializer;

import java.io.IOException;

/**
 * Created by Bjorn Harvold
 * Date: 8/10/11
 * Time: 2:30 PM
 * Responsibility:
 */
public class ObjectIdDeserializer extends StdDeserializer<ObjectId>{

    protected ObjectIdDeserializer() {
        super(ObjectId.class);
    }

    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return ObjectId.massageToObjectId(jsonParser.getText());
    }
}
