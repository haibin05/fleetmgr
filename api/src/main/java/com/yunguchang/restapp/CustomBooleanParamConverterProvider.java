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
public class CustomBooleanParamConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(Boolean.class)) {
            return (ParamConverter<T>) new ParamConverter<Boolean>() {
                @Override
                public Boolean fromString(String value) {
                    if (value == null) {
                        return false;
                    } else if (value.equals("y")) {
                        return Boolean.TRUE;
                    } else if (value.equals("n")) {
                        return Boolean.FALSE;
                    } else if (value.equals("yes")) {
                        return Boolean.TRUE;
                    } else if (value.equals("no")) {
                        return Boolean.FALSE;
                    } else if (Ints.tryParse(value) != null) {
                        int i = Ints.tryParse(value).intValue();
                        if (i == 0) {
                            return Boolean.FALSE;
                        } else {
                            return Boolean.TRUE;
                        }
                    }
                    return Boolean.valueOf(value);
                }

                @Override
                public String toString(Boolean value) {
                    return value.toString();
                }
            };
        }



        return null;
    }
}
