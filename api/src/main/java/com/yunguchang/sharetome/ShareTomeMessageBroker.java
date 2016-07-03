package com.yunguchang.sharetome;

import com.yunguchang.data.ApplicationRepository;
import com.yunguchang.data.ScheduleRepository;
import com.yunguchang.model.EntityConvert;
import com.yunguchang.model.common.ScheduleStatus;
import com.yunguchang.model.common.User;
import com.yunguchang.model.persistence.TBusApplyinfoEntity;
import com.yunguchang.model.persistence.TBusScheduleCarEntity;
import org.apache.commons.lang3.StringUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.yunguchang.sharetome.ShareTomeMessage.*;

/**
 * Created by gongy on 2015/11/17.
 */
@Stateless

public class ShareTomeMessageBroker {

    @Inject
    private ApplicationRepository applicationRepository;
    @Inject
    private ScheduleRepository scheduleRepository;

    public static final String robotName = "jxdlgps@jxdl.com";
    public static final String robotPwd = "123456";
    private ShareTomeMessageService shareTomeMessageService = ((ResteasyClientBuilder) ResteasyClientBuilder.newBuilder()).disableTrustManager().build().target(ShareTomeMessageService.SHARE_TOME_URL).proxy(ShareTomeMessageService.class);

    @Asynchronous
    public void receiveShareTomeMessage(@Observes ShareTomeEvent shareTomeEvent) throws GeneralSecurityException {
        //原则上，token应该是从sso登录获取。现在我们先使用一个robot  TODO 密码加密统一处理
        ShareTomeToken token = ShareTomeEvent.getToken();
        if (token == null) {
            token = shareTomeMessageService.getToken(robotName, robotPwd, ShareTomeMessageService.client_id,
                    ShareTomeMessageService.client_secret, null, ShareTomeMessageService.grant_type_password);
        } else if (ShareTomeEvent.isExpired()) {
            shareTomeMessageService.getToken(null, null, ShareTomeMessageService.client_id,
                    ShareTomeMessageService.client_secret, token.getRefreshToken(), ShareTomeMessageService.grant_type_refresh_token);
            token = ShareTomeEvent.getToken();
        }
        ShareTomeEvent.setToken(token);

        //TODO 异步的ejb函数。exception无法catch，需要考虑日志，参见 com.yunguchang.logger.Logger
        if (null == token.getAccessToken()) {
            throw new RuntimeException("Username or Password is error");
        }
        sendShareTomeMessage(token, shareTomeEvent);
    }

