package com.yunguchang.rest.impl;

import com.yunguchang.rest.TimerQueryService;
import org.jboss.resteasy.annotations.cache.NoCache;

import javax.annotation.Resource;
import javax.ejb.Stateful;
import javax.ejb.Timer;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.*;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by 禕 on 2015/9/9.
 */


@RequestScoped
@Stateful
public class TimerQueryServiceImpl implements TimerQueryService {
    @Resource
    javax.ejb.TimerService timerService;
    @Override@GET
    @NoCache
    @Produces(APPLICATION_JSON_UTF8)
    public List<Map> listAllTimers() {
        List<Map> list=new ArrayList<Map>();
        for (Timer timer: timerService.getAllTimers()){
            Map map=new HashMap();
            map.put("timer",timer.toString());
            map.put("nextTimeOut",timer.getNextTimeout());
            list.add(map);
        }
        return list;


    }

}
