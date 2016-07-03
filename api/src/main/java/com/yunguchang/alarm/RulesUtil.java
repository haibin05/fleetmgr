package com.yunguchang.alarm;

import com.yunguchang.logger.Logger;
import com.yunguchang.model.common.Car;
import org.apache.commons.beanutils.PropertyUtils;
import org.drools.core.spi.KnowledgeHelper;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.kie.api.time.SessionClock;

import java.lang.reflect.InvocationTargetException;

//import org.slf4j.LoggerFactory;

/**
 * Created by gongy on 9/22/2015.
 */
public class RulesUtil {


    public static void debug(KnowledgeHelper drools, Object argument) {

        String category = drools.getRule().getPackageName() + "."
                + drools.getRule().getName();

        Logger logger = org.jboss.logging.Logger.getMessageLogger(Logger.class, category);
        logger.debug(argument);
    }

    public static void info(KnowledgeHelper drools,
                            Object argument) {

        String category = drools.getRule().getPackageName() + "."
                + drools.getRule().getName();

        Logger logger = org.jboss.logging.Logger.getMessageLogger(Logger.class, category);
        logger.info(argument);
    }

    public static boolean isSameDay(DateTime dateTime1, SessionClock clock) {
        return Days.daysBetween(dateTime1, new DateTime(clock.getCurrentTime())).getDays() == 0;
    }


    public static boolean isInSchedule(Object scheduleEvent,Car car, SessionClock clock) {
        try {
            Car carInSchedule = (Car) PropertyUtils.getProperty(scheduleEvent, "car");
            if (!carInSchedule.getId().equals(car.getId())){
                return false;
            }
            DateTime startTime = (DateTime) PropertyUtils.getProperty(scheduleEvent, "startTime");
            long duration = (long) PropertyUtils.getProperty(scheduleEvent, "duration");
            long currentTime = clock.getCurrentTime();
            if (startTime.minusMinutes(30).isAfter(currentTime)) {
                return false;
            } else if (startTime.plus(duration).isAfter(currentTime)) {
                return true;
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return false;

    }

    public static DateTime now(){
        return DateTime.now();
    }


}
