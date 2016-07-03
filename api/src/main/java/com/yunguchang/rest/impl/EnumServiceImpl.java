package com.yunguchang.rest.impl;

import com.yunguchang.data.SysDictRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.KeyValue;
import com.yunguchang.model.persistence.TSysDicEntity;
import com.yunguchang.rest.EnumService;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunguchang.restapp.JaxRsActivator.APPLICATION_JSON_UTF8;

/**
 * Created by gongy on 2015/10/20.
 */

@RequestScoped
@Stateful
public class EnumServiceImpl implements EnumService {

    @Inject
    private SysDictRepository sysDictRepository;

    @Override@GET
    @Path("/{enumType}")
    @Produces(APPLICATION_JSON_UTF8)
    public  List<KeyValue> getDriverById(@PathParam("enumType") String enumType) {
        List<TSysDicEntity> results = sysDictRepository.getEnumByType(enumType);
        List<KeyValue> enums=new ArrayList<>();;
        for (TSysDicEntity result : results){
            enums.add(EntityConvert.fromEntity(result));
        };
        return enums;
    }

}
