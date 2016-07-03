package com.yunguchang.rest.impl;

import com.webcohesion.enunciate.metadata.rs.TypeHint;
import com.yunguchang.data.*;
import com.yunguchang.logger.Logger;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.SyncEntityConvert;
import com.yunguchang.model.common.*;
import com.yunguchang.model.persistence.*;
import com.yunguchang.rest.ApplicationService;
import com.yunguchang.rest.SecurityUtil;
import com.yunguchang.sharetome.MessageCreator;
import com.yunguchang.sharetome.ShareTomeEvent;
import org.jboss.resteasy.annotations.cache.NoCache;
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
import static com.yunguchang.sharetome.ShareTomeEvent.MessageCategory;

/**
 * Created by gongy on 8/20/2015.
 * 用车申请
 */
@RequestScoped
@Stateful
public class ApplicationServiceImpl implements ApplicationService {
    @Inject
    private ScheduleRepository scheduleRepository;

    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private PassengerRepository passengerRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private Logger logger;

    @Inject
    private Event<ShareTomeEvent> shareTomeEventEvent;

    @Inject
    private MessageCreator messageCreator;

    @Inject
    private SyncDataStatusRepository syncDataStatusRepository;

    @Override
    @GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Application> listAllApplications(@QueryParam("coordinator") String coordinatorUserId,
                                                 @QueryParam("reasonType") String reasonType,
                                                 @QueryParam("status") String status,
                                                 @QueryParam("startBefore") DateTime startBefore,
                                                 @QueryParam("startAfter") DateTime startAfter,
                                                 @QueryParam("endBefore") DateTime endBefore,
                                                 @QueryParam("endAfter") DateTime endAfter,
                                                 @QueryParam("$offset") Integer offset,
                                                 @QueryParam("$limit") Integer limit,
                                                 @QueryParam("$orderby") OrderByParam orderByParam,
                                                 @Context SecurityContext securityContext) {
        List<TBusApplyinfoEntity> applicationEntities =
                applicationRepository.getAllApplications(coordinatorUserId, reasonType, status, startBefore, startAfter, endBefore, endAfter, offset, limit, orderByParam,
                        SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Application> results = new ArrayList<>();
        for (TBusApplyinfoEntity applicationEntity : applicationEntities) {
            results.add(EntityConvert.fromEntity(applicationEntity));
        }
        return results;
    }

    @Override
    @GET
    @NoCache
    @Path("/{id}")
    @Produces(APPLICATION_JSON_UTF8)
    public Application lookupApplicationById(@PathParam("id") String id, @Context SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(id, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, id);
        }
        return EntityConvert.fromEntity(applicationEntity);
    }

