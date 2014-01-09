/*
 * Copyright (c) 2011. Purple Door Systems, BV.
 */

package com.mineness.utils.jackson;

import org.bson.types.ObjectId;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.ser.CustomSerializerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Bjorn Harvold
 * Date: 7/28/11
 * Time: 1:21 PM
 * Responsibility:
 */
public class CustomObjectMapper extends ObjectMapper {
    private final static Logger log = LoggerFactory.getLogger(CustomObjectMapper.class);

    public CustomObjectMapper() {
        CustomSerializerFactory sf = new CustomSerializerFactory();
        sf.addSpecificMapping(ObjectId.class, new ObjectIdSerializer());
        SimpleModule module = new SimpleModule("ObjectIdModule", new Version(1, 0, 0, null))
                .addDeserializer(ObjectId.class, new ObjectIdDeserializer());
        registerModule(module);
        this.setSerializerFactory(sf);

//        log.info("Registered custom Jackson custom object mapper");
    }
}
