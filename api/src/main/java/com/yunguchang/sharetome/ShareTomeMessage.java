package com.yunguchang.sharetome;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yunguchang.model.common.ReasonType;
import com.yunguchang.model.common.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gongy on 2015/11/17.
 */
public class ShareTomeMessage {
    public static class PostTarget {
        private String id;
        @JsonProperty("isGroup")
        private String group = "unknown";

        private String nickName;

        User.UserType userType = User.UserType.DRIVER;

        public PostTarget() {
        }

        public PostTarget(User.UserType userType) {
            this.userType = userType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String isGroup() {
            return group;
        }

        public void setGroup(String isGroup) {
            this.group = isGroup;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public boolean isCoordinator() {
            return this.userType.equals(User.UserType.COORDINATOR);
        }

        public boolean isAuditor() {
            return this.userType.equals(User.UserType.AUDITOR);
        }

        public boolean isDispatcher() {
            return this.userType.equals(User.UserType.DISPATCHER);
        }

        public boolean isDriver() {
            return this.userType.equals(User.UserType.DRIVER);
        }
    }

    public static class ApplyInfo {
        String applyId;
        String startPoint;      // 起点
        String endPoint;        // 目的地
        Date startTime;         // 开始时间
        Date endTime;           // 结束时间
        String mainUser;     // 用车负责人
        String mainUserPhone;     // 用车负责人 电话
        Integer peopleNum;     // 乘客人数
        User coordinator;     // 审核人
        ReasonType reason;      // 原因
        Boolean irregular;      // 申请单是否规范
        String irregularReson;      // 申请单是否规范

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getStartPoint() {
            return startPoint;
        }

        public void setStartPoint(String startPoint) {
            this.startPoint = startPoint;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public String getMainUser() {
            return mainUser;
        }

        public void setMainUser(String mainUser) {
            this.mainUser = mainUser;
        }

        public String getMainUserPhone() {
            return mainUserPhone;
        }

        public void setMainUserPhone(String mainUserPhone) {
            this.mainUserPhone = mainUserPhone;
        }

        public Integer getPeopleNum() {
            return peopleNum;
        }

        public void setPeopleNum(Integer peopleNum) {
            this.peopleNum = peopleNum;
        }

        public ReasonType getReason() {
            return reason;
        }

        public void setReason(ReasonType reason) {
            this.reason = reason;
        }

        public User getCoordinator() {
            return coordinator;
        }

        public void setCoordinator(User coordinator) {
            this.coordinator = coordinator;
        }

        public Boolean getIrregular() {
            return irregular;
        }

        public void setIrregular(Boolean irregular) {
            this.irregular = irregular;
        }

        public String getIrregularReson() {
            return irregularReson;
        }

        public void setIrregularReson(String irregularReson) {
            this.irregularReson = irregularReson;
        }
    }

    public static class ScheduleInfo {
        String scheduleId;
        String startPoint;      // 起点
        Date startTime;
        String endPoint;        // 目的地
        Date endTime;
        List<ApplyInfo> applyInfoList;
        List<ScheduleCarInfo> scheduleCarList;
        List<EvaluateInfo> evaluateInfo;

        public String getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
        }

        public String getStartPoint() {
            return startPoint;
        }

        public void setStartPoint(String startPoint) {
            this.startPoint = startPoint;
        }

        public String getEndPoint() {
            return endPoint;
        }

        public void setEndPoint(String endPoint) {
            this.endPoint = endPoint;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }

        public List<ApplyInfo> getApplyInfoList() {
            return applyInfoList;
        }

        public void setApplyInfoList(List<ApplyInfo> applyInfoList) {
            this.applyInfoList = applyInfoList;
        }

        public List<ScheduleCarInfo> getScheduleCarList() {
            return scheduleCarList;
        }

        public void setScheduleCarList(List<ScheduleCarInfo> scheduleCarList) {
            this.scheduleCarList = scheduleCarList;
        }

        public List<EvaluateInfo> getEvaluateInfo() {
            return evaluateInfo;
        }

        public void setEvaluateInfo(List<EvaluateInfo> evaluateInfo) {
            this.evaluateInfo = evaluateInfo;
        }
    }

    public static class ScheduleCarInfo {
        String scheduleCarId;
        String carId;
        String badge;
        String driverId;  // 驾驶员
        String driverName;  // 驾驶员
        String driverPhone;  // 驾驶员
        String driverMobile;  // 驾驶员
        Double startMile;
        Date startTime;
        Double endMile;
        Date endTime;

        public String getScheduleCarId() {
            return scheduleCarId;
        }

        public void setScheduleCarId(String scheduleCarId) {
            this.scheduleCarId = scheduleCarId;
        }

        public String getCarId() {
            return carId;
        }

        public void setCarId(String carId) {
            this.carId = carId;
        }

        public String getBadge() {
            return badge;
        }

        public void setBadge(String badge) {
            this.badge = badge;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getDriverPhone() {
            return driverPhone;
        }

        public void setDriverPhone(String driverPhone) {
            this.driverPhone = driverPhone;
        }

        public String getDriverMobile() {
            return driverMobile;
        }

        public void setDriverMobile(String driverMobile) {
            this.driverMobile = driverMobile;
        }

        public Double getStartMile() {
            return startMile;
        }

        public void setStartMile(Double startMile) {
            this.startMile = startMile;
        }

        public Date getStartTime() {
            return startTime;
        }

        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }

        public Double getEndMile() {
            return endMile;
        }

        public void setEndMile(Double endMile) {
            this.endMile = endMile;
        }

        public Date getEndTime() {
            return endTime;
        }

        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
    }

    public static class EvaluateInfo {
        String applyId;
        String scheduleId;
        String carId;
        String cphm;
        String driverId;
        String driverName;
        String fleetName;
        String passengerName;
        Integer carScore;        // 评分
        Integer driverScore;        // 评分
        Integer passengerScore;     // 评分
        Integer fleetScore;         // 评分
        String carReason;        // 评价
        String driverReason;        // 评价
        String passengerReason;     // 评价
        String fleetReason;         // 评价

        public String getApplyId() {
            return applyId;
        }

        public void setApplyId(String applyId) {
            this.applyId = applyId;
        }

        public String getScheduleId() {
            return scheduleId;
        }

        public void setScheduleId(String scheduleId) {
            this.scheduleId = scheduleId;
        }

        public Integer getCarScore() {
            return carScore;
        }

        public void setCarScore(Integer carScore) {
            this.carScore = carScore;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getPassengerName() {
            return passengerName;
        }

        public void setPassengerName(String passengerName) {
            this.passengerName = passengerName;
        }

        public String getFleetName() {
            return fleetName;
        }

        public void setFleetName(String fleetName) {
            this.fleetName = fleetName;
        }

        public Integer getDriverScore() {
            return driverScore;
        }

        public void setDriverScore(Integer driverScore) {
            this.driverScore = driverScore;
        }

        public Integer getPassengerScore() {
            return passengerScore;
        }

        public void setPassengerScore(Integer passengerScore) {
            this.passengerScore = passengerScore;
        }

        public Integer getFleetScore() {
            return fleetScore;
        }

        public void setFleetScore(Integer fleetScore) {
            this.fleetScore = fleetScore;
        }

        public String getCarReason() {
            return carReason;
        }

        public void setCarReason(String carReason) {
            this.carReason = carReason;
        }

        public String getDriverReason() {
            return driverReason;
        }

        public void setDriverReason(String driverReason) {
            this.driverReason = driverReason;
        }

        public String getPassengerReason() {
            return passengerReason;
        }

        public void setPassengerReason(String passengerReason) {
            this.passengerReason = passengerReason;
        }

        public String getFleetReason() {
            return fleetReason;
        }

        public void setFleetReason(String fleetReason) {
            this.fleetReason = fleetReason;
        }

        public String getCarId() {
            return carId;
        }

        public void setCarId(String carId) {
            this.carId = carId;
        }

        public String getCphm() {
            return cphm;
        }

        public void setCphm(String cphm) {
            this.cphm = cphm;
        }

        public String getDriverId() {
            return driverId;
        }

        public void setDriverId(String driverId) {
            this.driverId = driverId;
        }
    }

    public static class DispatcherPostContent {
        Boolean finished;
        ApplyInfo apply;
        ScheduleInfo schedule;
        ScheduleCarInfo scheduleCar;
        EvaluateInfo evaluateInfo;

        public ApplyInfo getApply() {
            return apply;
        }

        public void setApply(ApplyInfo apply) {
            this.apply = apply;
        }

        public ScheduleInfo getSchedule() {
            return schedule;
        }

        public void setSchedule(ScheduleInfo schedule) {
            this.schedule = schedule;
        }

        public ScheduleCarInfo getScheduleCar() {
            return scheduleCar;
        }

        public void setScheduleCar(ScheduleCarInfo scheduleCar) {
            this.scheduleCar = scheduleCar;
        }

        public EvaluateInfo getEvaluateInfo() {
            return evaluateInfo;
        }

        public void setEvaluateInfo(EvaluateInfo evaluateInfo) {
            this.evaluateInfo = evaluateInfo;
        }

        public Boolean getFinished() {
            return finished;
        }

        public void setFinished(Boolean finished) {
            this.finished = finished;
        }
    }

    private String title;
    private String postType = "Share";
    private String contentType = "Html";
    private String content;

    private List<PostTarget> postTargets = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<PostTarget> getPostTargets() {
        return postTargets;
    }

    public void setPostTargets(List<PostTarget> postTargets) {
        this.postTargets = postTargets;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
