package com.yunguchang.model.persistence;


import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;


/**
 * GPS 原始数据
 */
@Entity
@javax.persistence.Table(name = "t_gps_point",
        indexes = {
                @Index(name = "carid_pointtime", columnList = "CARID,pointTime"),
                @Index(name = "idx_carid_pointtime", columnList = "CARID,pointTime"),
                @Index(name = "idx_pointtime", columnList = "pointTime"),
                @Index(name = "idx_carid", columnList = "CARID"),
        }
)

public class TGpsPointEntity implements Serializable {
    /**
     * 自增加Id
     *
     * @return
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    /**
     * 车辆ID
     *
     * @return 车辆ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARID")
    private TAzCarinfoEntity car;

    /**
     * 存储时间点位 <br>
     * 每五分钟存储一条记录。每个点位时间是整点开始的每五分钟
     *
     * @return
     */
    private DateTime pointTime;
    private Double speed1;
    private Double lng1;
    private Double lat1;
    private DateTime sampleTime1;

    private Double speed2;
    private Double lng2;
    private Double lat2;
    private DateTime sampleTime2;
    private Double speed3;
    private Double lng3;
    private Double lat3;
    private DateTime sampleTime3;
    private Double speed4;
    private Double lng4;
    private Double lat4;
    private DateTime sampleTime4;
    private Double speed5;
    private Double lng5;
    private Double lat5;
    private DateTime sampleTime5;
    private Double speed6;
    private Double lng6;
    private Double lat6;
    private DateTime sampleTime6;
    private Double speed7;
    private Double lng7;
    private Double lat7;
    private DateTime sampleTime7;
    private Double speed8;
    private Double lng8;
    private Double lat8;
    private DateTime sampleTime8;
    private Double speed9;
    private Double lng9;
    private Double lat9;
    private DateTime sampleTime9;
    private Double speed10;
    private Double lng10;
    private Double lat10;
    private DateTime sampleTime10;
    private Double speed11;
    private Double lng11;
    private Double lat11;
    private DateTime sampleTime11;
    private Double speed12;
    private Double lng12;
    private Double lat12;
    private DateTime sampleTime12;
    private Double speed13;
    private Double lng13;
    private Double lat13;
    private DateTime sampleTime13;
    private Double speed14;
    private Double lng14;
    private Double lat14;
    private DateTime sampleTime14;
    private Double speed15;
    private Double lng15;
    private Double lat15;
    private DateTime sampleTime15;
    private Double speed16;
    private Double lng16;
    private Double lat16;
    private DateTime sampleTime16;
    private Double speed17;
    private Double lng17;
    private Double lat17;
    private DateTime sampleTime17;
    private Double speed18;
    private Double lng18;
    private Double lat18;
    private DateTime sampleTime18;
    private Double speed19;
    private Double lng19;
    private Double lat19;
    private DateTime sampleTime19;
    private Double speed20;
    private Double lng20;
    private Double lat20;
    private DateTime sampleTime20;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public TAzCarinfoEntity getCar() {
        return car;
    }

    public void setCar(TAzCarinfoEntity car) {
        this.car = car;
    }


    public DateTime getPointTime() {
        return pointTime;
    }

    public void setPointTime(DateTime pointTime) {
        this.pointTime = pointTime;
    }

    public Double getSpeed1() {
        return speed1;
    }

    public void setSpeed1(Double speed1) {
        this.speed1 = speed1;
    }

    public Double getLng1() {
        return lng1;
    }

    public void setLng1(Double lng1) {
        this.lng1 = lng1;
    }

    public Double getLat1() {
        return lat1;
    }

    public void setLat1(Double lat1) {
        this.lat1 = lat1;
    }

    public Double getSpeed2() {
        return speed2;
    }

    public void setSpeed2(Double speed2) {
        this.speed2 = speed2;
    }

    public Double getLng2() {
        return lng2;
    }

    public void setLng2(Double lng2) {
        this.lng2 = lng2;
    }

    public Double getLat2() {
        return lat2;
    }

    public void setLat2(Double lat2) {
        this.lat2 = lat2;
    }

    public Double getSpeed3() {
        return speed3;
    }

    public void setSpeed3(Double speed3) {
        this.speed3 = speed3;
    }

    public Double getLng3() {
        return lng3;
    }

    public void setLng3(Double lng3) {
        this.lng3 = lng3;
    }

    public Double getLat3() {
        return lat3;
    }

    public void setLat3(Double lat3) {
        this.lat3 = lat3;
    }

    public Double getSpeed4() {
        return speed4;
    }

    public void setSpeed4(Double speed4) {
        this.speed4 = speed4;
    }

    public Double getLng4() {
        return lng4;
    }

    public void setLng4(Double lng4) {
        this.lng4 = lng4;
    }

    public Double getLat4() {
        return lat4;
    }

    public void setLat4(Double lat4) {
        this.lat4 = lat4;
    }

    public Double getSpeed5() {
        return speed5;
    }

    public void setSpeed5(Double speed5) {
        this.speed5 = speed5;
    }

    public Double getLng5() {
        return lng5;
    }

