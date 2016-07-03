package com.yunguchang.rest.impl;

import com.yunguchang.data.DriverRepository;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.GpsUtil;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.rest.DriverService;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.service.PathService;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.yunguchang.model.EntityConvert.fromEntity;
import static com.yunguchang.model.EntityConvert.fromEntityWithoutScheduleCars;
import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * 驾驶员查询接口
 */
@RequestScoped
@Stateful
public class DriverServiceImpl implements DriverService {

    @Inject
    DriverRepository driverRepository;
    @Inject
    private VehicleRepository vehicleRepository;
    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private Logger logger;

    @Inject
    private PathService pathService;

    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Driver> listAllDrivers(@QueryParam("status") String status,
                                       @QueryParam("keyword") String keyword,
                                       @QueryParam("fleetId") String fleetId,
                                       @QueryParam("$offset") Integer offset,
                                       @QueryParam("$limit") Integer limit,
                                       @Context SecurityContext securityContext) {
        DriverStatus driverStatus;
        if (status == null) {
            driverStatus = DriverStatus.ALL;
        } else {
            driverStatus = DriverStatus.valueOf(status);
        }
        List<TRsDriverinfoEntity> results = driverRepository.queryDrivers(driverStatus, keyword, fleetId, offset, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<Driver> driverList = new ArrayList<>();
        for (TRsDriverinfoEntity result : results) {
            driverList.add(EntityConvert.fromEntity(result));
        }
        return driverList;
    }


    @Override
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    public Driver getDriverById(@PathParam("id") String driverId, @Context SecurityContext securityContext) {
        // 1065549
        TRsDriverinfoEntity result = driverRepository.loadDriverById(driverId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (result != null) {
            return EntityConvert.fromEntity(result);
        } else {
            throw logger.entityNotFound(TRsDriverinfoEntity.class, driverId);
        }
    }


    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public DriverInfo getDriverState(@PathParam("id") String driverId,
                                     @QueryParam("now") String now,
                                     @QueryParam("datetime") DateTime dateTime,
                                     @Context SecurityContext securityContext) {
        // 1065549
        DriverInfo driverInfo = new DriverInfo();
        if (now != null || dateTime == null) {
            dateTime = DateTime.now();
        }

        boolean isOnWorking = driverRepository.isOnWorkingByDriverIdAndDate(driverId, dateTime, SecurityUtil.getPrincipalExtOrNull(securityContext));

        if (isOnWorking) {
            driverInfo.setOnDuty(true);

        } else {
            driverInfo.setOnDuty(false);
            return driverInfo;
        }
        if (driverInfo.isOnDuty()) {

            TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCarByDriverIdAndTime(driverId, dateTime, SecurityUtil.getPrincipalExtOrNull(securityContext));
            if (scheduleCarEntity != null) {
                driverInfo.setSchedule(fromEntityWithoutScheduleCars(scheduleCarEntity.getSchedule()));
                driverInfo.setCar(fromEntity(scheduleCarEntity.getCar()));
                List<TGpsPointEntity> gpsRecords = gpsRepository.getGpsRecordByCarIdAndBeforeTime(scheduleCarEntity.getCar().getId(), dateTime, 1, SecurityUtil.getPrincipalExtOrNull(securityContext));


                if (gpsRecords != null && gpsRecords.size() > 0) {
                    List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(gpsRecords.get(0));
                    Collections.reverse(points);
                    for (GpsPoint gpsPoint : points) {
                        if (gpsPoint != null && gpsPoint.getPersistTime().isBefore(dateTime)) {
                            driverInfo.setGpsPoint(gpsPoint);
                            break;
                        }

                    }
                }
            }


        }
        return driverInfo;
    }

    @Override
    @GET
    @NoCache
    @Path("/{id}/schedules")
    @Produces(APPLICATION_JSON_UTF8)
    public List<Schedule> getDriverTasks(@PathParam("id") String driverId,
                                         @QueryParam("start") DateTime start,
                                         @QueryParam("end") DateTime end, @Context SecurityContext securityContext) {
        // driverId = 1677292
        if (end == null) {
            end = DateTime.now();
        }
        if (start == null) {
            start = end.withTimeAtStartOfDay();
        }
        List<TBusScheduleRelaEntity> schedules = driverRepository.getDriverSchedules(driverId, start, end, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Schedule> results = new ArrayList<>();
        for (TBusScheduleRelaEntity scheduleEntity : schedules) {
            results.add(EntityConvert.fromEntityWithoutScheduleCars(scheduleEntity));
        }
        return results;
    }


    @Override
    @GET
    @NoCache
    @Path("/{id}/paths")
    @Produces(APPLICATION_JSON_UTF8)//"application/json; charset=UTF-8"
    public List<PathWithAlarm> getPaths(@PathParam("id") String driverId,
                                        @QueryParam("start") DateTime start,
                                        @QueryParam("end") DateTime end,
                                        @Context SecurityContext securityContext) {

        // driverId = 1065793
        if (end == null) {
            end = DateTime.now();
        }
        if (start == null) {
            start = end.withTimeAtStartOfDay();
        }

        List<PathWithAlarm> returnPaths = new ArrayList<>();
        PrincipalExt principal = SecurityUtil.getPrincipalExtOrNull(securityContext);
        List<TBusScheduleCarEntity> scheduleCars = scheduleRepository.getSchedulesByDriverIdAndStartAndEnd(driverId, start, end, principal);
        if (scheduleCars.size() > 0) {
            for (TBusScheduleCarEntity scheduleCar : scheduleCars) {
                String carId = scheduleCar.getCar().getId();
                DateTime pathStart = scheduleCar.getSchedule().getStarttime();
                if (pathStart.isBefore(start)) {
                    pathStart = start;
                }
                DateTime pathEndTime = scheduleCar.getSchedule().getEndtime();
                if (pathEndTime.isAfter(end)) {
                    pathEndTime = end;
                }
                List<PathWithAlarm> paths = pathService.caclPathWithAlarms(carId, pathStart, pathEndTime, principal);
                returnPaths.addAll(paths);
            }
        } else {
            // 按照 驾驶员、开始时间、结束时间 进行GPS数据查询
            TAzCarinfoEntity car = vehicleRepository.getCarByDriverId(driverId, principal);
            if (car != null) {
                List<PathWithAlarm> paths = pathService.caclPathWithAlarms(car.getId(), start, end, principal);
                returnPaths.addAll(paths);
            }
        }
        return returnPaths;
    }





}
