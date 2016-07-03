package com.yunguchang.rest.impl;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.data.SyncDataStatusRepository;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.SyncEntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.TBusRecordinfoEntity;
import com.yunguchang.model.persistence.TBusScheduleCarEntity;
import com.yunguchang.model.persistence.TSyncDataStatusEntity;
import com.yunguchang.rest.ScheduleCarService;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sharetome.MessageCreator;
import com.yunguchang.sharetome.ShareTomeEvent;
import org.joda.time.DateTime;

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

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;
import static com.yunguchang.sharetome.ShareTomeEvent.*;

/**
 * Created by 禕 on 2015/10/25.
 */

@RequestScoped
@Stateful
public class ScheduleCarServiceImpl implements ScheduleCarService {
    @Inject
    private Logger logger;
    @Inject
    private ScheduleRepository  scheduleRepository;
    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;
    @Inject
    private Event<ShareTomeEvent> shareTomeEventEvent;
    @Inject
    private MessageCreator messageCreator;

    @Override
    @GET
    @Produces(APPLICATION_JSON_UTF8)
    public List<ScheduleCar> getScheduleCars(@QueryParam("start") DateTime start, @QueryParam("end") DateTime end, @Context SecurityContext securityContext){
        List<TBusScheduleCarEntity> scheduleCars = scheduleRepository.getAllScheduleCars(start, end, SecurityUtil.getPrincipalExtOrNull(securityContext));

        List<ScheduleCar> results=new ArrayList<>();
        for (TBusScheduleCarEntity scheduleCar : scheduleCars) {
            results.add(EntityConvert.fromEntity(scheduleCar));
        }
        return results;
    }

    @Override
    @GET
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public ScheduleCar getScheduleCarById(@PathParam("id") String id, @Context SecurityContext securityContext){
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (scheduleCarEntity == null) {
            ScheduleCar scheduleCar = new ScheduleCar();
            scheduleCar.setStatus(ScheduleStatus.CANCELED);
            return scheduleCar;
        }
        return EntityConvert.fromEntity(scheduleCarEntity);
    }

    @Override
    @PUT
    @Path("/{id}/record")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public Record updateScheduleCarRecord(@PathParam("id") String scheduleCarId, Record record, @Context SecurityContext securityContext){
        TBusRecordinfoEntity recordEntity = scheduleRepository.updateScheduleCarRecordWith(scheduleCarId, record, SecurityUtil.getPrincipalExtOrNull(securityContext));

        MessageCategory messageCategory = record.getEndMile() == null ? MessageCategory.SCHEDULE_START : MessageCategory.SCHEDULE_END;

        if (record.getEndMile() != null && record.getEndMile() > 0) {
            syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.RECORD, SyncEntityConvert.fromEntity(recordEntity));
        }

        List<Builder> builderList = messageCreator.sendPostMessageToSnsByRecordEntity(recordEntity, messageCategory);
        for(Builder builder : builderList){
            shareTomeEventEvent.fire(builder.build());
        }
        return EntityConvert.fromEntity(recordEntity);
    }

    @Override
    @GET
    @Path("/{id}/record")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public Record getScheduleCarRecord(@PathParam("id") String scheduleCarId, @Context SecurityContext securityContext){
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCar(scheduleCarId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if(scheduleCarEntity==null){
            throw logger.entityNotFound(TBusScheduleCarEntity.class,scheduleCarId);
        }
        TBusRecordinfoEntity recordEntity = scheduleRepository.getScheduleCarRecordByScheduleCarIdWithPermission(scheduleCarId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        Record record = EntityConvert.fromEntity(recordEntity);
        //如果有车辆调度单而没有行驶记录,在业务上,我们依旧可以认为存在一条全空的行驶记录.
        if (record==null){
            record=new Record();
        }

        return record;
    }

}
