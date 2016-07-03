package com.yunguchang.alarm;

import com.yunguchang.data.*;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.drools.core.ObjectFilter;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.event.DebugAgendaEventListener;
import org.drools.core.event.DebugRuleRuntimeEventListener;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;
import org.kie.api.cdi.KSession;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import java.util.*;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 9/21/2015.
 */
@Singleton
@Startup
@Path("/rule")
public class RulesExecutor {

    @Inject
    @KSession("basicKSession")
    private KieSession kSession;

    @Inject
    @Fire
    private Event<AlarmEvent> alarmEventSource;


    @Inject
    @Clear
    private Event<AlarmEvent> clearAlarmEventSource;


    @Inject
    private Event<ReturningDepotEvent> returningDepotEventSource;


    @Inject
    private VehicleRepository vehicleRepository;


    @Inject
    private AlarmRepository alarmRepository;

    @Inject
    private DepotRepository depotRepository;


    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private GpsRepository gpsRepository;

    @Inject
    private Logger logger;


    @Inject
    @ConfigProperty(name = "garage.lng", defaultValue = "120.771598")
    private double garageLng;

    @Inject
    @ConfigProperty(name = "garage.lat", defaultValue = "30.805585")
    private double garageLat;

    @Inject
    private RuleBridge ruleBridge;

    @Resource(lookup = "java:jboss/ee/concurrency/executor/gps")
    private ManagedExecutorService mes;

    private boolean start = false;
    private boolean stop=false;
    private Set<FactHandle> carHandles = new HashSet<>();





    @PostConstruct
    public void startup() {
        kSession.addEventListener(new DebugAgendaEventListener());
        kSession.addEventListener(new DebugRuleRuntimeEventListener());
        kSession.setGlobal("alarmEventSource", alarmEventSource);
        kSession.setGlobal("clearAlarmEventSource", clearAlarmEventSource);
        kSession.setGlobal("returningDepotEventSource", returningDepotEventSource);
        kSession.setGlobal("garageLat", garageLat);
        kSession.setGlobal("garageLng", garageLng);
        kSession.setGlobal("ruleBridge", ruleBridge);
        initRuleEngine();
    }
    @Path("/stop")
    @GET
    public void stop(){
        kSession.halt();
    }

    @Path("/init")
    @GET
    public void initRuleEngine() {
        synchronized (this) {
//            if kSession.

            for (FactHandle carHandle : carHandles) {
                kSession.delete(carHandle);
            }
            carHandles.clear();

            Collection<FactHandle> handles = kSession.getFactHandles();
            for (FactHandle handle : handles) {
                Object fact = kSession.getObject(handle);
                if (fact instanceof AlarmEvent) {
                    AlarmEvent alarmEvent = (AlarmEvent) fact;
                    if (alarmEvent.getEnd() == null) {
                        alarmEvent.setEnd(alarmEvent.getStart().withTimeAtStartOfDay().plusDays(1));
                        clearAlarmEventSource.fire(alarmEvent);
                    }
                }
                kSession.delete(handle);
            }


            List<TAzJzRelaEntity> licenseClassVehicleMappings = vehicleRepository.listAllLicenceClassAndVehicleMappings();
            for (TAzJzRelaEntity licenseClassVehicleMapping : licenseClassVehicleMappings) {
                LicenseVehicleMapping mapping = EntityConvert.fromEntity(licenseClassVehicleMapping);
                kSession.insert(mapping);
            }

            refreshScheduleCar();
            DateTime todayStart = DateTime.now().withTimeAtStartOfDay();
            DateTime tomorrowStart = todayStart.plusDays(1);


            List<TBusAlarmEntity> alarmEntities = alarmRepository.getNoClearedAlarmByStartEnd(todayStart, tomorrowStart);
            for (TBusAlarmEntity alarmEntity : alarmEntities) {
                kSession.insert(EntityConvert.fromEntity(alarmEntity));
            }

            kSession.insert(kSession.getSessionClock());

            List<TAzCarinfoEntity> carEntites = vehicleRepository.listAllCarsWithGPS();
            for (TAzCarinfoEntity carEntite : carEntites) {
                Car car = EntityConvert.fromEntity(carEntite);
                FactHandle handle = kSession.insert(car);
                carHandles.add(handle);
            }





            if (!start) {
                start = true;
                logger.debug("submit drools fireUntilHalt task");
                mes.submit(new Runnable() {
                    @Override
                    public void run() {
                        while(!stop){
                            logger.debug("start drools");
                            try {
                                kSession.fireUntilHalt();
                            }catch (Exception e ) {
                                logger.ruleEngineExecutingError(e);
                            }
                            logger.restartRuleEngine();
                        }


                    }
                });
                logger.debug("submit successfully");
            }
        }
    }


