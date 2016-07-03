package com.yunguchang.restapp.excel;

import org.apache.poi.ss.usermodel.Workbook;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by gongy on 2015/11/22.
 */
//Add @Provider and @Produces({APPLICATION_SPREAD_SHEET}) at Subclasses instead here
abstract public class AbstractExcelWriter<T> implements MessageBodyWriter<List<T>> {

    protected Class<T> elementClass;

    public AbstractExcelWriter(Class<T> clazz) {
        elementClass = clazz;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            if (parameterizedType.getActualTypeArguments().length > 0) {
                Type elementType = parameterizedType.getActualTypeArguments()[0];
                if (elementType instanceof Class && (elementClass.isAssignableFrom((Class<?>) elementType))) {
                    return true;
                }
            }

        }
        return false;
    }

    @Override
    public long getSize(List<T> ts, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }

    @Override
    public void writeTo(List<T> ts, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        Workbook workbook = create(ts);
        workbook.write(entityStream);

    }

    protected abstract Workbook create(List<T> elementList);
}
