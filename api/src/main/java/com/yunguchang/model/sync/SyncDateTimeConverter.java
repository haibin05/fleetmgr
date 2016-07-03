package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Created by gongy on 2015/10/20.
 */
public class SyncDateTimeConverter implements Converter< String,DateTime> {
    private DateTimeFormatter fmt = ISODateTimeFormat.dateParser();
    @Override
    public DateTime convert(String value) {
        return fmt.parseDateTime(value);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }


    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(DateTime.class);
    }
}
