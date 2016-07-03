package com.yunguchang.gps;

import com.yunguchang.model.persistence.TGpsPointEntity;
import com.yunguchang.model.common.Car;
import com.yunguchang.model.common.GpsPoint;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.Hibernate;
import org.joda.time.DateTime;
import org.joda.time.MutableDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 禕 on 2015/9/8.
 */

public class GpsUtil {

    public final static int POINTS_PER_MINUTE = 4;
    public final static int POINTS_PER_RECORD = 20;
    public final static int MINUTES_PER_RECORD = POINTS_PER_RECORD / POINTS_PER_MINUTE;
    public final static int SECONDS_PER_POINT = 60 / POINTS_PER_MINUTE;


    private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static double EARTH_RADIUS = 6378.137 * 1000;

    public static GpsPoint fromResponse(Map<String, String> response) {
        try {

            if (!response.get("Status").equals("103")) {
                return null;
            }
            GpsPoint gpsPoint = new GpsPoint();
            Car car = new Car();
            car.setBadge(response.get("VehicleNo"));
            gpsPoint.setLat(Double.parseDouble(response.get("Latitude")));
            gpsPoint.setLng(Double.parseDouble(response.get("Longitude")));
            gpsPoint.setSpeed(Double.parseDouble(response.get("Speed")));
            gpsPoint.setCar(car);
            gpsPoint.setSampleTime(formatter.parseDateTime(response.get("GPSTime")));
            return gpsPoint;
        } catch (Exception e) {
            return null;
        }

    }

    public static DateTime getPersistPointTime(DateTime dateTime) {
        int min = dateTime.getMinuteOfHour();
        MutableDateTime pointTime = new MutableDateTime(dateTime);
        pointTime.setMinuteOfHour(min / MINUTES_PER_RECORD * MINUTES_PER_RECORD);
        pointTime.setSecondOfMinute(0);
        pointTime.setMillisOfSecond(0);
        return pointTime.toDateTime();
    }

    public static int getPersistSection(DateTime dateTime) {
        int min = dateTime.getMinuteOfHour();
        int sec = dateTime.getSecondOfMinute();
        int section = min % MINUTES_PER_RECORD * POINTS_PER_MINUTE + sec / SECONDS_PER_POINT + 1;
        return section;
    }

    public static boolean isSameLocation(GpsPoint gpsPoint1, GpsPoint gpsPoint2) {
        if (gpsPoint1 == null && gpsPoint2 == null) {
            return true;
        }
        if (gpsPoint1 == null || gpsPoint2 == null) {
            return false;
        }

        double distance = getDistance(gpsPoint1.getLat(), gpsPoint1.getLng(), gpsPoint2.getLat(), gpsPoint2.getLng());
        //小于5米
        return distance < 5;
    }

    //这个函数值返回gps点位和关联车辆的id。车辆对象上的更深层测的对象需要再重新查询
    public static List<GpsPoint> fromPersistPointsWithShallowCarObject(TGpsPointEntity tGpsPointEntity) {
        List<GpsPoint> results = new ArrayList<GpsPoint>();
        if (tGpsPointEntity == null) {
            return results;
        }
        DateTime pointTime = tGpsPointEntity.getPointTime();
        try {
            for (int i = 0; i < POINTS_PER_RECORD; i++) {
                int section = i + 1;
                Double lat = (Double) PropertyUtils.getSimpleProperty(tGpsPointEntity, "lat" + section);
                Double lng = (Double) PropertyUtils.getSimpleProperty(tGpsPointEntity, "lng" + section);
                Double speed = (Double) PropertyUtils.getSimpleProperty(tGpsPointEntity, "speed" + section);
                DateTime sampleTime = (DateTime) PropertyUtils.getSimpleProperty(tGpsPointEntity, "sampleTime" + section);
                DateTime persistTime = pointTime.plusSeconds(SECONDS_PER_POINT * i);
                if (isValidGpsPoint(lat, lng, sampleTime)) {
                    GpsPoint gpsPoint = new GpsPoint();
                    if(tGpsPointEntity.getCar() != null) {
                        Car car = new Car();
                        car.setId(tGpsPointEntity.getCar().getId());
                        gpsPoint.setCar(car);
                    }
                    gpsPoint.setSpeed(speed);
                    gpsPoint.setLng(lng);
                    gpsPoint.setLat(lat);
                    gpsPoint.setPersistTime(persistTime);
                    gpsPoint.setSampleTime(sampleTime);
                    results.add(gpsPoint);
                } else {
                    results.add(null);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
        return results;
    }




    
    

    private static boolean isValidGpsPoint(Double lat, Double lng, DateTime sampleTime) {
        if (lat == null || lat == (0)) {
            return false;
        }
        if (lng == null || lng == (0)) {
            return false;
        }

        if (sampleTime == null) {
            return false;
        }

        return true;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算两个gps坐标之间的距离，单位米
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        return s;
    }




//    public static double getDistanceFromXtoY(double lat_a, double lng_a, double lat_b, double lng_b) {
//        double pk = (double) (180 / 3.14169);
//        double a1 = lat_a / pk;
//        double a2 = lng_a / pk;
//        double b1 = lat_b / pk;
//        double b2 = lng_b / pk;
//        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
//        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
//        double t3 = Math.sin(a1) * Math.sin(b1);
//        double tt = Math.acos(t1 + t2 + t3);
//        return EARTH_RADIUS * tt;
//    }

}