    public void setLng5(Double lng5) {
        this.lng5 = lng5;
    }

    public Double getLat5() {
        return lat5;
    }

    public void setLat5(Double lat5) {
        this.lat5 = lat5;
    }

    public Double getSpeed6() {
        return speed6;
    }

    public void setSpeed6(Double speed6) {
        this.speed6 = speed6;
    }

    public Double getLng6() {
        return lng6;
    }

    public void setLng6(Double lng6) {
        this.lng6 = lng6;
    }

    public Double getLat6() {
        return lat6;
    }

    public void setLat6(Double lat6) {
        this.lat6 = lat6;
    }

    public Double getSpeed7() {
        return speed7;
    }

    public void setSpeed7(Double speed7) {
        this.speed7 = speed7;
    }

    public Double getLng7() {
        return lng7;
    }

    public void setLng7(Double lng7) {
        this.lng7 = lng7;
    }

    public Double getLat7() {
        return lat7;
    }

    public void setLat7(Double lat7) {
        this.lat7 = lat7;
    }

    public Double getSpeed8() {
        return speed8;
    }

    public void setSpeed8(Double speed8) {
        this.speed8 = speed8;
    }

    public Double getLng8() {
        return lng8;
    }

    public void setLng8(Double lng8) {
        this.lng8 = lng8;
    }

    public Double getLat8() {
        return lat8;
    }

    public void setLat8(Double lat8) {
        this.lat8 = lat8;
    }

    public Double getSpeed9() {
        return speed9;
    }

    public void setSpeed9(Double speed9) {
        this.speed9 = speed9;
    }

    public Double getLng9() {
        return lng9;
    }

    public void setLng9(Double lng9) {
        this.lng9 = lng9;
    }

    public Double getLat9() {
        return lat9;
    }

    public void setLat9(Double lat9) {
        this.lat9 = lat9;
    }

    public Double getSpeed10() {
        return speed10;
    }

    public void setSpeed10(Double speed10) {
        this.speed10 = speed10;
    }

    public Double getLng10() {
        return lng10;
    }

    public void setLng10(Double lng10) {
        this.lng10 = lng10;
    }

    public Double getLat10() {
        return lat10;
    }

    public void setLat10(Double lat10) {
        this.lat10 = lat10;
    }

    public Double getSpeed11() {
        return speed11;
    }

    public void setSpeed11(Double speed11) {
        this.speed11 = speed11;
    }

    public Double getLng11() {
        return lng11;
    }

    public void setLng11(Double lng11) {
        this.lng11 = lng11;
    }

    public Double getLat11() {
        return lat11;
    }

    public void setLat11(Double lat11) {
        this.lat11 = lat11;
    }

    public Double getSpeed12() {
        return speed12;
    }

    public void setSpeed12(Double speed12) {
        this.speed12 = speed12;
    }

    public Double getLng12() {
        return lng12;
    }

    public void setLng12(Double lng12) {
        this.lng12 = lng12;
    }

    public Double getLat12() {
        return lat12;
    }

    public void setLat12(Double lat12) {
        this.lat12 = lat12;
    }

    public Double getSpeed13() {
        return speed13;
    }

    public void setSpeed13(Double speed13) {
        this.speed13 = speed13;
    }

    public Double getLng13() {
        return lng13;
    }

    public void setLng13(Double lng13) {
        this.lng13 = lng13;
    }

    public Double getLat13() {
        return lat13;
    }

    public void setLat13(Double lat13) {
        this.lat13 = lat13;
    }

    public Double getSpeed14() {
        return speed14;
    }

    public void setSpeed14(Double speed14) {
        this.speed14 = speed14;
    }

    public Double getLng14() {
        return lng14;
    }

    public void setLng14(Double lng14) {
        this.lng14 = lng14;
    }

    public Double getLat14() {
        return lat14;
    }

    public void setLat14(Double lat14) {
        this.lat14 = lat14;
    }

    public Double getSpeed15() {
        return speed15;
    }

    public void setSpeed15(Double speed15) {
        this.speed15 = speed15;
    }

    public Double getLng15() {
        return lng15;
    }

    public void setLng15(Double lng15) {
        this.lng15 = lng15;
    }

    public Double getLat15() {
        return lat15;
    }

    public void setLat15(Double lat15) {
        this.lat15 = lat15;
    }

    public Double getSpeed16() {
        return speed16;
    }

    public void setSpeed16(Double speed16) {
        this.speed16 = speed16;
    }

    public Double getLng16() {
        return lng16;
    }

    public void setLng16(Double lng16) {
        this.lng16 = lng16;
    }

    public Double getLat16() {
        return lat16;
    }

    public void setLat16(Double lat16) {
        this.lat16 = lat16;
    }

    public Double getSpeed17() {
        return speed17;
    }

    public void setSpeed17(Double speed17) {
        this.speed17 = speed17;
    }

    public Double getLng17() {
        return lng17;
    }

    public void setLng17(Double lng17) {
        this.lng17 = lng17;
    }

    public Double getLat17() {
        return lat17;
    }

