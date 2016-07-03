package com.yunguchang.model.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gongy on 2015/11/10.
 */
public class Roles {
    private static Map<String,String> roleMapping=new HashMap<>();

    //司机
    public static String DRIVER="Driver";
    //车队长，片组长
    public static String SUPERVISOR ="Supervisor";
    //高层领导，经理
    public static String MANAGER="Manager";
    //用车申请人，协调人
    public static String COORDINATOR="Coordinator";
    //总调
    public static String CENTRAL_DISPATCHER="CentralDispatcher";
    //系统管理。用于数据接口
    public  static String ADMIN="Admin";
    //用车审核人
    public static String APPROVER="Approver";

    static {

        roleMapping.put("分队长", SUPERVISOR);
        roleMapping.put("片组长", SUPERVISOR);
        roleMapping.put("车队长", SUPERVISOR);
        roleMapping.put("厂长", MANAGER);
        roleMapping.put("经理", MANAGER);
        roleMapping.put("领导", MANAGER);
        roleMapping.put("用车申请人", COORDINATOR);
        roleMapping.put("用车审核人", APPROVER);
        roleMapping.put("总调度", CENTRAL_DISPATCHER);
        roleMapping.put("系统管理员", ADMIN);
    }
    public static String getSystemRole(String rawRole){
        return roleMapping.get(rawRole);
    }

}
