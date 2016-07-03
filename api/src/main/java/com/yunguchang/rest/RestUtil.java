package com.yunguchang.rest;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Variant;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_EXCEL_TYPE;

/**
 * Created by 禕 on 2015/10/16.
 */
public class RestUtil {
    public static MediaType getMediaType(Request req) {
        List<Variant> vars = Variant
                .mediaTypes(MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8"), APPLICATION_EXCEL_TYPE)
                .add()
                .build();
        Variant variant = req.selectVariant(vars);
        if (variant != null) {
            return variant.getMediaType();
        } else {
            return MediaType.APPLICATION_JSON_TYPE.withCharset("utf-8");
        }
    }
}
