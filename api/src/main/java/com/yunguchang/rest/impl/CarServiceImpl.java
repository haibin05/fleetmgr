package com.yunguchang.rest.impl;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.baidu.KeyPool;
import com.yunguchang.data.ApplicationRepository;
import com.yunguchang.data.GpsRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.data.VehicleRepository;
import com.yunguchang.gps.*;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.common.Application;
import com.yunguchang.model.persistence.*;
import com.yunguchang.rest.CarService;
import com.yunguchang.rest.RestUtil;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.service.PathService;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Phaser;

import static com.yunguchang.model.EntityConvert.fromEntity;
import static com.yunguchang.model.EntityConvert.getCarStatus;
import static com.yunguchang.restapp.JaxRsActivator.*;
import static java.util.Arrays.asList;

/**
 * 车辆查询接口
 */

@RequestScoped
@Stateful
public class CarServiceImpl implements CarService {
    /**
     * 查询车辆列表
     *
     * @param badge 查询条件：车牌
     * @return
     */


    @Inject
    private VehicleRepository vehicleRepository;


    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private GpsRepository gpsRepository;


    @Resource(lookup = "java:jboss/ee/concurrency/executor/path")
    private ManagedExecutorService mes;


    @Inject
    private BaiduClient baiduClient;

    @Inject
    private GpsClient gpsClient;

    @Inject
    private Logger logger;

    @Inject
    private KeyPool keyPool;

    @Inject
    private PathService pathService;


    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Car> listAllCars(@QueryParam("badge") String badge,
                                 @QueryParam("fleet") String fleetName,
                                 @QueryParam("fleetId") String fleetId,
                                 @QueryParam("keyword") String keyword,
                                 @QueryParam("moving") Integer isMoving,
                                 @QueryParam("state") CarState state,
                                 @QueryParam("lastGps") Boolean lastGps,
                                 @QueryParam("gpsInstalled") Boolean gpsInstalled,
                                 @QueryParam("$offset") Integer offset,
                                 @QueryParam("$limit") Integer limit,
                                 @Context SecurityContext securityContext) {
        List<TAzCarinfoEntity> results = vehicleRepository.listAllCars(badge, fleetName, keyword, fleetId, isMoving, state, lastGps, gpsInstalled, offset, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Car> cars = new ArrayList<Car>();
        for (TAzCarinfoEntity result : results) {
            Car car = EntityConvert.fromEntity(result);
            if (Boolean.TRUE.equals(lastGps) && result.getGpsPoints() != null && result.getGpsPoints().size() > 0) {
                TGpsPointEntity gpsPointEntity = result.getGpsPoints().get(0);
                List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(gpsPointEntity);
                Collections.reverse(points);
                GpsPoint point = Iterables.find(points, Predicates.notNull(), null);
                car.setLastGps(point);
            }
            cars.add(car);
        }

        return cars;


    }


    @Override
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Car.class)
    public Car getCar(@PathParam("id") String id, @Context SecurityContext securityContext) {
        TAzCarinfoEntity carinfoEntity = vehicleRepository.getCarById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (carinfoEntity != null) {
            return EntityConvert.fromEntity(carinfoEntity);
        } else {
            throw new NotFoundException();
        }
    }

    @Override
    @GET
    @NoCache
    @Path("/{id}/state")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(CarInfo.class)