    private void sendShareTomeMessage(final ShareTomeToken token, final ShareTomeEvent shareTomeEvent) {
        ApplyInfo applyInfo = shareTomeEvent.getContent().getApply();
        ScheduleInfo scheduleInfo = shareTomeEvent.getContent().getSchedule();
        ScheduleCarInfo scheduleCarInfo = shareTomeEvent.getContent().getScheduleCar();

        List<User> coordinatorList = new ArrayList<>();
        List<User> auditorList = new ArrayList<>();
        List<User> dispatcherList = new ArrayList<>();
        List<User> driverList = new ArrayList<>();
        for (User user : shareTomeEvent.getRecipients()) {
            if (user.isCoordinator()) {
                coordinatorList.add(user);
                continue;
            }
            if (user.isAuditor()) {
                auditorList.add(user);
                continue;
            }
            if (user.isDispatcher()) {
                dispatcherList.add(user);
                continue;
            }
            if (user.isDriver()) {
                driverList.add(user);
                continue;
            }
        }
        TBusApplyinfoEntity applyEntity;
        switch (shareTomeEvent.getMessageCategory()) {
            case ADD_POST_MEMBER:
                applyEntity = applicationRepository.getApplicationById(applyInfo.getApplyId());
                addMemberToPost(token, applyEntity.getPostId4Dispatcher(), 0, dispatcherList);
                break;
            case APPLICATION_CREATE:        // 申请 创建 申请    -> 2
            case APPLICATION_APPROVE:       // 部门审批     -> 3
            case APPLICATION_REJECT:      // 审批退回     -> 5
                sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                break;
            case APPLICATION_DISPATCH:                // 调度成功     -> 4
            case APPLICATION_DISPATCH_MERGE:                // 调度成功     -> 4
            case SCHEDULE_START:                       // 驾驶员出厂
            case SCHEDULE_END:                         // 驾驶员回厂
            case APPLICATION_DISPATCH_UPDATE:     // 更新调度单    -> 4
            case SCHEDULE_CHANGE_DELETE_APPLY:            // 更新调度单-删除信息
            case SCHEDULE_CHANGE_ADD_APPLY:               // 更新调度单-添加信息
            case SCHEDULE_CHANGE_DELETE_CAR:            // 更新调度单-删除信息
            case SCHEDULE_CHANGE_ADD_CAR:               // 更新调度单-添加信息
            case SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR:            // 更新调度单-删除信息
                if (applyInfo != null && !coordinatorList.isEmpty()) {      // 申请人
                    sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                }
                if (scheduleInfo != null && !dispatcherList.isEmpty()) {  // 调度人
                    TBusApplyinfoEntity applyForAll = null;
                    Map<String, TBusApplyinfoEntity> applyEntityMap = new HashMap<>();
                    for (ApplyInfo apply : scheduleInfo.getApplyInfoList()) {
                        applyEntity = applicationRepository.getApplicationById(apply.getApplyId());
                        applyEntityMap.put(applyEntity.getUuid(), applyEntity);
                        if (applyForAll == null || applyForAll.getPostId4Dispatcher().compareTo(applyEntity.getPostId4Dispatcher()) > 0) {
                            applyForAll = applyEntity;
                        }
                    }
                    sendMessageForApply(token, shareTomeEvent, applyForAll.getUuid());

                    // send post to other apply for dispatcher and send post only once
                    if (scheduleInfo.getApplyInfoList().size() > 0 && ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH.equals(shareTomeEvent.getMessageCategory())) {
                        shareTomeEvent.setMessageCategory(ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH_MERGE);
                        for (ApplyInfo apply : scheduleInfo.getApplyInfoList()) {
                            if (!applyForAll.getUuid().equals(apply.getApplyId())) {
                                applyEntity = applyEntityMap.get(apply.getApplyId());
                                sendShareTomeMessage(token, shareTomeEvent, applyEntity.getPostId4Dispatcher(), 0, dispatcherList.toArray(new User[dispatcherList.size()]));
                            }
                        }
                        shareTomeEvent.setMessageCategory(ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH);
                    }
                }
                if (scheduleCarInfo != null && !driverList.isEmpty()) {   // 驾驶员
                    if(shareTomeEvent.getMessageCategory().equals(ShareTomeEvent.MessageCategory.SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR)) {
                        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCarById(scheduleCarInfo.getScheduleCarId());
                        String postId4Driver = scheduleCarEntity.getPostId4Driver().contains("::") ? scheduleCarEntity.getPostId4Driver().split("::")[0] : scheduleCarEntity.getPostId4Driver();
                        sendShareTomeMessage(token, shareTomeEvent, postId4Driver, 0, EntityConvert.toSnsUser(scheduleCarEntity.getDriver()));
                    } else {
                        sendMessageForDriver(token, shareTomeEvent, driverList, scheduleInfo, scheduleCarInfo);
                    }
                }
                break;
            case APPLICATION_RATE_DRIVER:            // 评价驾驶员
                if (applyInfo != null) {      // 申请人  // 调度人
                    sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                }
                break;
            case APPLICATION_RATE_PASSENGER:         // 评价用车人
            case APPLICATION_RATE_APPLY:              // 评价用车申请
                if (applyInfo != null) {      // 申请人  // 调度人
                    sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                }
                break;
            case SCHEDULE_RETREAT:       // 调度退回     -> 6
                sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                break;
            case SCHEDULE_DELETE:        // 调度作废     -> 7
                if (applyInfo != null) {      // 申请人
                    sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                }
                if(scheduleInfo != null) {
                    for(ApplyInfo apply : scheduleInfo.getApplyInfoList()) {
                        // 调度员
                        applyEntity = applicationRepository.getApplicationById(apply.getApplyId());
                        sendShareTomeMessage(token, shareTomeEvent, applyEntity.getPostId4Dispatcher(), 0, dispatcherList.toArray(new User[dispatcherList.size()]));
                    }
                }
                if (scheduleCarInfo != null && !driverList.isEmpty()) {   // 驾驶员  driver is old driver
                    TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCarById(scheduleCarInfo.getScheduleCarId());
                    String driverPostId = sendShareTomeMessage(token, shareTomeEvent, scheduleCarEntity.getPostId4Driver(), 0, EntityConvert.toSnsUser(scheduleCarEntity.getDriver()));
                    scheduleCarEntity.setPostId4Driver(driverPostId);
                    scheduleRepository.updateScheduleCar4PostId(scheduleCarInfo.getScheduleCarId(), scheduleCarEntity);
                }
                break;
            case APPLICATION_CANCEL:     // 取消申请    -> 8
                sendMessageForApply(token, shareTomeEvent, applyInfo.getApplyId());
                break;
            case ADD_NEW_MEMBER:    // 添加用户
                SharetomeUserVO userVO;
                for(User user : shareTomeEvent.getRecipients()) {
                    userVO = new SharetomeUserVO();
                    userVO.setNickName(user.getUserName());
                    if(user.isDriver()) {
                        userVO.setRegisterName(user.getMobile());
                        userVO.setMobileNo(user.getMobile());
                    } else {
                        userVO.setRegisterName(user.getEid());
                        userVO.setEmail(user.getEid() + "@jxcl.com");
                    }
                    createNewUser(token, 0, userVO);
                }
                break;
            default:
                return;
        }
    }

