package com.yunguchang.model.persistence;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@Table(name = "t_sys_permission")
public class TSysPermissionEntity implements Serializable {

    @Id
    @Column(name = "UUID")
    private String uuid;
    private String roleid;
    private String menuid;
    private String btnid;


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }


    public String getRoleid() {
        return roleid;
    }

    public void setRoleid(String roleid) {
        this.roleid = roleid;
    }


    public String getMenuid() {
        return menuid;
    }

    public void setMenuid(String menuid) {
        this.menuid = menuid;
    }


    public String getBtnid() {
        return btnid;
    }

    public void setBtnid(String btnid) {
        this.btnid = btnid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TSysPermissionEntity that = (TSysPermissionEntity) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        if (roleid != null ? !roleid.equals(that.roleid) : that.roleid != null) return false;
        if (menuid != null ? !menuid.equals(that.menuid) : that.menuid != null) return false;
        if (btnid != null ? !btnid.equals(that.btnid) : that.btnid != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (roleid != null ? roleid.hashCode() : 0);
        result = 31 * result + (menuid != null ? menuid.hashCode() : 0);
        result = 31 * result + (btnid != null ? btnid.hashCode() : 0);
        return result;
    }
}
