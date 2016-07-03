package com.yunguchang.restapp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaMapper;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Created by gongy on 9/10/2015.
 */
@Provider
public class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    final JodaMapper mapper = new JodaMapper();

    public ObjectMapperContextResolver() {
        mapper.setWriteDatesAsTimestamps(false);
        //mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

    }

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return mapper;
    }
}
