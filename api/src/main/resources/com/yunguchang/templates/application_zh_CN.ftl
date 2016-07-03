<!doctype html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用车申请</title>
    <meta name="description" content="">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="viewport" content="user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimal-ui" />
    <meta name="apple-mobile-web-app-status-bar-style" content="yes" />
    <style>
        body {
            font-size: 14px;
        }
        p {
            margin: 0 0 10px;
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
            padding: 10px 15px;
            background-color: #fff;
            border:1px solid #2ea9df;
        }
        .group-item .item-header {
            font-weight: 400;
            border-bottom:1px solid #2ea9df;
            margin:0 -15px;color:#2ea9df;
            padding:0 30px;
            line-height:38px;
        }
        .group-item .item-body {
            padding:15px;
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
        <span class="pull-left">用车申请</span>
        <span class="pull-right">${r.status!}</span>
    </h5>
    <div class="item-body">
        <p><span class="address-badge from">起</span> ${r.origin!}</p>
        <p style="padding-left:24px;"> ${r.start!}</p>
        <p><span class="address-badge to">终</span> ${r.destination!}</p>
        <p style="padding-left:24px;"> ${r.end!}</p>
        <p>人数: ${r.passengers!}</p>
        <p>事宜: ${r.reason!}</p>
        <assign class="bottom-bar clearfix"/>
        <#if coordinator?? && coordinator>
            <a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/applications/${r.applyId!}">申请单详情</a>
        </#if>

        <#if dispatcher?? && dispatcher>
            <a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/applications/${r.applyId!}/schedule/step1">派单</a>
            <a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/applications/${r.applyId!}">申请单详情</a>
        </#if>

        <#if driver && driver>
            <#--<a class="btn btn-default pull-right" href="${fleethost!}/mobile/#/applications/${r.applyId!}">申请单详情</a>-->
        </#if>
    </div>
</div>
</body>
</html>