    public CarInfo getCarState(@PathParam("id") String id,
                               @QueryParam("now") String now,
                               @QueryParam("datetime") DateTime dateTime,
                               @Context SecurityContext securityContext) {
        TAzCarinfoEntity carEntity = vehicleRepository.getCarById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));

        if (carEntity == null) {
            throw new NotFoundException();
        }

        CarInfo carInfo = new CarInfo();

        if (now != null) {
            dateTime = DateTime.now();
        }
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCarByCarIdAndTime(id, dateTime, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (scheduleCarEntity != null) {
            carInfo.setSchedule(EntityConvert.fromEntityWithoutScheduleCars(scheduleCarEntity.getSchedule()));
            carInfo.setDriver(EntityConvert.fromEntity(scheduleCarEntity.getDriver()));
            carEntity.setInusing(true);
        } else {
            carInfo.setDriver(fromEntity(carEntity.getDriver()));
        }
        carInfo.setCarState(getCarStatus(carEntity));
        carInfo.setFleet(fromEntity(carEntity.getSysOrg()));

        List<TGpsPointEntity> gpsRecords = gpsRepository.getGpsRecordByCarIdAndBeforeTime(id, dateTime, 1, SecurityUtil.getPrincipalExtOrNull(securityContext));


        if (gpsRecords.size() > 0) {
            List<GpsPoint> points = GpsUtil.fromPersistPointsWithShallowCarObject(gpsRecords.get(0));
            Collections.reverse(points);
            for (GpsPoint gpsPoint : points) {
                if (gpsPoint != null && gpsPoint.getPersistTime().isBefore(dateTime)) {
                    carInfo.setGpsPoint(gpsPoint);
                    break;
                }

            }
        }

        return carInfo;
    }

    @Override
    @GET
    @NoCache
    @Path("/{id}/schedules")
    @Produces(APPLICATION_JSON_UTF8)
    public List<Schedule> getCarSchedules(@PathParam("id") String id,
                                          @QueryParam("start") DateTime start,
                                          @QueryParam("end") DateTime end,
                                          @Context SecurityContext securityContext) {
        if (end == null) {
            end = DateTime.now();
        }
        if (start == null) {
            start = end.withTimeAtStartOfDay();
        }
        List<TBusScheduleRelaEntity> schedules = scheduleRepository.getSchedulesByCarIdAndStartAndEnd(id, start, end, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Schedule> results = new ArrayList<Schedule>();

        for (TBusScheduleRelaEntity scheduleEntity : schedules) {
            List<TBusApplyinfoEntity> applyinfoEntitList = applicationRepository.getApplicationWithDetailByScheduleId(scheduleEntity.getUuid(), SecurityUtil.getPrincipalExtOrNull(securityContext));

            List<Application> applications = new ArrayList<>();
            for (TBusApplyinfoEntity applyinfoEntity : applyinfoEntitList) {
                applications.add(EntityConvert.fromEntity(applyinfoEntity));
            }

            Schedule schedule = EntityConvert.fromEntityWithScheduleCars(scheduleEntity);
            schedule.setApplications(applications.toArray(new Application[]{}));
            results.add(schedule);
        }

        return results;
    }

    @Override
    @GET
    @NoCache
    @Path("/{carId}/paths")
    @Produces({APPLICATION_JSON_UTF8, APPLICATION_SPREAD_SHEET})//"application/json; charset=UTF-8"
    @TypeHint(List.class)
    public List<PathWithAlarm> getPaths(@PathParam("carId") String carId,
                                        @QueryParam("start") DateTime start,
                                        @QueryParam("end") DateTime end,
                                        @Context SecurityContext securityContext,
                                        @Context Request req, @Context HttpServletResponse response) {
        List<PathWithAlarm> pathWithAlarms = pathService.caclPathWithAlarms(carId, start, end, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (RestUtil.getMediaType(req).isCompatible(APPLICATION_EXCEL_TYPE)) {
            final Phaser phaser = new Phaser();
            phaser.register();

            for (PathWithAlarm pathWithAlarm : pathWithAlarms) {
                for (final GpsPoint gpsPoint : pathWithAlarm.getGpsPoints()) {
                    phaser.register();
                    mes.submit(new Runnable() {
                        @Override
                        public void run() {
                            String key = keyPool.acquire();
                            if (key == null) {
                                return;
                            }
                            try {
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
            }
            phaser.arriveAndAwaitAdvance();
            TAzCarinfoEntity carEntity = vehicleRepository.getCarById(carId);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
            String filename = "paths_" + carEntity.getCphm() + "_" + formatter.print(start) + ".xlsx";
            try {
                response.addHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, "UTF-8") + "\"");
            } catch (UnsupportedEncodingException e) {

            }
        }
        return pathWithAlarms;
    }


    @Override
    @PUT
    @Path("/{carId}/depot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Car.class)
    public Car updateFleet(@PathParam("carId") String carId, Depot depot) {
        TAzCarinfoEntity entity = vehicleRepository.updateDepotOfCar(carId, depot);
        Response.ResponseBuilder builder;
        if (entity != null) {
            return fromEntity(entity);
        } else {
            throw new NotFoundException();
        }

    }

    @Override
    @GET
    @NoCache
    @Path("/{id}/singlePaths")
    @Produces(APPLICATION_JSON_UTF8)//"application/json; charset=UTF-8"
    public List<PathWithAlarm> getSinglePaths(@PathParam("id") String id,
                                              @QueryParam("start") DateTime start,
                                              @QueryParam("end") DateTime end) {

        List<TGpsAdjustedPath> adjustedEntities = gpsRepository.getAllGpsAdjustedPathByCarIdAndTime(id, start, end);
        List<PathWithAlarm> pathWithAlarms = new ArrayList<>();
        for (TGpsAdjustedPath adjustedEntity : adjustedEntities) {
            PathWithAlarm pathWithAlarm = new PathWithAlarm();
            GpsPoint gpsOriginPoint = new GpsPoint();
            gpsOriginPoint.setLat(adjustedEntity.getGpsOriginLat());
            gpsOriginPoint.setLng(adjustedEntity.getGpsOriginLng());
            GpsPoint gpsDestinationPoint = new GpsPoint();
            gpsDestinationPoint.setLat(adjustedEntity.getGpsDestinationLat());
            gpsDestinationPoint.setLng(adjustedEntity.getGpsDestinationLng());

            pathWithAlarm.setGpsPoints(asList(gpsOriginPoint, gpsDestinationPoint));

            String[] pathLatLng = adjustedEntity.getPath().split(";");

            List<GpsPoint> adjustedPoints = new ArrayList<>();
            for (String latLng : pathLatLng) {
                GpsPoint gpsPoint = new GpsPoint();
                gpsPoint.setLng(Double.valueOf(latLng.split(",")[0]));
                gpsPoint.setLat(Double.valueOf(latLng.split(",")[1]));
                adjustedPoints.add(gpsPoint);


            }
            pathWithAlarm.setAdjustedPoints(adjustedPoints);
            pathWithAlarm.setStart(adjustedEntity.getStart());
            pathWithAlarm.setEnd(adjustedEntity.getEnd());
            pathWithAlarms.add(pathWithAlarm);
            pathWithAlarm.setCar(fromEntity(adjustedEntity.getCar()));
        }
        return pathWithAlarms;

    }


    @Override
    @GET
    @NoCache
    @Path("/{id}/paths/{pathid}")
    @Produces(APPLICATION_JSON_UTF8)
    public PathWithAlarm getPath(@PathParam("id") String id, @PathParam("pathid") String pathid
    ) {
        return null;
    }


    @Override
    @GET
    @NoCache
    @Path("/{id}/gps")
    @Produces(APPLICATION_JSON_UTF8)
    public GpsPoint getGpsPoint(@PathParam("id") String id,
                                @QueryParam("now") String now,
                                @QueryParam("datetime") DateTime datetime,
                                @Context SecurityContext securityContext) {
        if (datetime == null) {
            datetime = DateTime.now();
        }

        return pathService.getGpsPointAtOrBeforTime(id, datetime, SecurityUtil.getPrincipalExtOrNull(securityContext));

    }

    @Override
    public List<GpsPoint> getGpsPoints(String id, DateTime start, DateTime end, @Context SecurityContext securityContext) {
        if (end == null) {
            end = DateTime.now();
        }
        if (start == null) {
            start = end.withTimeAtStartOfDay();
        }
        List<TGpsPointEntity> gpsEntites = gpsRepository.getGpsRecordByCarIdAndStartEnd(id, start, end, SecurityUtil.getPrincipalExtOrNull(securityContext));
        return pathService.getAllMovingGpsPoints(gpsEntites);
    }

    @Override
    public Response sendVoiceMessage(String id, VoiceMessage voiceMessage, @Context SecurityContext securityContext) {
        TAzCarinfoEntity carEntity = vehicleRepository.getCarById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (carEntity == null) {
            throw logger.entityNotFound(TAzCarinfoEntity.class, id);
        }
        if (StringUtils.isBlank(carEntity.getCphm())) {
            throw logger.invalidCar(id);
        }
        for (int i = 0; i < 3; i++) {       // TODO  delete on 2016-03-15 dependents on gps chen
            Map<String, String> result = gpsClient.broadCast(carEntity.getCphm(), voiceMessage.getMessage());
        }
        return Response.status(Response.Status.OK).build();
    }


}