    private TBusApplyinfoEntity sendMessageForApply(ShareTomeToken token, ShareTomeEvent shareTomeEvent, String applyId) {
        TBusApplyinfoEntity applyEntity = applicationRepository.getApplicationById(applyId);
        if (applyEntity == null) {
            return null;
        }
//        List<User> users = filterUserByPostContent(shareTomeEvent.getContent());
        List<User> users = shareTomeEvent.getRecipients();
        for (User user : users) {
            if (user.isCoordinator()) {
                String coordinatorPostId = sendShareTomeMessage(token, shareTomeEvent, applyEntity.getPostId4Coordinator(), 0, user);// send post to coordinator
                if (applyEntity.getPostId4Coordinator() == null) {
                    applyEntity.setPostId4Coordinator(coordinatorPostId);
                }
            } else if (user.isAuditor()) {
                String auditorPostId = sendShareTomeMessage(token, shareTomeEvent, applyEntity.getPostId4Auditor(), 0, user);// send post to auditor
                if (applyEntity.getPostId4Auditor() == null) {
                    applyEntity.setPostId4Auditor(auditorPostId);
                }
            } else if (user.isDispatcher()) {
                String dispatcherPostId = sendShareTomeMessage(token, shareTomeEvent, applyEntity.getPostId4Dispatcher(), 0, user);  // send post to dispatcher
                if (applyEntity.getPostId4Dispatcher() == null) {
                    applyEntity.setPostId4Dispatcher(dispatcherPostId);
                }
            } else if (user.isDriver()) {
                continue;
            }
        }

        return applicationRepository.updateApplication4PostId(applyEntity.getUuid(), applyEntity);
    }

    private String addMemberToPost(ShareTomeToken token, String postId, int tryTime, List<User> users) {
        if (users == null || users.size() == 0) {
            return null;
        }
        List<String> userList = new ArrayList<>();
        for (User user : users) {
            userList.add(user.getUserId());
        }
        try {
            if(StringUtils.isBlank(postId)) {
                return null;
            }
            if (postId.contains("::")) {
                postId = postId.split("::")[0];
            }
            // send post
            ShareTomeMessageResponse shareTomePostResponse = shareTomeMessageService.addPostMembers(token.getFullBearerToken(), postId, userList);
            if (!shareTomePostResponse.isSuccess()) {
                throw new RuntimeException("Send post failed");
            }
        } catch (NotAuthorizedException authException) {
            if (tryTime < 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                token = shareTomeMessageService.getToken(robotName, robotPwd, ShareTomeMessageService.client_id,
                        ShareTomeMessageService.client_secret, null, ShareTomeMessageService.grant_type_password);
                ShareTomeEvent.setToken(token);
                return addMemberToPost(token, postId, tryTime++, users);
            } else {
                throw authException;
            }
        }
        return postId;
    }

    private void createNewUser(ShareTomeToken token, int tryTime, SharetomeUserVO user) {
        if (user == null) {
            return;
        }
        try {
            // send post
            ShareTomeMessageResponse shareTomePostResponse = shareTomeMessageService.createNewUser(token.getFullBearerToken(), user);
            if (!shareTomePostResponse.isSuccess()) {
                throw new RuntimeException("Create new user failed");
            }
        } catch (NotAuthorizedException authException) {
            if (tryTime < 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                token = shareTomeMessageService.getToken(robotName, robotPwd, ShareTomeMessageService.client_id,
                        ShareTomeMessageService.client_secret, null, ShareTomeMessageService.grant_type_password);
                ShareTomeEvent.setToken(token);
                createNewUser(token, tryTime++, user);
            } else {
                throw authException;
            }
        }
    }

