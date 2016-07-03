package com.yunguchang.rest.impl;

import com.yunguchang.baidu.KeyPool;
import com.yunguchang.data.*;
import com.yunguchang.gps.BaiduClient;
import com.yunguchang.gps.GeoCoderResult;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Alarm;
import com.yunguchang.model.common.AlarmEvent;
import com.yunguchang.model.common.GpsPoint;
import com.yunguchang.model.common.StatisticsInfo;
import com.yunguchang.model.persistence.TBusAlarmEntity;
import com.yunguchang.rest.AlarmService;
import com.yunguchang.rest.RestUtil;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sam.PrincipalExt;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Phaser;

import static com.yunguchang.restapp.JaxRsActivator.*;

/**
 * Created by gongy on 9/1/2015.
 */

@RequestScoped
@Stateful
public class AlarmServiceImpl implements AlarmService {

    @Resource(lookup = "java:jboss/ee/concurrency/executor/path")
    private ManagedExecutorService mes;

    @Inject
    private AlarmRepository alarmRepository;
    @Context
    private HttpServletResponse servletResponse;

    @Inject
    private KeyPool keyPool;

    @Inject
    private BaiduClient baiduClient;

    @Inject
    private VehicleRepository vehicleRepository;
    @Inject
    private ScheduleRepository scheduleRepository;
    @Inject
    private DepotRepository depotRepository;

    @Override
    @GET
    @NoCache
    @Produces({APPLICATION_JSON_UTF8})
    public Map<String, Object> listAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext, Request request, HttpServletResponse response) {
        List<TBusAlarmEntity> results = alarmRepository.getAlarmByStartEndType(start, end, alarm, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<AlarmEvent> alarms = new ArrayList();

        for (TBusAlarmEntity result : results) {
            AlarmEvent alarmEvent = (EntityConvert.fromEntity(result));
            if (alarmEvent.getAlarm() != null) {
                alarms.add(alarmEvent);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("alarms", alarms);

        // 统计数据
        StatisticsInfo currentInventoryInfo =  getCurrentStatisticsInfo(securityContext);
        result.put("statistics", currentInventoryInfo);

        return result;
    }

    @Override
    @GET
    @NoCache
    @Produces({APPLICATION_JSON_UTF8, APPLICATION_SPREAD_SHEET})
    public List<AlarmEvent> exportAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @QueryParam("$limit") Integer limit,
            @Context SecurityContext securityContext, Request request, HttpServletResponse response) {
        List<TBusAlarmEntity> results = alarmRepository.getAlarmByStartEndType(start, end, alarm, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<AlarmEvent> alarms = new ArrayList();

        for (TBusAlarmEntity result : results) {
            AlarmEvent alarmEvent = (EntityConvert.fromEntity(result));
            if (alarmEvent.getAlarm() != null) {
                alarms.add(alarmEvent);
            }
        }

        if (RestUtil.getMediaType(request).isCompatible(APPLICATION_EXCEL_TYPE)) {
            final Phaser phaser = new Phaser();
            phaser.register();

            for (final AlarmEvent alarmEvent : alarms) {

                phaser.register();
                mes.submit(new Runnable() {
                    @Override
                    public void run() {
                        String key = keyPool.acquire();
                        if (key == null) {
                            return;
                        }
                        try {
                            GpsPoint gpsPoint = alarmEvent.getGpsPoint();
                            String location = gpsPoint.getLat() + "," + gpsPoint.getLng();
                            GeoCoderResult.Result baiduResult = baiduClient.geocoder(location, "json", key).getResult();
                            StringBuilder sb = new StringBuilder();
                            String formattedAddress = baiduResult.getFormattedAddress();
                            if (formattedAddress != null) {
                                sb.append(formattedAddress);
                            }
                            String sematicDescription = baiduResult.getSematicDescription();
                            if (sematicDescription != null) {
                                sb.append(" ").append(sematicDescription);
                            }
                            gpsPoint.setAddress(sb.toString());
                        } finally {
                            if (key != null) {
                                keyPool.release(key);
                            }
                            phaser.arrive();
                        }
                    }
                });
            }
            phaser.arriveAndAwaitAdvance();
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
            String filename = "alarms_" + formatter.print(start) + ".xlsx";
            try {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
            } catch (UnsupportedEncodingException e) {

            }
        }
        return alarms;
    }


    /**
    1）统计数据显示：
    车辆数
    安装GPS车辆数
    当日任务数量
    当日告警数量
    当前出车数量
    昨日9:00未回厂数量
    */
    public StatisticsInfo getCurrentStatisticsInfo(SecurityContext securityContext){
        DateTime now = DateTime.now();
        DateTime startTime = now.withTimeAtStartOfDay();
        DateTime endTime = startTime.plusDays(1);

        StatisticsInfo statisticsInfo = new StatisticsInfo();
        statisticsInfo.setInventoryTime(DateTime.now());

        PrincipalExt currentUser = SecurityUtil.getPrincipalExtOrNull(securityContext);
        // 车辆数
        int count = vehicleRepository.countCarWithGpsState(null, currentUser);
        statisticsInfo.setCountOfAllCar(count);

        // 安装GPS车辆数
        count = vehicleRepository.countCarWithGpsState(true, currentUser);
        statisticsInfo.setCountOfGpsCar(count);

        // 当日任务数量           -1 有效的调度单
        count = scheduleRepository.countAllScheduleWithTime(startTime, endTime, null, null, -1, currentUser);
        statisticsInfo.setCountOfSchedule(count);

        // 当日告警数量
        count = alarmRepository.countAllAlarmsByType(startTime, endTime, null, currentUser);
        statisticsInfo.setCountOfAlarm(count);

        // 当前出车数量
        count = scheduleRepository.countAllScheduleCarWithTime(now, now, currentUser);
        statisticsInfo.setCountOfOutCar(count);

        // 昨日9:00未回厂数量
        count = depotRepository.countCarsNotInDepot(startTime.minusHours(3).minusMillis(1), endTime, currentUser);
        statisticsInfo.setCountOfNotReturnCar(count);

        return statisticsInfo;
    }

    @Override
    public int countAllAlarms(
            @QueryParam("start") DateTime start,
            @QueryParam("end") DateTime end,
            @QueryParam("alarm") Alarm alarm,
            @Context SecurityContext securityContext) {
        return 0;
    }

    @Override
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    public AlarmEvent lookupAlarmById(@PathParam("id") String id) {
        return null;
    }


}