    @Path("/refresh")
    @GET
    public void refreshScheduleCar() {
        synchronized (this) {
            Map<String, FactHandle> scheduleCarMap = new HashMap<>();


            Collection<FactHandle> handles = kSession.getFactHandles();
            for (FactHandle handle : handles) {

                Object fact = kSession.getObject(handle);
                if (fact instanceof ScheduleCar) {
                    scheduleCarMap.put(((ScheduleCar) fact).getId(), handle);
                }


            }

            DateTime todayStart = DateTime.now().withTimeAtStartOfDay();
            DateTime tomorrowStart = todayStart.plusDays(1);
            List<TBusScheduleRelaEntity> scheduleEntities = scheduleRepository.getSchedulesByStartAndEnd(todayStart, tomorrowStart);
            for (TBusScheduleRelaEntity scheduleEntity : scheduleEntities) {
                com.yunguchang.model.common.Schedule schedule = EntityConvert.fromEntityWithScheduleCars(scheduleEntity);
                for (ScheduleCar scheduleCar : schedule.getScheduleCars()) {
                    String scheduleCarId = scheduleCar.getId();
                    if (ScheduleStatus.CANCELED.equals(scheduleCar.getStatus()) ||
                            ScheduleStatus.NOT_IN_EFFECT.equals(scheduleCar.getStatus())) {
                        removeScheduleFact(scheduleCarMap, scheduleCarId);
                        continue;
                    }

                    if (scheduleCarMap.containsKey(scheduleCarId)) {
                        FactHandle factHandle = scheduleCarMap.get(scheduleCarId);
                        ScheduleCar factScheduleCar = (ScheduleCar) kSession.getObject(factHandle);
                        if (factScheduleCar.getStatus() != null && !factScheduleCar.getStatus().equals(scheduleCar.getStatus()) ||
                                factScheduleCar.getStart() != scheduleCar.getStart() ||
                                factScheduleCar.getDuration() != scheduleCar.getDuration()) {
                            kSession.update(factHandle, scheduleCar);
                        }

                        continue;
                    }
                    if (!scheduleCarMap.containsKey(scheduleCarId) &&
                            scheduleCar.getSchedule().getEnd().plusMinutes(30).isAfter(DateTime.now())) {
                        logger.insertScheduleCar(scheduleCar);
                        kSession.insert(scheduleCar);
                        continue;
                    }


                }
            }
        }
    }

    private void removeScheduleFact(Map<String, FactHandle> scheduleCarMap, String scheduleCarId) {
        if (scheduleCarMap.containsKey(scheduleCarId)) {
            FactHandle handle = scheduleCarMap.get(scheduleCarId);
            if (handle != null) {
                kSession.delete(handle);
            }
        }
    }

    @PreDestroy
    private void shutdown() {
        stop=true;
        if (kSession instanceof  InternalKnowledgeRuntime){
            ((InternalKnowledgeRuntime)kSession).setEndOperationListener(null);
        }
        kSession.halt();
        kSession.dispose();

    }


    @Asynchronous
    public void receiveGpsPoint(@Observes GpsPoint gpsPoint) {
        kSession.insert(gpsPoint);
    }

    @Asynchronous
    public void receiveFireAlarm(@Fire @Observes AlarmEvent alarmEvent) {

        if (alarmEvent.getAlarm()==null||
                alarmEvent.getCar()==null||
                alarmEvent.getStart()==null){
            logger.dropAlarm(alarmEvent);
            return;
        }
        TAzCarinfoEntity car = vehicleRepository.getCarById(alarmEvent.getCar().getId());
        if (car == null) {
            logger.dropAlarm(alarmEvent);
            return;
        }
        TBusAlarmEntity busAlarmEntity = new TBusAlarmEntity();
        busAlarmEntity.setStart(alarmEvent.getStart());
        busAlarmEntity.setEnd(alarmEvent.getEnd());
        busAlarmEntity.setCar(car);
        busAlarmEntity.setAlarm(alarmEvent.getAlarm().getId());
        GpsPoint gpsPoint = alarmEvent.getGpsPoint();
        if (gpsPoint != null) {
            busAlarmEntity.setGpsLat(gpsPoint.getLat());
            busAlarmEntity.setGpsLng(gpsPoint.getLng());
            busAlarmEntity.setGpsSpeed(gpsPoint.getSpeed());
            busAlarmEntity.setGpsSampleTime(gpsPoint.getSampleTime());
        }
        if (alarmRepository.getAlarmByExample(busAlarmEntity) == null) {
            alarmRepository.saveAlarm(busAlarmEntity);
            alarmEvent.setId(busAlarmEntity.getId());
        } else {
            logger.eventDuplicated(busAlarmEntity);
        }

    }
    @Asynchronous
    public void receiveClearAlarm(@Clear @Observes AlarmEvent alarmEvent) {
        if (alarmEvent.getId() != 0) {
            alarmRepository.updateAlarmEndTime(alarmEvent.getId(), alarmEvent.getEnd());
        }

    }
    @Asynchronous
    public void receiveReturningDepot(@Observes ReturningDepotEvent returningDepotEvent) {
        if (returningDepotEvent.getCar()==null ||
                returningDepotEvent.getEventTime()==null){
            logger.dropReturnEvent(returningDepotEvent);
            return;
        }
        TAzCarinfoEntity car = vehicleRepository.getCarById(returningDepotEvent.getCar().getId());
        if (car == null) {
            logger.dropReturnEvent(returningDepotEvent);
            return;
        }

        TBusReturningDepotEntity busReturningDepotEntity = new TBusReturningDepotEntity();
        busReturningDepotEntity.setCar(car);
        busReturningDepotEntity.setReturnTime(returningDepotEvent.getEventTime());
        if (depotRepository.getReturnEventBySample(busReturningDepotEntity) == null) {
            depotRepository.save(busReturningDepotEntity);
        } else {
            logger.eventDuplicated(busReturningDepotEntity);
        }


    }


    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Object> getAllFacts(final @QueryParam("class") String clazzName) {
        ObjectFilter filter = null;
        if (clazzName != null) {
            filter = new ObjectFilter() {
                @Override
                public boolean accept(Object object) {
                    return object.getClass().getSimpleName().equals(clazzName);
                }
            };
        } else {
            filter = new ObjectFilter() {
                @Override
                public boolean accept(Object object) {
                    return object.getClass().getPackage().getName().startsWith("com.yunguchang");
                }
            };
        }
        return new ArrayList(kSession.getObjects(filter));

    }


}
