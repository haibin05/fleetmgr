package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sys_user_role",
        indexes = {
                @Index(name = "fk_hvfdmivmib0ofmg9rjss8vjj", columnList = "roleid", unique = false)
        }
)
public class TSysUserRoleEntity implements Serializable {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().replace("-", "");
    private String userid;
    private String roleid;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSysUserRoleEntity that = (TSysUserRoleEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userid != null ? !userid.equals(that.userid) : that.userid != null) return false;
        if (roleid != null ? !roleid.equals(that.roleid) : that.roleid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userid != null ? userid.hashCode() : 0);
        result = 31 * result + (roleid != null ? roleid.hashCode() : 0);
        return result;
    }
}
