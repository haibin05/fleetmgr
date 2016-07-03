package com.yunguchang.sharetome;

import com.yunguchang.model.common.User;
import com.yunguchang.utils.tools.FreeMarkerUtil;

import java.util.*;

import static com.yunguchang.sharetome.ShareTomeMessage.DispatcherPostContent;
import static com.yunguchang.sharetome.ShareTomeMessage.PostTarget;

/**
 * Created by gongy on 2015/11/17.
 */
public class ShareTomeEvent {
    private static ShareTomeToken token;

    private List<User> recipients;

    private DispatcherPostContent content;

    private MessageCategory messageCategory;

    public ShareTomeEvent(List<User> recipients, DispatcherPostContent content, MessageCategory messageCategory) {
        this.recipients = recipients;
        this.content = content;
        this.messageCategory = messageCategory;
    }

    public static ShareTomeToken getToken() {
        return token;
    }

    public static boolean isExpired() {
        return token.getCreateTime().plus(token.getExpireIn().toDate().getTime() * 1000).isAfter((new Date()).getTime());
    }

    public static void setToken(ShareTomeToken token) {
        ShareTomeEvent.token = token;
    }

    public List<User> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<User> recipients) {
        this.recipients = recipients;
    }

    public DispatcherPostContent getContent() {
        return content;
    }

    public void setContent(DispatcherPostContent content) {
        this.content = content;
    }

    public MessageCategory getMessageCategory() {
        return messageCategory;
    }

    public void setMessageCategory(MessageCategory messageCategory) {
        this.messageCategory = messageCategory;
    }

    public enum MessageCategory {
        APPLICATION_CREATE,     // 创建 申请    -> 2
        APPLICATION_APPROVE,    // 部门审批     -> 3
        APPLICATION_DISPATCH,   // 调度成功     -> 4
        APPLICATION_DISPATCH_MERGE,  // 调度成功
        APPLICATION_DISPATCH_UPDATE,   // 调度成功     -> 4
        APPLICATION_REJECT,     // 审批退回     -> 5
        SCHEDULE_RETREAT,       // 调度退回     -> 6
        SCHEDULE_DELETE,        // 调度作废     -> 7
        APPLICATION_CANCEL,     // 取消申请    -> 8
        SCHEDULE_CHANGE_DELETE_APPLY,  // 删除信息
        SCHEDULE_CHANGE_ADD_APPLY,     // 添加信息
        SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR,  // 删除信息
        SCHEDULE_CHANGE_DELETE_CAR,  // 删除信息
        SCHEDULE_CHANGE_ADD_CAR,     // 添加信息
        SCHEDULE_START,         // 出车
        SCHEDULE_END,           // 回厂
        APPLICATION_RATE_DRIVER,            // 评价驾驶员
        APPLICATION_RATE_PASSENGER,        // 评价用车人
        APPLICATION_RATE_APPLY,             // 评价用车申请
        ADD_POST_MEMBER,             // 添加用户
        ADD_NEW_MEMBER,             // 添加用户
        OTHER,                  // 其他
    }

    public static Builder builder() {
        return new Builder();
    }

    public ShareTomeMessage toShareTomeComment(List<User> users) {
        ShareTomeMessage message = new ShareTomeMessage();
        message.setPostTargets(convertPostTarget(this.getRecipients(), users));

        Map<String, Object> context = new HashMap<>();
        context.put("r", this.content);
        context.put("coordinator", message.getPostTargets().get(0).isCoordinator());
        context.put("auditor", message.getPostTargets().get(0).isAuditor());
        context.put("dispatcher", message.getPostTargets().get(0).isDispatcher());
        context.put("driver", message.getPostTargets().get(0).isDriver());
        context.put("fleethost", ShareTomeMessageService.CALLBACK_URL);
        String template = null;
        switch (this.getMessageCategory()) {
            case APPLICATION_CREATE:
                template = "application_apply_create_zh_CN.ftl";
                break;
            case APPLICATION_APPROVE:
                template = "application_apply_approve_zh_CN.ftl";
                break;
            case APPLICATION_DISPATCH:
                if (message.getPostTargets().get(0).isDriver()) {
                    template = "application_apply_dispatch_driver_zh_CN.ftl";
                } else {
                    template = "application_apply_dispatch_zh_CN.ftl";
                }
                break;
            case APPLICATION_DISPATCH_MERGE:    // for dispatcher only
                if (message.getPostTargets().get(0).isDispatcher()) {
                    template = "application_apply_dispatch_merge_zh_CN.ftl";
                }
                break;
            case APPLICATION_REJECT:
                template = "application_apply_reject_zh_CN.ftl";
                break;
            case SCHEDULE_RETREAT:
                template = "application_schedule_reject_zh_CN.ftl";
                break;
            case SCHEDULE_DELETE:
                template = "application_schedule_delete_zh_CN.ftl";
                break;
            case APPLICATION_CANCEL:
                template = "application_apply_cancel_zh_CN.ftl";
                break;
            case SCHEDULE_START:
                template = "application_schedule_start_zh_CN.ftl";
                break;
            case SCHEDULE_END:
                template = "application_schedule_end_zh_CN.ftl";
                break;
            case APPLICATION_RATE_DRIVER:
                template = "application_apply_rote_driver_zh_CN.ftl";
                break;
            case APPLICATION_RATE_PASSENGER:
                template = "application_apply_rote_passenger_zh_CN.ftl";
                break;
            case APPLICATION_RATE_APPLY:
                template = "application_apply_rote_apply_zh_CN.ftl";
                break;

            case APPLICATION_DISPATCH_UPDATE:
                context.put("update", true);
                if (message.getPostTargets().get(0).isDriver()) {
                    template = "application_schedule_apply_add_zh_CN.ftl";
                } else {
                    template = "application_schedule_driver_add_zh_CN.ftl";
                }
                break;
            case SCHEDULE_CHANGE_DELETE_APPLY:
                template = "application_schedule_apply_delete_zh_CN.ftl";
                break;
            case SCHEDULE_CHANGE_DELETE_CAR:
            case SCHEDULE_CHANGE_DELETE_SCHEDULE_CAR:
                template = "application_schedule_driver_delete_zh_CN.ftl";
                break;
            case SCHEDULE_CHANGE_ADD_APPLY:
                template = "application_schedule_apply_add_zh_CN.ftl";
                break;
            case SCHEDULE_CHANGE_ADD_CAR:
                template = "application_schedule_driver_add_zh_CN.ftl";
                break;
            default:
                template = null;
                break;
        }

        String content = FreeMarkerUtil.generateContent(context, template);
        message.setContent(content);
        return message;
    }

    public ShareTomeMessage toShareTomePost(List<User> users) {
        ShareTomeMessage message = new ShareTomeMessage();
        if (this.getContent().getSchedule() != null && !MessageCategory.APPLICATION_CREATE.equals(this.getMessageCategory())) {
            message.setTitle(this.getContent().getSchedule().getStartPoint() + "->" + this.getContent().getSchedule().getEndPoint());
        } else {
            message.setTitle(this.getContent().getApply().getMainUser() + "[" + this.getContent().getApply().getStartPoint() + "->" + this.getContent().getApply().getEndPoint() + "]");
        }
        message.setPostTargets(convertPostTarget(this.getRecipients(), users));
        message.setContent(null);
        return message;
    }

    private static List<PostTarget> convertPostTarget(List<User> userList, List<User> users) {
        List<PostTarget> postTargets = new ArrayList<>();
        List<User> postTargetList = new ArrayList<>();
        if (users == null || users.size() == 0) {
            postTargetList.addAll(userList);
        } else {
            postTargetList.addAll(users);
        }
        for (User user : postTargetList) {
            PostTarget postTarget = new PostTarget(user.getUserType());
            if (user.getUserId() != null) {
                postTarget.setId(user.getUserId());
            } else {
                postTarget.setId(user.getEid());
            }
            postTarget.setNickName(user.getUserName());
            postTargets.add(postTarget);
        }
        return postTargets;
    }

    public static class Builder {
        private List<User> recipients = new ArrayList<>();

        private DispatcherPostContent content;

        private MessageCategory messageCategory;

        public List<User> getRecipients() {
            return recipients;
        }

        public Builder addRecipient(User recipient) {
            recipients.add(recipient);
            return this;

        }

        public DispatcherPostContent getContent() {
            return content;
        }

        public Builder setContent(DispatcherPostContent content) {
            if (this.content != null) {
                mergeContent(content, this.content);
            } else {
                this.content = content;
            }
            return this;
        }

        private void mergeContent(DispatcherPostContent contentFrom, DispatcherPostContent contentTo) {
            if (contentTo.getApply() == null) {
                contentTo.setApply(contentFrom.getApply());
            }
            if (contentTo.getSchedule() == null) {
                contentTo.setSchedule(contentFrom.getSchedule());
            }
            if (contentTo.getScheduleCar() == null) {
                contentTo.setScheduleCar(contentFrom.getScheduleCar());
            }
            if (contentTo.getEvaluateInfo() == null) {
                contentTo.setEvaluateInfo(contentFrom.getEvaluateInfo());
            }
        }

        public MessageCategory getMessageCategory() {
            return messageCategory;
        }

        public Builder setMessageCategory(MessageCategory messageCategory) {
            this.messageCategory = messageCategory;
            return this;

        }

        public ShareTomeEvent build() {
            return new ShareTomeEvent(recipients, content, messageCategory);
        }
    }
}