    private void sendMessageForDriver(ShareTomeToken token, ShareTomeEvent shareTomeEvent, List<User> driverList, ScheduleInfo scheduleInfo, ScheduleCarInfo scheduleCarInfo) {
        TBusScheduleCarEntity scheduleCarEntity = scheduleRepository.getScheduleCarById(scheduleCarInfo.getScheduleCarId());
        String fullDriverPost = scheduleCarEntity.getPostId4Driver();
        if (fullDriverPost == null) {
            fullDriverPost = " :: ";
        }
        String oldDriverPostId;
        if(fullDriverPost.contains("::")) {
            if (StringUtils.isBlank(fullDriverPost.split("::")[1])) {
                oldDriverPostId = fullDriverPost.split("::")[0];
            } else {
                oldDriverPostId = fullDriverPost.split("::")[1];
            }
        } else {
            oldDriverPostId = fullDriverPost;
        }

        // to remove driver
        for (User driver : driverList) {
            if (!scheduleCarInfo.getDriverId().equals(driver.getEid())) {
                for (ScheduleCarInfo carEntity : scheduleInfo.getScheduleCarList()) {
                    if (carEntity.getDriverId().equals(driver.getEid())) {
                        shareTomeEvent.getContent().setScheduleCar(carEntity);
                        sendShareTomeMessage(token, shareTomeEvent, oldDriverPostId, 0, driverList.toArray(new User[driverList.size()]));
                        break;
                    }
                }
            }
        }
        // to new driver
        String newDriverPostId = fullDriverPost.split("::")[0];
        boolean newPostForNewDriver = true;
        for (ScheduleCarInfo carEntity : scheduleInfo.getScheduleCarList()) {
            if (carEntity.getDriverId().equals(scheduleCarInfo.getDriverId())) {        // current driver
                TBusScheduleCarEntity tmpScheduleCarEntity = scheduleRepository.getScheduleCarById(carEntity.getScheduleCarId());
                newDriverPostId = tmpScheduleCarEntity.getPostId4Driver() == null ? "" : tmpScheduleCarEntity.getPostId4Driver().split("::")[0];
                newPostForNewDriver = false;
                break;
            }
        }
        if (newPostForNewDriver) {
            shareTomeEvent.getContent().setScheduleCar(EntityConvert.toSnsScheduleCarInfo(scheduleCarEntity));
            newDriverPostId = "";
        }
        // for new message content
        boolean ignore = false;
        if (ShareTomeEvent.MessageCategory.SCHEDULE_CHANGE_DELETE_CAR.equals(shareTomeEvent.getMessageCategory())) {
            if (!scheduleCarEntity.getStatus().equals(ScheduleStatus.CANCELED.id())) {
                shareTomeEvent.setMessageCategory(ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH);
                if (!oldDriverPostId.equals(newDriverPostId)) {
                    ignore = true;
                }
            }
        }
        if (!ignore || newPostForNewDriver) {
            if(ShareTomeEvent.MessageCategory.APPLICATION_DISPATCH.equals(shareTomeEvent.getMessageCategory())) {
                newDriverPostId = sendShareTomeMessage(token, shareTomeEvent, null, 0, EntityConvert.toSnsUser(scheduleCarEntity.getDriver()));
            } else {
                newDriverPostId = sendShareTomeMessage(token, shareTomeEvent, newDriverPostId, 0, EntityConvert.toSnsUser(scheduleCarEntity.getDriver()));
            }
        }
        scheduleCarEntity.setPostId4Driver(newDriverPostId + " :: " + oldDriverPostId);
        scheduleRepository.updateScheduleCar4PostId(scheduleCarInfo.getScheduleCarId(), scheduleCarEntity);
    }

    private String sendShareTomeMessage(ShareTomeToken token, ShareTomeEvent shareTomeEvent, String postId, int tryTime, User... users) {
        if (users == null || users.length == 0) {
            return null;
        }
        List<User> userList = new ArrayList<>();
        for (User user : users) {
            userList.add(user);
        }
        try {
            if (StringUtils.isBlank(postId)) {
                // send post
                ShareTomeMessageResponse shareTomePostResponse = shareTomeMessageService.sendMessageByOpenAPI(
                        token.getFullBearerToken(), shareTomeEvent.toShareTomePost(userList));
                if (!shareTomePostResponse.isSuccess()) {
                    throw new RuntimeException("Send post failed");
                }
                postId = shareTomePostResponse.getData().get("postId").toString();
            }
            if (postId.contains("::")) {
                postId = postId.split("::")[0];
            }
            // 添加回复（订单简介）
            ShareTomeMessageResponse shareTomeCommentResponse = shareTomeMessageService.replyPostByOpenAPI(
                    token.getFullBearerToken(), postId.trim(), shareTomeEvent.toShareTomeComment(userList));
            if (!shareTomeCommentResponse.isSuccess()) {
                throw new RuntimeException("Reply comment failed");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            if (tryTime < 3) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                }
                if(exception instanceof NotAuthorizedException) {
                    token = shareTomeMessageService.getToken(robotName, robotPwd, ShareTomeMessageService.client_id,
                            ShareTomeMessageService.client_secret, null, ShareTomeMessageService.grant_type_password);
                    ShareTomeEvent.setToken(token);
                }
                return sendShareTomeMessage(token, shareTomeEvent, postId, tryTime + 1, users);
            } else {
                throw exception;
            }
        }
        return postId;
    }

}