    @Override
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Application.class)
    public Application createApplication(Application application, @Context SecurityContext securityContext) {
        application.setStatus(new KeyValue("2"));       // 添加 审核流程，需要将原来  3 -> 2 （部门审批）
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.createApplication(EntityConvert.toEntity(application),
                SecurityUtil.getPrincipalExtOrNull(securityContext));

        // 对于 用车审核人 审核通过之后，需要发送给，申请人，审核人消息
        if (ApplyStatus.DEP_APPROVE.equals(ApplyStatus.valueOf(Integer.valueOf(application.getStatus().getKey())))) {
            shareTomeEventEvent.fire(
                    messageCreator.sendPostMessageToSnsByApplicationEntity(applyinfoEntity, MessageCategory.APPLICATION_CREATE, null).build()
            );
            syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.APPLY, SyncEntityConvert.fromApplyEntity(applyinfoEntity));
        }

        return EntityConvert.fromEntity(applyinfoEntity);
    }

    @Override
    public Application updateApplicationStatus(String applicationId, ApplicationStatus status, @Context SecurityContext securityContext) {
        if (ApplyStatus.DISPATCH_SUCCESS.equals(ApplyStatus.valueOf(Integer.valueOf(status.getStatus())))) {
            throw logger.invalidOperation("Update dispatched application");
        }

        if (ApplyStatus.APPLY.toStringValue().equals(status.getStatus()) || ApplyStatus.APPLY_REJECT.toStringValue().equals(status.getStatus())) {     // 同意 退回
            TBusApproveSugEntity approveSugInfo = new TBusApproveSugEntity();
            approveSugInfo.setSuggest(ApplyStatus.APPLY.toStringValue().equals(status.getStatus()) ? "01" : "02");
            TBusApproveSugEntity approveSugEntity = applicationRepository.approveApplication(applicationId, null, approveSugInfo, SecurityUtil.getPrincipalExtOrNull(securityContext));
            syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.APPROVE, SyncEntityConvert.toApproveInfo(approveSugEntity));
        } else {
            TBusApplyinfoEntity applyEntity = applicationRepository.updateApplicationStatus(applicationId, status.getStatus(), null, SecurityUtil.getPrincipalExtOrNull(securityContext));

            if(ApplyStatus.DISPATCH_REJECT.toStringValue().equals(status.getStatus())) {
                // [APPLY_REJECT#applyId#userId]
                syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.REJECTED, "APPLY_REJECT#" + applicationId + "#" + applyEntity.getUpdateuser());
            } else if(ApplyStatus.APPLY_CANCEL.toStringValue().equals(status.getStatus())) {
                // [APPLY_CANCEL#applyId]
                syncDataStatusRepository.saveLocalSyncDataStatus(TSyncDataStatusEntity.SyncDataType.APPLY_CANCELED, "APPLY_CANCEL#" + applicationId);
            }
        }
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));

        shareTomeEventEvent.fire(
                messageCreator.sendPostMessageToSnsByApplicationEntity(applyinfoEntity, null, status.getStatus()).build()
        );

        return EntityConvert.fromEntity(applyinfoEntity);
    }

    @Override
    public List<Car> listAllCandidateCars(String applicationIds, String keyword, Integer offset, Integer limit, @Context SecurityContext securityContext) {
        String[] application = applicationIds.split(",");
        List<TAzCarinfoEntity> carEntities = applicationRepository.listAllCandidateCars(application, keyword, offset, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Car> results = new ArrayList<Car>();
        for (TAzCarinfoEntity carEntity : carEntities) {
            results.add(EntityConvert.fromEntity(carEntity));
        }
        return results;
    }

    @Override
    public List<Driver> listAllCandidateDrivers(String applicationIds, String carId, String keyword, Integer offset, Integer limit, @Context SecurityContext securityContext) {
        String[] application = applicationIds.split(",");
        List<TRsDriverinfoEntity> driverEntities = applicationRepository.listAllCandidateDrivers(application, carId, keyword, offset, limit, SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Driver> results = new ArrayList<Driver>();
        for (TRsDriverinfoEntity driverEntity : driverEntities) {
            results.add(EntityConvert.fromEntity(driverEntity));
        }
        return results;
    }

    @Override
    @PUT
    @Path("/{id}/cars/{carId}/rateOfDriver")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public RateOfDriver updateRateOfDriver(@PathParam("id") String applicationId, @PathParam("carId") String carId, RateOfDriver rateOfDriver, @Context SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));

        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }

        TBusScheduleCarEntity sechduleCarEntity = scheduleRepository.getScheduleCarByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (sechduleCarEntity == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, "applicationId " + applicationId + "carId " + carId);
        }
        TBusEvaluateinfoEntity rateOfDriverEntity = applicationRepository.updateRateOfDriver(applicationId, carId, rateOfDriver, SecurityUtil.getPrincipalExtOrNull(securityContext));

        shareTomeEventEvent.fire(
                messageCreator.sendPostMessageToSnsByEvaluateEntity(rateOfDriverEntity, MessageCategory.APPLICATION_RATE_DRIVER).build()
        );

        return EntityConvert.fromEntityToRateOfDriver(rateOfDriverEntity);
    }

    @Override
    @GET
    @Path("/{id}/cars/{carId}/rateOfDriver")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public RateOfDriver getRateOfDriver(@PathParam("id") String applicationId, @PathParam("carId") String carId, @Context SecurityContext securityContext) {

        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }

        TBusScheduleCarEntity sechduleCarEntity = scheduleRepository.getScheduleCarByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (sechduleCarEntity == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, "applicationId " + applicationId + "carId " + carId);
        }

        TBusEvaluateinfoEntity rateOfDriverEntity = applicationRepository.getRateByByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        //即使数据库中没有rating记录
        //从业务上，依旧可以认为存在一条全空的rating
        if (rateOfDriverEntity == null) {
            return new RateOfDriver();
        } else {
            return EntityConvert.fromEntityToRateOfDriver(rateOfDriverEntity);
        }
    }

    @Override
    @PUT
    @Path("/{id}/cars/{carId}/rateOfPassenger")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public RateOfDriver updateRateOfPassenger(@PathParam("id") String applicationId, @PathParam("carId") String carId, RateOfPassenger rateOfPassenger, @Context SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }

        TBusScheduleCarEntity sechduleCarEntity = scheduleRepository.getScheduleCarByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (sechduleCarEntity == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, "applicationId " + applicationId + "carId " + carId);
        }
        TBusEvaluateinfoEntity rateEntity = applicationRepository.updateRateOfPassenger(applicationId, carId, rateOfPassenger, SecurityUtil.getPrincipalExtOrNull(securityContext));

        shareTomeEventEvent.fire(
                messageCreator.sendPostMessageToSnsByEvaluateEntity(rateEntity, MessageCategory.APPLICATION_RATE_PASSENGER).build()
        );

        return EntityConvert.fromEntityToRateOfDriver(rateEntity);
    }

    @Override
    @GET
    @Path("/{id}/cars/{carId}/rateOfPassenger")
    @Produces(APPLICATION_JSON_UTF8)
    @TypeHint(Schedule.class)
    public RateOfPassenger getRateOfPassenger(@PathParam("id") String applicationId, @PathParam("carId") String carId, @Context SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }

        TBusScheduleCarEntity sechduleCarEntity = scheduleRepository.getScheduleCarByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (sechduleCarEntity == null) {
            throw logger.entityNotFound(TBusScheduleCarEntity.class, "applicationId " + applicationId + "carId " + carId);
        }

        TBusEvaluateinfoEntity rateEntity = applicationRepository.getRateByByApplicationIdAndCarId(applicationId, carId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        //即使数据库中没有rating记录
        //从业务上，依旧可以认为存在一条全空的rating
        if (rateEntity == null) {
            return new RateOfPassenger();
        } else {
            return EntityConvert.fromEntityToRateOfPassenger(rateEntity);
        }
    }

    @Override
    public Application updateRateOfApplication(String applicationId, RateOfApplication rateOfApplication, @Context SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }
        TBusApplyinfoEntity applyinfoEntity = applicationRepository.updateRateOfApplication(applicationId, rateOfApplication, SecurityUtil.getPrincipalExtOrNull(securityContext));

        shareTomeEventEvent.fire(
                messageCreator.sendPostMessageToSnsByApplicationEntity(applyinfoEntity, MessageCategory.APPLICATION_RATE_APPLY, null).build()
        );

        return EntityConvert.fromEntity(applyinfoEntity);
    }

    @Override
    public Application getRateOfApplication(String applicationId, SecurityContext securityContext) {
        TBusApplyinfoEntity applicationEntity = applicationRepository.getApplicationById(applicationId, SecurityUtil.getPrincipalExtOrNull(securityContext));
        if (applicationEntity == null) {
            throw logger.entityNotFound(TBusApplyinfoEntity.class, applicationId);
        }
        return EntityConvert.fromEntity(applicationEntity);
    }

}
