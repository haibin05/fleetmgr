<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
<#if (coordinator?? && coordinator) || (auditor?? && auditor)>
    <title>派单更新</title>
</#if>
<#if dispatcher?? && dispatcher>
    <title>派单更新</title>
</#if>
<#if driver?? && driver>
    <title>派单成功</title>
</#if>
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimal-ui" />
    <meta name="apple-mobile-web-app-status-bar-style" content="yes" />
    <style>
        body {
            font-size: 14px;
        }
        h5 {
            font-size:14px;
        }
        p {
            margin: 0 0 10px;
            padding: 0 10px 0 25px;
        }
        a {
            text-decoration: none;
            -webkit-tap-highlight-color: rgba(0,0,0,0);
            color: #007aff;
        }
        a:hover {
            outline: 0;
            color: #007aff;
            text-decoration: none;
        }
        .btn-default:hover {
            color: #333;
            background-color: #e6e6e6;
            border-color: #adadad;
        }
        .pull-left {
            float: left!important;
        }
        .pull-right {
            float: right!important;
        }
        .clearfix:after {
            clear: both;
        }
        .clearfix:before, .clearfix:after {
            content: " ";
            display: table;
        }
        .m-l-10 {
            margin-left:10px;
        }
        .label {
            color:#4c4c4c;
        }
        .label-with-pad {
            padding-left:14px;
            color:#4c4c4c;
        }
        .address-badge {
            display: inline-block;
            width: 24px;
            height: 24px;
            text-align: center;
            line-height: 24px;
            border-radius: 50%;
        }
        .address-badge.from {
            border: 1px solid #2ea9df;
            color: #2ea9df;
        }
        .address-badge.to {
            border: 1px solid #cd7f32;
            color: #cd7f32;
        }
        .group-item {
            position: relative;
            display: block;
            background-color: #fff;
            border:1px solid #2ea9df;
        }
        .group-item .item-header {
            font-weight: 400;
            border-bottom:1px solid #2ea9df;
            margin:0;
            color:#2ea9df;
            padding:0 30px;
            line-height:38px;
        }
        .group-item .item-body {
            padding:15px 0;
        }
        .h-divider {
            border-top: 1px solid #d6d6d6;
            margin: 15px 0;
        }
        .h-divider-car {
            border-top: 2px solid #2ea9df;
            margin: 15px 0;
        }
        .btn {
            display: inline-block;
            margin-bottom: 0;
            font-weight: 400;
            text-align: center;
            vertical-align: middle;
            touch-action: manipulation;
            cursor: pointer;
            background-image: none;
            border: 1px solid transparent;
            white-space: nowrap;
            padding: 6px 12px;
            font-size: 14px;
            line-height: 1.42857143;
            border-radius: 4px;
            -webkit-user-select: none;
            -moz-user-select: none;
            -ms-user-select: none;
            user-select: none;
        }
        .btn-default {
            color: #333;
            background-color: #fff;
            border-color: #ccc;
        }
        .bottom-bar .btn {
            margin-top:20px;
            margin-right:20px;
        }

    </style>
</head>
<body>

<div class="group-item">
    <!-- 申请人 -->
<#if (coordinator?? && coordinator) || (auditor?? && auditor)>
    <h5 class="item-header clearfix">
        <span class="pull-left">派单更新</span>
        <span class="pull-right">待出发</span>
    </h5>

    <div class="item-body">

        <p><span class="address-badge from">起</span> ${r.apply.startPoint!}</p>
        <p style="padding-left:24px;"> ${r.apply.startTime?string("MM月dd日 HH:mm")}</p>
        <p><span class="address-badge to">终</span> ${r.apply.endPoint!}</p>
        <p style="padding-left:24px;"> ${r.apply.endTime?string("MM月dd日 HH:mm")}</p>
        <p>乘客: ${r.apply.mainUser!}
            <#if r.apply.mainUserPhone??>
                ( ${r.apply.mainUserPhone!} )
            </#if>
        </p>
        <p>人数: ${r.apply.peopleNum!}</p>
        <p>事宜: ${r.apply.reason!}</p>
        <div class="h-divider-car"></div>

        <#list r.schedule.scheduleCarList as scheduleCar>
            <p>司机: ${scheduleCar.driverName!}</p>
            <p>手机: ${scheduleCar.driverMobile!}
                <#if scheduleCar.driverPhone??>
                    ( ${scheduleCar.driverPhone!} )
                </#if>
            </p>
            <p>车牌号码: ${scheduleCar.badge!}</p>
            <#if scheduleCar_has_next>
                <div class="h-divider"></div>
            </#if>
        </#list>
    </div>
