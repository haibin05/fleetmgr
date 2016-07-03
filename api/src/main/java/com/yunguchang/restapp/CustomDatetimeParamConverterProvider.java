package com.yunguchang.restapp;

import com.google.common.primitives.Ints;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by gongy on 9/11/2015.
 */
@Provider
public class CustomDatetimeParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {



        if (rawType.equals(DateTime.class)) {
            return (ParamConverter<T>) new ParamConverter<DateTime>() {
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();

                @Override
                public DateTime fromString(String value) {
                    return fmt.parseDateTime(value);
                }

                @Override
                public String toString(DateTime value) {
                    return fmt.print(value);
                }
            };
        }
        return null;
    }
}
