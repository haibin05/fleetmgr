package com.yunguchang.model.persistence;

import org.hibernate.annotations.*;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import java.io.Serializable;
import java.util.List;

/**
 * Created by gongy on 8/31/2015.
 */
@Entity
@javax.persistence.Table(name = "t_az_carinfo",
        indexes = {
                @Index(name = "fk_73qdfql4p4v4w70jgcdxuu8oy", columnList = "sscd", unique = false),
                @Index(name = "FK_7bjcgt0os0qfmamoevd02h9uc", columnList = "jsy", unique = false),
                @Index(name = "FK_6ap0pxxa3k3mgl1f9piappk4c", columnList = "sscd", unique = false),
        }
)
@FilterDefs(
        {
                @FilterDef(name = "filter_cars_fleet", parameters = {@ParamDef(name = "userId", type = "string")} ,defaultCondition = "SSCD like " +
                        "   (" +
                        "   select CONCAT(u.deptid, '%') from t_sys_user u where u.userid=:userId " +
                        "   )"
                ),
                @FilterDef(name = "filter_cars_all", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1=1"),
                @FilterDef(name = "filter_cars_driver", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="JSY = :userId"),
                @FilterDef(name = "filter_cars_none", parameters = {@ParamDef(name = "userId", type = "string")},defaultCondition="1 = 0")

        }
)

@Filters({
        @Filter(name = "filter_cars_fleet"),
        @Filter(name = "filter_cars_all"),
        @Filter(name = "filter_cars_driver"),
        @Filter(name = "filter_cars_none")
})

@Where(
        clause = "status=1"
)
public class TAzCarinfoEntity implements Serializable {

    /**
     * 主键
     */
    @Id
    @javax.persistence.Column(name = "ID")
    private String id;
    /**
     * 车牌号码
     */

    private String cphm;

//    private String htbh;
    /**
     * 车队
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SSCD")
    private TSysOrgEntity sysOrg;

    /**
     * 驾驶员
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JSY")
    private TRsDriverinfoEntity driver;

    /**
     * 车辆类型
     */
    private String cllx;

    @OneToMany(mappedBy = "car")
    private List<TGpsPointEntity> gpsPoints;

    @OneToMany(mappedBy = "car")
    private List<TGpsAdjustedPath> gpsAdjustedPaths;

//    private String clsbh;

    private String clzt;

    @OneToMany(mappedBy = "car")
    private List<TAzWzxxEntity> violations;

    @OneToMany(mappedBy = "car")
    private List<TBusReturningDepotEntity> returningDepots;

//    @OneToMany(mappedBy = "car")
//    private List<TQxServicinginfoEntity> reparings;


    @OneToMany(mappedBy = "car")
    private List<TBusScheduleCarEntity> scheduleCars;
    private Integer hdzk;
    private String gpsazqk;
    private String status;

    @Formula("( SELECT dic.datavalue FROM t_sys_dic dic where dic.classcode='CarType' and dic.datacode= cllx )")
    private String model;


//    @Formula("(select count(*)<>0 from t_qx_servicinginfo r where r.CLXXID=id and r.zt <> '归档')")
//    private Boolean repairing;

    @Formula("(" +
            "select count(*)<>0 from t_bus_schedule_car sc, t_bus_schedule_rela s " +
            " where " +
            " sc.CARID =id and sc.RELANO=s.UUID" +
            " and s.starttime<=now() and now()<=s.endtime" +
            " and s.status=1 " +
            ")")
    private Boolean inusing;

    @Embedded
    /**
     * 驻车点
     */
    private TEmbeddedDepot depot;

    /**
     * 维修状态改变时间
     */
    private DateTime repairingStateTime;


    /**
     * 维修状态
     */
    private Integer repairingState;

    /**
     * 违章次数
     */
    private Integer violationTimes;
    /**
     * 行驶证车型
     */
    private String xszcx;

    public Integer getViolationTimes() {
        return violationTimes;
    }

    public void setViolationTimes(Integer violationTimes) {
        this.violationTimes = violationTimes;
    }

    public TEmbeddedDepot getDepot() {
        return depot;
    }

    public void setDepot(TEmbeddedDepot depot) {
        this.depot = depot;
    }

    public List<TBusScheduleCarEntity> getScheduleCars() {
        return scheduleCars;
    }

    public void setScheduleCars(List<TBusScheduleCarEntity> scheduleCars) {
        this.scheduleCars = scheduleCars;
    }
//    private String clfjfzh;

//    private String bz;


//    private String syr;

//    private String djjg;

//    private Timestamp djrq;

//    private String jdcdjbh;

//    private String csys;


    //    private String clsb;
//
//    private String gcjk;
//
//    private String fdjh;
//
//    private String cpmc;
//
//    private String zxxs;
//
//    private String ljQ;
//
//    private String ljH;
//
//    private String lts;
//
//    private String ltgg;
//
//    private String gbthps;
//
//    private String zj;
//
//    private String zs;
//
//    private String wkccC;
//
//    private String wkccK;
//
//    private String wkccG;
//
//    private String hxnbccC;
//
//    private String hxnbccK;
//
//    private String hxnbccG;
//
//    private Integer zzl;

    public String getModel() {
        return model;
    }

    //    private Integer jsszk;
//
//    private String clhdfs;
//
//    private Integer hdzzl;
//
//    private Integer zqyzzl;
//
//    private String djsyxz;
//
//    private Timestamp clccrq;
//
//    private Timestamp fzrq;
//
//    private Integer pl;
//
//    private Integer gl;
//
//    private String fdjxh;
//
//    private String jdcdjzhbh;
//
//    private String rlzl;
//
//    private String xxzh;
//
//    private String bm;
//
//    private Timestamp rq;
//
//    private String clpzsydwmc;
//
//    private String clpzsydwcj;
//
//    private String clpzsydwxz;
//
//    private String clgdzcssdwcj;
//
//    private String clgdzcssdwxz;
//
//    private String gdzcbh;
//
//    private String clxz;
//    private String yyzy;
//    private String clpp;
//    private String pzfs;
//    private String clppxl;
//    private Integer gzjg;
//    private String zcgs;
//    private String fwdx;
//    private String defzr;
//    private Timestamp cdxszyxq;
//    private String ccs;
//    private String ck;
//    private String fzr;
//    private Integer xslc;
//    private Timestamp qzbfrq;
//    private String gfcx;
//    private String scdname;
//    private String hbdj;
//    private String hbcbz;
//    private String xcjlyazqk;

//    public List<TQxServicinginfoEntity> getReparings() {
//        return reparings;
//    }
//    private Timestamp xcjlyazrq;
//    private Timestamp gpsazrq;
//    private String clxh;
//    private Timestamp gzsj;
//    private String cjh;
//    private String cscc;
//    private String hxnbcc;
//    private String jzyq;
//    private String zcsx;

    public void setModel(String model) {
        this.model = model;
    }
//    private Timestamp xcnjsj;
//    private Integer jzdj;
//    private String sftzcl;

//    public void setReparings(List<TQxServicinginfoEntity> reparings) {
//        this.reparings = reparings;
//    }
//    private String insertuser;
//    private String insertorg;
//    private Timestamp insertdate;
//    private String updateuser;
//    private String updateorg;
//    private Timestamp updatedate;
//    private String zbh;
//    private String cpys;
//    private String gxjh;
//    private String jdaz;
//    private String etc;
//    private String bt;
//    private String qjd;
//    private String sjq;
//    private String mhq;
//    private String areacode;
//    private String dnclxz;

    /**
     * id
     *
     * @return
     */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 车牌号
     *
     * @return
     */

    public String getCphm() {
        return cphm;
    }

    public void setCphm(String cphm) {
        this.cphm = cphm;
    }


//    public String getHtbh() {
//        return htbh;
//    }
//
//    public void setHtbh(String htbh) {
//        this.htbh = htbh;
//    }


//    public Boolean getRepairing() {
//        return repairing;
//    }
//
//    public void setRepairing(Boolean repairing) {
//        this.repairing = repairing;
//    }

    public Boolean getInusing() {
        return inusing;
    }

    public void setInusing(Boolean inusing) {
        this.inusing = inusing;
    }

    public TSysOrgEntity getSysOrg() {
        return sysOrg;
    }


    public void setSysOrg(TSysOrgEntity sscd) {
        this.sysOrg = sscd;
    }


    public TRsDriverinfoEntity getDriver() {
        return driver;
    }

    public void setDriver(TRsDriverinfoEntity driver) {
        this.driver = driver;
    }


    public String getCllx() {
        return cllx;
    }

    public void setCllx(String cllx) {
        this.cllx = cllx;
    }


//    public String getClsbh() {
//        return clsbh;
//    }
//
//    public void setClsbh(String clsbh) {
//        this.clsbh = clsbh;
//    }
//

    public DateTime getRepairingStateTime() {
        return repairingStateTime;
    }

    public void setRepairingStateTime(DateTime repairingStateTime) {
        this.repairingStateTime = repairingStateTime;
    }

    public Integer getRepairingState() {
        return repairingState;
    }

    public void setRepairingState(Integer repairingState) {
        this.repairingState = repairingState;
    }

    public String getClzt() {
        return clzt;
    }

    public void setClzt(String clzt) {
        this.clzt = clzt;
    }
//

//    public String getClfjfzh() {
//        return clfjfzh;
//    }
//
//    public void setClfjfzh(String clfjfzh) {
//        this.clfjfzh = clfjfzh;
//    }
//

//    public String getBz() {
//        return bz;
//    }
//
//    public void setBz(String bz) {
//        this.bz = bz;
//    }
//

//    public String getSyr() {
//        return syr;
//    }
//
//    public void setSyr(String syr) {
//        this.syr = syr;
//    }
//

//    public String getDjjg() {
//        return djjg;
//    }
//
//    public void setDjjg(String djjg) {
//        this.djjg = djjg;
//    }
//

//    public Timestamp getDjrq() {
//        return djrq;
//    }
//
//    public void setDjrq(Timestamp djrq) {
//        this.djrq = djrq;
//    }
//

//    public String getJdcdjbh() {
//        return jdcdjbh;
//    }
//
//    public void setJdcdjbh(String jdcdjbh) {
//        this.jdcdjbh = jdcdjbh;
//    }
//

//    public String getCsys() {
//        return csys;
//    }
//
//    public void setCsys(String csys) {
//        this.csys = csys;
//    }
//

//    public String getClsb() {
//        return clsb;
//    }
//
//    public void setClsb(String clsb) {
//        this.clsb = clsb;
//    }
//

//    public String getGcjk() {
//        return gcjk;
//    }
//
//    public void setGcjk(String gcjk) {
//        this.gcjk = gcjk;
//    }
//

//    public String getFdjh() {
//        return fdjh;
//    }
//
//    public void setFdjh(String fdjh) {
//        this.fdjh = fdjh;
//    }
//

//    public String getCpmc() {
//        return cpmc;
//    }
//
//    public void setCpmc(String cpmc) {
//        this.cpmc = cpmc;
//    }
//

//    public String getZxxs() {
//        return zxxs;
//    }
//
//    public void setZxxs(String zxxs) {
//        this.zxxs = zxxs;
//    }
//

//    public String getLjQ() {
//        return ljQ;
//    }
//
//    public void setLjQ(String ljQ) {
//        this.ljQ = ljQ;
//    }
//

//    public String getLjH() {
//        return ljH;
//    }
//
//    public void setLjH(String ljH) {
//        this.ljH = ljH;
//    }
//

//    public String getLts() {
//        return lts;
//    }
//
//    public void setLts(String lts) {
//        this.lts = lts;
//    }
//

//    public String getLtgg() {
//        return ltgg;
//    }
//
//    public void setLtgg(String ltgg) {
//        this.ltgg = ltgg;
//    }
//

//    public String getGbthps() {
//        return gbthps;
//    }
//
//    public void setGbthps(String gbthps) {
//        this.gbthps = gbthps;
//    }
//

//    public String getZj() {
//        return zj;
//    }
//
//    public void setZj(String zj) {
//        this.zj = zj;
//    }
//

//    public String getZs() {
//        return zs;
//    }
//
//    public void setZs(String zs) {
//        this.zs = zs;
//    }
//

//    public String getWkccC() {
//        return wkccC;
//    }
//
//    public void setWkccC(String wkccC) {
//        this.wkccC = wkccC;
//    }
//

//    public String getWkccK() {
//        return wkccK;
//    }
//
//    public void setWkccK(String wkccK) {
//        this.wkccK = wkccK;
//    }
//

//    public String getWkccG() {
//        return wkccG;
//    }
//
//    public void setWkccG(String wkccG) {
//        this.wkccG = wkccG;
//    }
//

//    public String getHxnbccC() {
//        return hxnbccC;
//    }
//
//    public void setHxnbccC(String hxnbccC) {
//        this.hxnbccC = hxnbccC;
//    }
//

//    public String getHxnbccK() {
//        return hxnbccK;
//    }
//
//    public void setHxnbccK(String hxnbccK) {
//        this.hxnbccK = hxnbccK;
//    }
//

//    public String getHxnbccG() {
//        return hxnbccG;
//    }
//
//    public void setHxnbccG(String hxnbccG) {
//        this.hxnbccG = hxnbccG;
//    }
//

//    public Integer getZzl() {
//        return zzl;
//    }
//
//    public void setZzl(Integer zzl) {
//        this.zzl = zzl;
//    }


    public Integer getHdzk() {
        return hdzk;
    }

    public void setHdzk(Integer hdzk) {
        this.hdzk = hdzk;
    }


//    public Integer getJsszk() {
//        return jsszk;
//    }
//
//    public void setJsszk(Integer jsszk) {
//        this.jsszk = jsszk;
//    }
//

//    public String getClhdfs() {
//        return clhdfs;
//    }
//
//    public void setClhdfs(String clhdfs) {
//        this.clhdfs = clhdfs;
//    }
//

//    public Integer getHdzzl() {
//        return hdzzl;
//    }
//
//    public void setHdzzl(Integer hdzzl) {
//        this.hdzzl = hdzzl;
//    }
//

//    public Integer getZqyzzl() {
//        return zqyzzl;
//    }
//
//    public void setZqyzzl(Integer zqyzzl) {
//        this.zqyzzl = zqyzzl;
//    }
//

//    public String getDjsyxz() {
//        return djsyxz;
//    }
//
//    public void setDjsyxz(String djsyxz) {
//        this.djsyxz = djsyxz;
//    }
//

//    public Timestamp getClccrq() {
//        return clccrq;
//    }
//
//    public void setClccrq(Timestamp clccrq) {
//        this.clccrq = clccrq;
//    }
//

//    public Timestamp getFzrq() {
//        return fzrq;
//    }
//
//    public void setFzrq(Timestamp fzrq) {
//        this.fzrq = fzrq;
//    }
//

//    public Integer getPl() {
//        return pl;
//    }
//
//    public void setPl(Integer pl) {
//        this.pl = pl;
//    }
//

//    public Integer getGl() {
//        return gl;
//    }
//
//    public void setGl(Integer gl) {
//        this.gl = gl;
//    }
//

//    public String getFdjxh() {
//        return fdjxh;
//    }
//
//    public void setFdjxh(String fdjxh) {
//        this.fdjxh = fdjxh;
//    }
//

//    public String getJdcdjzhbh() {
//        return jdcdjzhbh;
//    }
//
//    public void setJdcdjzhbh(String jdcdjzhbh) {
//        this.jdcdjzhbh = jdcdjzhbh;
//    }
//

//    public String getRlzl() {
//        return rlzl;
//    }
//
//    public void setRlzl(String rlzl) {
//        this.rlzl = rlzl;
//    }
//

//    public String getXxzh() {
//        return xxzh;
//    }
//
//    public void setXxzh(String xxzh) {
//        this.xxzh = xxzh;
//    }
//

//    public String getBm() {
//        return bm;
//    }
//
//    public void setBm(String bm) {
//        this.bm = bm;
//    }
//

//    public Timestamp getRq() {
//        return rq;
//    }
//
//    public void setRq(Timestamp rq) {
//        this.rq = rq;
//    }
//

//    public String getClpzsydwmc() {
//        return clpzsydwmc;
//    }
//
//    public void setClpzsydwmc(String clpzsydwmc) {
//        this.clpzsydwmc = clpzsydwmc;
//    }
//

//    public String getClpzsydwcj() {
//        return clpzsydwcj;
//    }
//
//    public void setClpzsydwcj(String clpzsydwcj) {
//        this.clpzsydwcj = clpzsydwcj;
//    }
//

//    public String getClpzsydwxz() {
//        return clpzsydwxz;
//    }
//
//    public void setClpzsydwxz(String clpzsydwxz) {
//        this.clpzsydwxz = clpzsydwxz;
//    }
//

//    public String getClgdzcssdwcj() {
//        return clgdzcssdwcj;
//    }
//
//    public void setClgdzcssdwcj(String clgdzcssdwcj) {
//        this.clgdzcssdwcj = clgdzcssdwcj;
//    }
//

//    public String getClgdzcssdwxz() {
//        return clgdzcssdwxz;
//    }
//
//    public void setClgdzcssdwxz(String clgdzcssdwxz) {
//        this.clgdzcssdwxz = clgdzcssdwxz;
//    }
//

//    public String getGdzcbh() {
//        return gdzcbh;
//    }
//
//    public void setGdzcbh(String gdzcbh) {
//        this.gdzcbh = gdzcbh;
//    }
//

//    public String getClxz() {
//        return clxz;
//    }
//
//    public void setClxz(String clxz) {
//        this.clxz = clxz;
//    }
//

//    public String getYyzy() {
//        return yyzy;
//    }
//
//    public void setYyzy(String yyzy) {
//        this.yyzy = yyzy;
//    }
//

//    public String getClpp() {
//        return clpp;
//    }
//
//    public void setClpp(String clpp) {
//        this.clpp = clpp;
//    }
//

//    public String getPzfs() {
//        return pzfs;
//    }
//
//    public void setPzfs(String pzfs) {
//        this.pzfs = pzfs;
//    }
//

//    public String getClppxl() {
//        return clppxl;
//    }
//
//    public void setClppxl(String clppxl) {
//        this.clppxl = clppxl;
//    }
//

//    public Integer getGzjg() {
//        return gzjg;
//    }
//
//    public void setGzjg(Integer gzjg) {
//        this.gzjg = gzjg;
//    }
//

//    public String getZcgs() {
//        return zcgs;
//    }
//
//    public void setZcgs(String zcgs) {
//        this.zcgs = zcgs;
//    }
//

//    public String getFwdx() {
//        return fwdx;
//    }
//
//    public void setFwdx(String fwdx) {
//        this.fwdx = fwdx;
//    }
//

//    public String getDefzr() {
//        return defzr;
//    }
//
//    public void setDefzr(String defzr) {
//        this.defzr = defzr;
//    }
//

//    public Timestamp getCdxszyxq() {
//        return cdxszyxq;
//    }
//
//    public void setCdxszyxq(Timestamp cdxszyxq) {
//        this.cdxszyxq = cdxszyxq;
//    }
//

//    public String getCcs() {
//        return ccs;
//    }
//
//    public void setCcs(String ccs) {
//        this.ccs = ccs;
//    }
//

//    public String getCk() {
//        return ck;
//    }
//
//    public void setCk(String ck) {
//        this.ck = ck;
//    }
//

//    public String getFzr() {
//        return fzr;
//    }
//
//    public void setFzr(String fzr) {
//        this.fzr = fzr;
//    }
//

//    public Integer getXslc() {
//        return xslc;
//    }
//
//    public void setXslc(Integer xslc) {
//        this.xslc = xslc;
//    }
//

//    public Timestamp getQzbfrq() {
//        return qzbfrq;
//    }
//
//    public void setQzbfrq(Timestamp qzbfrq) {
//        this.qzbfrq = qzbfrq;
//    }
//

//    public String getGfcx() {
//        return gfcx;
//    }
//
//    public void setGfcx(String gfcx) {
//        this.gfcx = gfcx;
//    }
//

//    public String getScdname() {
//        return scdname;
//    }
//
//    public void setScdname(String scdname) {
//        this.scdname = scdname;
//    }
//

//    public String getHbdj() {
//        return hbdj;
//    }
//
//    public void setHbdj(String hbdj) {
//        this.hbdj = hbdj;
//    }
//

//    public String getHbcbz() {
//        return hbcbz;
//    }
//
//    public void setHbcbz(String hbcbz) {
//        this.hbcbz = hbcbz;
//    }
//

//    public String getXcjlyazqk() {
//        return xcjlyazqk;
//    }
//
//    public void setXcjlyazqk(String xcjlyazqk) {
//        this.xcjlyazqk = xcjlyazqk;
//    }


    public String getGpsazqk() {
        return gpsazqk;
    }

    public void setGpsazqk(String gpsazqk) {
        this.gpsazqk = gpsazqk;
    }


//    public Timestamp getXcjlyazrq() {
//        return xcjlyazrq;
//    }
//
//    public void setXcjlyazrq(Timestamp xcjlyazrq) {
//        this.xcjlyazrq = xcjlyazrq;
//    }
//

//    public Timestamp getGpsazrq() {
//        return gpsazrq;
//    }
//
//    public void setGpsazrq(Timestamp gpsazrq) {
//        this.gpsazrq = gpsazrq;
//    }
//

//    public String getClxh() {
//        return clxh;
//    }
//
//    public void setClxh(String clxh) {
//        this.clxh = clxh;
//    }
//

//    public Timestamp getGzsj() {
//        return gzsj;
//    }
//
//    public void setGzsj(Timestamp gzsj) {
//        this.gzsj = gzsj;
//    }
//

//    public String getCjh() {
//        return cjh;
//    }
//
//    public void setCjh(String cjh) {
//        this.cjh = cjh;
//    }
//

//    public String getCscc() {
//        return cscc;
//    }
//
//    public void setCscc(String cscc) {
//        this.cscc = cscc;
//    }
//

//    public String getHxnbcc() {
//        return hxnbcc;
//    }
//
//    public void setHxnbcc(String hxnbcc) {
//        this.hxnbcc = hxnbcc;
//    }
//

//    public String getJzyq() {
//        return jzyq;
//    }
//
//    public void setJzyq(String jzyq) {
//        this.jzyq = jzyq;
//    }
//

    //    public String getZcsx() {
//        return zcsx;
//    }
//
//    public void setZcsx(String zcsx) {
//        this.zcsx = zcsx;
//    }
//
    public String getXszcx() {
        return xszcx;
    }

    public void setXszcx(String xszcx) {
        this.xszcx = xszcx;
    }
//

//    public Timestamp getXcnjsj() {
//        return xcnjsj;
//    }
//
//    public void setXcnjsj(Timestamp xcnjsj) {
//        this.xcnjsj = xcnjsj;
//    }
//

//    public Integer getJzdj() {
//        return jzdj;
//    }
//
//    public void setJzdj(Integer jzdj) {
//        this.jzdj = jzdj;
//    }
//

    //    public String getSftzcl() {
//        return sftzcl;
//    }
//
//    public void setSftzcl(String sftzcl) {
//        this.sftzcl = sftzcl;
//    }
//
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
//

//    public String getInsertuser() {
//        return insertuser;
//    }
//
//    public void setInsertuser(String insertuser) {
//        this.insertuser = insertuser;
//    }
//

//    public String getInsertorg() {
//        return insertorg;
//    }
//
//    public void setInsertorg(String insertorg) {
//        this.insertorg = insertorg;
//    }
//

//    public Timestamp getInsertdate() {
//        return insertdate;
//    }
//
//    public void setInsertdate(Timestamp insertdate) {
//        this.insertdate = insertdate;
//    }
//

//    public String getUpdateuser() {
//        return updateuser;
//    }
//
//    public void setUpdateuser(String updateuser) {
//        this.updateuser = updateuser;
//    }
//

//    public String getUpdateorg() {
//        return updateorg;
//    }
//
//    public void setUpdateorg(String updateorg) {
//        this.updateorg = updateorg;
//    }
//

//    public Timestamp getUpdatedate() {
//        return updatedate;
//    }
//
//    public void setUpdatedate(Timestamp updatedate) {
//        this.updatedate = updatedate;
//    }
//

//    public String getZbh() {
//        return zbh;
//    }
//
//    public void setZbh(String zbh) {
//        this.zbh = zbh;
//    }
//

//    public String getCpys() {
//        return cpys;
//    }
//
//    public void setCpys(String cpys) {
//        this.cpys = cpys;
//    }
//

//    public String getGxjh() {
//        return gxjh;
//    }
//
//    public void setGxjh(String gxjh) {
//        this.gxjh = gxjh;
//    }
//

//    public String getJdaz() {
//        return jdaz;
//    }
//
//    public void setJdaz(String jdaz) {
//        this.jdaz = jdaz;
//    }
//

//    public String getEtc() {
//        return etc;
//    }
//
//    public void setEtc(String etc) {
//        this.etc = etc;
//    }
//

//    public String getBt() {
//        return bt;
//    }
//
//    public void setBt(String bt) {
//        this.bt = bt;
//    }
//

//    public String getQjd() {
//        return qjd;
//    }
//
//    public void setQjd(String qjd) {
//        this.qjd = qjd;
//    }
//

//    public String getSjq() {
//        return sjq;
//    }
//
//    public void setSjq(String sjq) {
//        this.sjq = sjq;
//    }
//

//    public String getMhq() {
//        return mhq;
//    }
//
//    public void setMhq(String mhq) {
//        this.mhq = mhq;
//    }
//

//    public String getAreacode() {
//        return areacode;
//    }
//
//    public void setAreacode(String areacode) {
//        this.areacode = areacode;
//    }
//

//    public String getDnclxz() {
//        return dnclxz;
//    }
//
//    public void setDnclxz(String dnclxz) {
//        this.dnclxz = dnclxz;
//    }


    public List<TGpsPointEntity> getGpsPoints() {
        return gpsPoints;
    }

    public void setGpsPoints(List<TGpsPointEntity> gpsPoints) {
        this.gpsPoints = gpsPoints;
    }

    public List<TGpsAdjustedPath> getGpsAdjustedPaths() {
        return gpsAdjustedPaths;
    }

    public void setGpsAdjustedPaths(List<TGpsAdjustedPath> gpsAdjustedPaths) {
        this.gpsAdjustedPaths = gpsAdjustedPaths;
    }

    public List<TAzWzxxEntity> getViolations() {
        return violations;
    }

    public void setViolations(List<TAzWzxxEntity> violations) {
        this.violations = violations;
    }

    public List<TBusReturningDepotEntity> getReturningDepots() {
        return returningDepots;
    }

    public void setReturningDepots(List<TBusReturningDepotEntity> returningDepots) {
        this.returningDepots = returningDepots;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TAzCarinfoEntity that = (TAzCarinfoEntity) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (cphm != null ? !cphm.equals(that.cphm) : that.cphm != null) return false;
        if (sysOrg != null ? !sysOrg.equals(that.sysOrg) : that.sysOrg != null) return false;
        if (driver != null ? !driver.equals(that.driver) : that.driver != null) return false;
        if (cllx != null ? !cllx.equals(that.cllx) : that.cllx != null) return false;
        if (gpsAdjustedPaths != null ? !gpsAdjustedPaths.equals(that.gpsAdjustedPaths) : that.gpsAdjustedPaths != null)
            return false;
        if (clzt != null ? !clzt.equals(that.clzt) : that.clzt != null) return false;
        if (violations != null ? !violations.equals(that.violations) : that.violations != null) return false;
        if (returningDepots != null ? !returningDepots.equals(that.returningDepots) : that.returningDepots != null)
            return false;
        if (scheduleCars != null ? !scheduleCars.equals(that.scheduleCars) : that.scheduleCars != null) return false;
        if (hdzk != null ? !hdzk.equals(that.hdzk) : that.hdzk != null) return false;
        if (gpsazqk != null ? !gpsazqk.equals(that.gpsazqk) : that.gpsazqk != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (model != null ? !model.equals(that.model) : that.model != null) return false;
        if (inusing != null ? !inusing.equals(that.inusing) : that.inusing != null) return false;
        if (depot != null ? !depot.equals(that.depot) : that.depot != null) return false;
        if (repairingStateTime != null ? !repairingStateTime.equals(that.repairingStateTime) : that.repairingStateTime != null)
            return false;
        if (repairingState != null ? !repairingState.equals(that.repairingState) : that.repairingState != null)
            return false;
        return !(xszcx != null ? !xszcx.equals(that.xszcx) : that.xszcx != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (cphm != null ? cphm.hashCode() : 0);
        result = 31 * result + (sysOrg != null ? sysOrg.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (cllx != null ? cllx.hashCode() : 0);
        result = 31 * result + (gpsAdjustedPaths != null ? gpsAdjustedPaths.hashCode() : 0);
        result = 31 * result + (clzt != null ? clzt.hashCode() : 0);
        result = 31 * result + (violations != null ? violations.hashCode() : 0);
        result = 31 * result + (returningDepots != null ? returningDepots.hashCode() : 0);
        result = 31 * result + (scheduleCars != null ? scheduleCars.hashCode() : 0);
        result = 31 * result + (hdzk != null ? hdzk.hashCode() : 0);
        result = 31 * result + (gpsazqk != null ? gpsazqk.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (model != null ? model.hashCode() : 0);
        result = 31 * result + (inusing != null ? inusing.hashCode() : 0);
        result = 31 * result + (depot != null ? depot.hashCode() : 0);
        result = 31 * result + (repairingStateTime != null ? repairingStateTime.hashCode() : 0);
        result = 31 * result + (repairingState != null ? repairingState.hashCode() : 0);
        result = 31 * result + (xszcx != null ? xszcx.hashCode() : 0);
        return result;
    }
}
