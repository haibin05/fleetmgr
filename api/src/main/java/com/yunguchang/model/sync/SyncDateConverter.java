package com.yunguchang.model.sync;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.Map;

/**
 * Created by 禕 on 2015/10/15.
 */
public class SyncDateConverter extends StdConverter< String,DateTime> {

    private DateTimeFormatter fmt = ISODateTimeFormat.dateParser();
    @Override
    public DateTime convert(String value) {
        return fmt.parseDateTime(value);
    }


}
