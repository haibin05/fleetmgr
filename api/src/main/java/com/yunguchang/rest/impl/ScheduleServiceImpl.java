package com.yunguchang.rest.impl;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.data.*;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.OrderByParam;
import com.yunguchang.model.common.Schedule;
import com.yunguchang.model.persistence.TBusScheduleRelaEntity;
import com.yunguchang.model.persistence.TSyncDataStatusEntity;
import com.yunguchang.rest.ScheduleService;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sam.PrincipalExt;
import com.yunguchang.sharetome.MessageCreator;
import com.yunguchang.sharetome.ShareTomeEvent;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.sharetome.ShareTomeEvent.*;
import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 8/20/2015.
 */


@RequestScoped
@Stateful
public class ScheduleServiceImpl implements ScheduleService {
    @Inject
    private Logger logger;
    @Inject
    private ScheduleRepository scheduleRepository;
    @Inject
    private ApplicationRepository applicationRepository;
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;
    @Inject
    private UserRepository userRepository;
    @Inject
    private DriverRepository driverRepository;
    @Inject
    private Event<ShareTomeEvent> shareTomeEventEvent;
    @Inject
    private MessageCreator messageCreator;

    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Schedule> listAllSchedules(
            @QueryParam("driverId") String driverId,
            @QueryParam("carId") String carId,
            @QueryParam("scheduleCarId") String scheduleCarId,
            @QueryParam("$offset") Integer offset,
            @QueryParam("$limit") Integer limit,
            @QueryParam("$orderby") OrderByParam orderByParam,
            @Context SecurityContext securityContext) {
        List<TBusScheduleRelaEntity> scheduleEntities = scheduleRepository.getAllSchedules(
                driverId,
                carId,
                scheduleCarId,
                offset,
                limit,
                orderByParam,
                SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<Schedule> result = new ArrayList<>();
        for (TBusScheduleRelaEntity scheduleEntity : scheduleEntities) {
            result.add(EntityConvert.fromEntityWithScheduleCars(scheduleEntity));
        }
        return result;
    }


    @Override
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    public Schedule lookupScheduleById(@PathParam("id") String id, @Context SecurityContext securityContext) {
        TBusScheduleRelaEntity scheduleEntity = scheduleRepository.getScheduleById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (scheduleEntity == null) {
            throw logger.entityNotFound(TBusScheduleRelaEntity.class, id);
        }
        return EntityConvert.fromEntityWithScheduleCars(scheduleEntity);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public Schedule createSchedule(Schedule schedule, @Context SecurityContext securityContext) {
        TBusScheduleRelaEntity scheduleEntity = scheduleRepository.createSchedule(schedule, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<Builder> builderList = messageCreator.sendPostMessageToSnsByScheduleEntity(scheduleEntity, MessageCategory.APPLICATION_DISPATCH);
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }
        return EntityConvert.fromEntityWithScheduleCars(scheduleEntity);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public Schedule updateSchedule(String scheduleId, Schedule schedule, SecurityContext securityContext) {
        TBusScheduleRelaEntity oldScheduleEntity = scheduleRepository.getScheduleById(scheduleId, SecurityUtil.getPrincipalExtOrNull(securityContext));

        TBusScheduleRelaEntity newScheduleEntity = scheduleRepository.updateSchedules(scheduleId, schedule, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<Builder> builderList = messageCreator.sendPostMessageToSnsByMergeScheduleEntity(oldScheduleEntity, newScheduleEntity);
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }

        return EntityConvert.fromEntityWithScheduleCars(newScheduleEntity);
    }

    @Override
    @DELETE
    public Boolean deleteSchedule(String scheduleId, SecurityContext securityContext) {
        PrincipalExt principalExt = SecurityUtil.getPrincipalExtOrNull(securityContext);
        TBusScheduleRelaEntity scheduleEntity = scheduleRepository.getScheduleById(scheduleId, principalExt);

        scheduleRepository.deleteSchedule(scheduleId, principalExt);

        // SCHEDULE_DELETE#scheduleId#cancelType#cancelReason#userId
        syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.DELETED, "SCHEDULE_DELETE#" + scheduleId + "#" + scheduleEntity.getCanceltype() + "#" + scheduleEntity.getCancelseason() + "#" + principalExt.getUserIdOrNull());

        List<Builder> builderList = messageCreator.sendPostMessageToSnsByOldScheduleEntity(scheduleEntity);
        for (Builder builder : builderList) {
            shareTomeEventEvent.fire(builder.build());
        }

        return true;
    }

}