</#if>
    <!-- 调度人 -->
<#if dispatcher?? && dispatcher>
    <h5 class="item-header clearfix">
        <span class="pull-left">派单更新</span>
        <span class="pull-right">待出发</span>
    </h5>

    <div class="item-body">

        <#list r.schedule.applyInfoList as applyInfo>
            <p><span class="address-badge from">起</span> ${applyInfo.startPoint!}</p>
            <p style="padding-left:24px;"> ${applyInfo.startTime?string("MM月dd日 HH:mm")}</p>
            <p><span class="address-badge to">终</span> ${applyInfo.endPoint!}</p>
            <p style="padding-left:24px;"> ${applyInfo.endTime?string("MM月dd日 HH:mm")}</p>
            <p>乘客: ${applyInfo.mainUser!}
                <#if applyInfo.mainUserPhone??>
                    ( ${applyInfo.mainUserPhone!} )
                </#if>
            </p>
            <p>人数: ${applyInfo.peopleNum!}</p>
            <p>事宜: ${applyInfo.reason!}</p>
            <#if applyInfo_has_next>
                <div class="h-divider"></div>
            </#if>
        </#list>
        <div class="h-divider-car"></div>

        <#list r.schedule.scheduleCarList as scheduleCar>
            <p>司机: ${scheduleCar.driverName!}</p>
            <p>手机: ${scheduleCar.driverMobile!}
                <#if scheduleCar.driverPhone??>
                    ( ${scheduleCar.driverPhone!} )
                </#if>
            </p>
            <p>车牌号码: ${scheduleCar.badge!}</p>
            <#if scheduleCar_has_next>
                <div class="h-divider"></div>
            </#if>
        </#list>

        <assign class="bottom-bar clearfix"/>
        <a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/schedules/${r.schedule.scheduleId!}">修改派单</a>
    </div>
</#if>
    <!-- 驾驶员 -->
<#if driver?? && driver>
    <h5 class="item-header clearfix">
        <span class="pull-left">派单更新</span>
        <span class="pull-right">待出发</span>
    </h5>

    <div class="item-body">
        <p><span class="address-badge from">起</span> ${r.schedule.startPoint!}</p>
        <p style="padding-left:24px;"> ${r.schedule.startTime?string("MM月dd日 HH:mm")}</p>
        <p><span class="address-badge to">终</span> ${r.schedule.endPoint!}</p>
        <p style="padding-left:24px;"> ${r.schedule.endTime?string("MM月dd日 HH:mm")}</p>

        <#list r.schedule.applyInfoList as applyInfo>
            <p>乘客: ${applyInfo.mainUser!}
                <#if applyInfo.mainUserPhone??>
                    ( ${applyInfo.mainUserPhone!} )
                </#if>
            </p>
        </#list>
        <div class="h-divider-car"></div>

        <p>司机: ${r.scheduleCar.driverName!}</p>
        <p>手机: ${r.scheduleCar.driverMobile!}
            <#if r.scheduleCar.driverPhone??>
                ( ${r.scheduleCar.driverPhone!} )
            </#if>
        </p>
        <p>车牌号码: ${r.scheduleCar.badge!}</p>

        <assign class="bottom-bar clearfix"/>
        <a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/scheduleCars/${r.scheduleCar.scheduleCarId!}/record/start">出车</a>

    </div>
</#if>

</div>
</body>
</html>