    public void setLat17(Double lat17) {
        this.lat17 = lat17;
    }

    public Double getSpeed18() {
        return speed18;
    }

    public void setSpeed18(Double speed18) {
        this.speed18 = speed18;
    }

    public Double getLng18() {
        return lng18;
    }

    public void setLng18(Double lng18) {
        this.lng18 = lng18;
    }

    public Double getLat18() {
        return lat18;
    }

    public void setLat18(Double lat18) {
        this.lat18 = lat18;
    }

    public Double getSpeed19() {
        return speed19;
    }

    public void setSpeed19(Double speed19) {
        this.speed19 = speed19;
    }

    public Double getLng19() {
        return lng19;
    }

    public void setLng19(Double lng19) {
        this.lng19 = lng19;
    }

    public Double getLat19() {
        return lat19;
    }

    public void setLat19(Double lat19) {
        this.lat19 = lat19;
    }

    public Double getSpeed20() {
        return speed20;
    }

    public void setSpeed20(Double speed20) {
        this.speed20 = speed20;
    }

    public Double getLng20() {
        return lng20;
    }

    public void setLng20(Double lng20) {
        this.lng20 = lng20;
    }

    public Double getLat20() {
        return lat20;
    }

    public void setLat20(Double lat20) {
        this.lat20 = lat20;
    }

    public DateTime getSampleTime1() {
        return sampleTime1;
    }

    public void setSampleTime1(DateTime sampleTime1) {
        this.sampleTime1 = sampleTime1;
    }

    public DateTime getSampleTime2() {
        return sampleTime2;
    }

    public void setSampleTime2(DateTime sampleTime2) {
        this.sampleTime2 = sampleTime2;
    }

    public DateTime getSampleTime3() {
        return sampleTime3;
    }

    public void setSampleTime3(DateTime sampleTime3) {
        this.sampleTime3 = sampleTime3;
    }

    public DateTime getSampleTime4() {
        return sampleTime4;
    }

    public void setSampleTime4(DateTime sampleTime4) {
        this.sampleTime4 = sampleTime4;
    }

    public DateTime getSampleTime5() {
        return sampleTime5;
    }

    public void setSampleTime5(DateTime sampleTime5) {
        this.sampleTime5 = sampleTime5;
    }

    public DateTime getSampleTime6() {
        return sampleTime6;
    }

    public void setSampleTime6(DateTime sampleTime6) {
        this.sampleTime6 = sampleTime6;
    }

    public DateTime getSampleTime7() {
        return sampleTime7;
    }

    public void setSampleTime7(DateTime sampleTime7) {
        this.sampleTime7 = sampleTime7;
    }

    public DateTime getSampleTime8() {
        return sampleTime8;
    }

    public void setSampleTime8(DateTime sampleTime8) {
        this.sampleTime8 = sampleTime8;
    }

    public DateTime getSampleTime9() {
        return sampleTime9;
    }

    public void setSampleTime9(DateTime sampleTime9) {
        this.sampleTime9 = sampleTime9;
    }

    public DateTime getSampleTime10() {
        return sampleTime10;
    }

    public void setSampleTime10(DateTime sampleTime10) {
        this.sampleTime10 = sampleTime10;
    }

    public DateTime getSampleTime11() {
        return sampleTime11;
    }

    public void setSampleTime11(DateTime sampleTime11) {
        this.sampleTime11 = sampleTime11;
    }

    public DateTime getSampleTime12() {
        return sampleTime12;
    }

    public void setSampleTime12(DateTime sampleTime12) {
        this.sampleTime12 = sampleTime12;
    }

    public DateTime getSampleTime13() {
        return sampleTime13;
    }

    public void setSampleTime13(DateTime sampleTime13) {
        this.sampleTime13 = sampleTime13;
    }

    public DateTime getSampleTime14() {
        return sampleTime14;
    }

    public void setSampleTime14(DateTime sampleTime14) {
        this.sampleTime14 = sampleTime14;
    }

    public DateTime getSampleTime15() {
        return sampleTime15;
    }

    public void setSampleTime15(DateTime sampleTime15) {
        this.sampleTime15 = sampleTime15;
    }

    public DateTime getSampleTime16() {
        return sampleTime16;
    }

    public void setSampleTime16(DateTime sampleTime16) {
        this.sampleTime16 = sampleTime16;
    }

    public DateTime getSampleTime17() {
        return sampleTime17;
    }

    public void setSampleTime17(DateTime sampleTime17) {
        this.sampleTime17 = sampleTime17;
    }

    public DateTime getSampleTime18() {
        return sampleTime18;
    }

    public void setSampleTime18(DateTime sampleTime18) {
        this.sampleTime18 = sampleTime18;
    }

    public DateTime getSampleTime19() {
        return sampleTime19;
    }

    public void setSampleTime19(DateTime sampleTime19) {
        this.sampleTime19 = sampleTime19;
    }

    public DateTime getSampleTime20() {
        return sampleTime20;
    }

    public void setSampleTime20(DateTime sampleTime20) {
        this.sampleTime20 = sampleTime20;
    }


}
