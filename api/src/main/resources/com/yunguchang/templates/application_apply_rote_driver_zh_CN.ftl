<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <title>评价结束</title>
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
    <h5 class="item-header clearfix">
        <span class="pull-left">评价</span>
        <span class="pull-right">评价驾驶员结束</span>
    </h5>

    <div class="item-body">

    <#if (coordinator?? && coordinator) || (auditor?? && auditor)>
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

        <p>司机: ${r.evaluateInfo.driverName!}</p>
        <p>评分: ${r.evaluateInfo.driverScore!}
            <#if r.evaluateInfo.driverReason??>
                （${r.evaluateInfo.driverReason!}）
            </#if>
        </p>
        <p>车辆: ${r.evaluateInfo.cphm!}</p>
        <p>评分: ${r.evaluateInfo.carScore!}
            <#if r.evaluateInfo.carReason??>
                （${r.evaluateInfo.carReason!}）
            </#if>
        </p>
        <p>车队: ${r.evaluateInfo.fleetName!}</p>
        <p>评分: ${r.evaluateInfo.fleetScore!}
            <#if r.evaluateInfo.fleetReason??>
                （${r.evaluateInfo.fleetReason!}）
            </#if>
        </p>
    </#if>

    <#if dispatcher?? && dispatcher>
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

        <assign class="bottom-bar clearfix"/>
        <#if r.evaluateInfo.driverScore??>
            <p>司机: ${r.evaluateInfo.driverName!}</p>
            <p>评分: ${r.evaluateInfo.driverScore!}
                <#if r.evaluateInfo.driverReason??>
                    （${r.evaluateInfo.driverReason!}）
                </#if>
            </p>
        </#if>
        <#if r.evaluateInfo.carScore??>
            <p>车辆: ${r.evaluateInfo.cphm!}</p>
            <p>评分: ${r.evaluateInfo.carScore!}
                <#if r.evaluateInfo.carReason??>
                    （${r.evaluateInfo.carReason!}）
                </#if>
            </p>
        </#if>
        <#if r.evaluateInfo.fleetScore??>
            <p>车队: ${r.evaluateInfo.fleetName!}</p>
            <p>评分: ${r.evaluateInfo.fleetScore!}
                <#if r.evaluateInfo.fleetReason??>
                    （${r.evaluateInfo.fleetReason!}）
                </#if>
            </p>
        </#if>
        <#if r.evaluateInfo.passengerScore??>
            <p>乘客: ${r.evaluateInfo.passengerName!}</p>
            <p>评分: ${r.evaluateInfo.passengerScore!}
                <#if r.evaluateInfo.passengerReason??>
                    （${r.evaluateInfo.passengerReason!}）
                </#if>
            </p>
        </#if>
    </#if>

    <#if driver?? && driver>
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

        <assign class="bottom-bar clearfix"/>
        <p>司机: ${r.evaluateInfo.driverName!}</p>
        <p>评分: ${r.evaluateInfo.driverScore!}
            <#if r.evaluateInfo.driverReason??>
                （${r.evaluateInfo.driverReason!}）
            </#if>
        </p>
    </#if>

    </div>
</div>
</body>
</html>