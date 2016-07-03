package com.yunguchang.restapp;


import com.yunguchang.model.common.OrderByParam;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gongy on 2015/11/9.
 */
@Provider
public class CustomOrderbyParamConverterProvider implements ParamConverterProvider {
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(OrderByParam.class))        {
            return (ParamConverter<T>) new ParamConverter<OrderByParam>() {
                @Override
                public OrderByParam fromString(String value) {
                    if (StringUtils.isBlank(value)){
                        return null;
                    }
                    return new OrderByParam(value);
                }

                @Override
                public String toString(OrderByParam orderByParam) {
                    return orderByParam.toString();
                }
            };
        }
        return null;
    }
}
