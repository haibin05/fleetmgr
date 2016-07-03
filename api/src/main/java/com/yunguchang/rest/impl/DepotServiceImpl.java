package com.yunguchang.rest.impl;

import com.yunguchang.data.AlarmRepository;
import com.yunguchang.data.DepotRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.Depot;
import com.yunguchang.model.common.ReturningDepotEvent;
import com.yunguchang.model.common.ReturningEventType;
import com.yunguchang.model.persistence.TAzCarinfoEntity;
import com.yunguchang.model.persistence.TBusReturningDepotEntity;
import com.yunguchang.model.persistence.TEmbeddedDepot;
import com.yunguchang.rest.DepotService;
import com.yunguchang.rest.SecurityUtil;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.joda.time.DateTime;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * 停车场管理 - 驻车点管理
 */
@RequestScoped
@Stateful

public class DepotServiceImpl implements DepotService {

    @Inject
    private DepotRepository depotRepository;

    @Inject
    private AlarmRepository alarmRepository;

    @Override@GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Depot> listAllDepot(@Context SecurityContext securityContext) {
        List<TEmbeddedDepot> results = depotRepository.listDepots(SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<Depot> depots=new ArrayList<>();
        for (TEmbeddedDepot embeddedDepot : results) {
            if (embeddedDepot==null){
                continue;
            }
            depots.add(EntityConvert.fromEntity(embeddedDepot));
        }
        //depotRepository.listAllDepot();
        return depots;
    }

    @Override@GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/carsInDepot")
    public List<ReturningDepotEvent> listAllCarsInDepot(@QueryParam("start") DateTime start, @QueryParam("end") DateTime end,@Context SecurityContext securityContext) {
        List<TBusReturningDepotEntity> returningDepotEntities = depotRepository.listReturningDepotByStartAndEnd(start, end,SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<ReturningDepotEvent> results = new ArrayList<>();
        for (TBusReturningDepotEntity returningDepotEntity : returningDepotEntities) {
            ReturningDepotEvent event = EntityConvert.fromEntity(returningDepotEntity);
            event.setReturningEventType(ReturningEventType.RETURN);
            results.add(event);
        }
        return results;
    }


    @Override@GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    @Path("/carsNotInDepot")
    public List<ReturningDepotEvent> listAllCarsNotInDepot(@QueryParam("date") DateTime date,@Context SecurityContext securityContext) {
        List<TAzCarinfoEntity> carEntites = depotRepository.listNoReturnByDate(date,SecurityUtil.getPrincipalExtOrNull(securityContext));
        List<ReturningDepotEvent> cars = new ArrayList<>();
        for (TAzCarinfoEntity carEntite : carEntites) {
            Car car = EntityConvert.fromEntity(carEntite);
            car.setDriver(EntityConvert.fromEntity(carEntite.getDriver()));
            ReturningDepotEvent event = new ReturningDepotEvent();
            event.setCar(car);
            event.setReturningEventType(ReturningEventType.NORETURN);
            event.setEventTime(date.withTime(21, 0, 0, 0));
            cars.add(event);
        }

        return cars;


    }
}
