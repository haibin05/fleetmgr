package com.yunguchang.schedule;

import com.yunguchang.model.common.Roles;
import com.yunguchang.model.common.User;
import com.yunguchang.sam.PrincipalExt;

/**
 * Created by haibin on 2016/3/9.
 */
public class ScheduleRunnerUtility {
    public static PrincipalExt getOneUserWithAdminRole() {
        PrincipalExt principalExt = new PrincipalExt();

        User admin = new User();
        admin.setRoles(Roles.ADMIN);
        admin.setEid("admin");
        admin.setUserId("admin");
        admin.setUserName("admin");
        principalExt.setUser(admin);
        return principalExt;
    }
}
