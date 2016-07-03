package com.yunguchang.utils.tools;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by WHB on 2015-11-20.
 */
public class FreeMarkerUtil {
    public static String generateContent(Map<String, Object> context, String templateName) {

        if(templateName == null) {
            return null;
        }
        StringWriter result = new StringWriter();
        try {
            Configuration config = new Configuration();

            String templatesPath = System.getProperty("user.home") + File.separator + "XT" + File.separator + "templates";
            config.setDirectoryForTemplateLoading(new File(templatesPath));
            config.setObjectWrapper(new DefaultObjectWrapper());

            Template template = config.getTemplate(templateName, "UTF-8");

            if (template != null) {
                template.process(context, result);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            result.flush();
            try {
                result.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }
}
