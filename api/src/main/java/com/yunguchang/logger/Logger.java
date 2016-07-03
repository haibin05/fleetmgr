package com.yunguchang.logger;

/**
 * Created by 禕 on 2105/10/24.
 */


import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.joda.time.DateTime;

import javax.ejb.EJBAccessException;
import javax.persistence.EntityNotFoundException;

import static org.jboss.logging.Logger.Level.*;


@org.jboss.logging.annotations.MessageLogger(projectCode = "FLEET")
public interface Logger {


    @Message(value = "Can't find entity of %s with id %s", id = 20000)
    EntityNotFoundException entityNotFound(Class clazz, Object key);

    @Message(value = "Can't create schedule %s", id = 20001)
    IllegalArgumentException invalidSchedule(Schedule schedule);




    @Message(value = "No valid start or end time of application %s", id = 20002)
    IllegalArgumentException invalidApplication(String applicationids);

    @Message(value = "Can't find the car badge of %s", id = 20003)
    IllegalArgumentException invalidCar(String id);

    @Message(value = "Can't execute the operation named %s", id = 20004)
    IllegalArgumentException invalidOperation(String operationName);


    @LogMessage(level = DEBUG)
    @Message(value = "%s", id = 2000)
    void debug(Object s);


    @LogMessage(level = DEBUG)
    @Message(value = "%s,%s", id = 2001)
    void debug(Object s, Throwable failure);

    @LogMessage(level = DEBUG)
    @Message(value = "fetch gps data", id = 2100)
    void fetchGpsData();

    @LogMessage(level = DEBUG)
    @Message(value = "start to adjust path", id = 2101)
    void startAdjustPath();

    @LogMessage(level = DEBUG)
    @Message(value = "%d records adjusting", id = 2102)
    void numberOfAjusteing(int number);

    @LogMessage(level = DEBUG)
    @Message(value = "can't adjust whole records", id = 2103)
    void cantAdjustWholeRecords();

    @LogMessage(level = DEBUG)
    @Message(value = "start to fix invalid path", id = 2104)
    void startFixInvalidPath();

    @LogMessage(level = DEBUG)
    @Message(value = "adjust path," +
            " POINTS_NUMBER_SKIP_ADJUSTMENT is %s," +
            " origin is %s" +
            " destination is %s", id = 2105)
    void adjustPath(int number, GpsPoint origin, GpsPoint destination);

    @LogMessage(level = DEBUG)
    @Message(value = "fetch gps data finished", id = 2106)
    void fetchGpsDataFinished();


    @LogMessage(level = DEBUG)
    @Message(value = "Insert scheduleCar to rule engine, %s", id = 2107)
    void insertScheduleCar(ScheduleCar scheduleCar);


    @LogMessage(level = INFO)
    @Message(value = "%s", id = 3000)
    void info(Object s);

    @LogMessage(level = INFO)
    @Message(value = "%s,%s", id = 3001)
    void info(Object s, Throwable failure);

    @LogMessage(level = INFO)
    @Message(value = "Browser %s connected.", id = 3100)
    void browserConnected(String browser);

    @LogMessage(level = INFO)
    @Message(value = "Injected Factory %s connected.", id = 3101)
    void injectedFactoryConnected(String factory);

    @LogMessage(level = INFO)
    @Message(value = "Browser %s unexpectedly disconnected.", id = 3102)
    void browserUnexpectedlyDisconnected(String browser);

    @LogMessage(level = INFO)
    @Message(value = "Browser %s closed the connection.", id = 3103)
    void browserClosedTheConnection(String browser);

    @LogMessage(level = INFO)
    @Message(value = "GPS point dropped. Badge is %s. Current time is %s. Sample time was %s", id = 3104)
    void dropGpsPoint(String badge, DateTime currentTime, DateTime sampleTime);

    @LogMessage(level = INFO)
    @Message(value = "invalid token provided %s", id = 3105)
    void invalidToken(String s);

    @LogMessage(level = INFO)
    @Message(value = "no such user %s", id = 3106)
    void noSuchUser(String e);


    @LogMessage(level = WARN)
    @Message(value = "%s", id = 4000)
    void warn(Object s);

    @LogMessage(level = WARN)
    @Message(value = "%s,%s", id = 4001)
    void warn(Object s, Throwable failure);




    @LogMessage(level = WARN)
    @Message(value = "alarm %s is dropped, because can't find the car in the database", id = 4100)
    void dropAlarm(AlarmEvent alarmEvent);

    @LogMessage(level = WARN)
    @Message(value = "returning depot event %s is dropped, because can't find the car in the database", id = 4101)
    void dropReturnEvent(ReturningDepotEvent returningDepotEvent);


    @LogMessage(level = WARN)
    @Message(value = "Unknown reason causes duplicated event %s", id = 4102)
    void eventDuplicated(Object object);

    @LogMessage(level = WARN)
    @Message(value = "Rule engine restarting", id = 4103)
    void restartRuleEngine();


    @LogMessage(level = ERROR)
    @Message(value = "%s", id = 5000)
    void error(Object s);

    @LogMessage(level = ERROR)
    @Message(value = "%s,%s", id = 5001)
    void error(Object s, Throwable failure);

    @LogMessage(level = ERROR)
    @Message(value = "mismatch point time %s, %s", id = 5101)
    void mismatchPointTime(DateTime pointTime, GpsPoint point);


    @LogMessage(level = ERROR)
    @Message(value = "error when adjust path, %s", id = 5102)
    void errorWhenAdjust(Throwable failure);

    @LogMessage(level = ERROR)
    @Message(value = "Unknown error when query alarm by example, %s", id = 5103)
    void queryAlarmByExampleError(String message);

    @LogMessage(level = ERROR)
    @Message(value = "Unknown error when query return events by example, %s", id = 5104)
    void queryReturnEventByExampleError(String message);

    @LogMessage(level = ERROR)
    @Message(value = "Unknown error when executing rule engine, %s", id = 5105)
    void ruleEngineExecutingError(Throwable e );


}