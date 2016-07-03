'use strict';

function ScheduleInfoWindow() {
    // 默认停靠位置和偏移量
    this.defaultAnchor = BMAP_ANCHOR_TOP_RIGHT;
    this.defaultOffset = new BMap.Size(10, 40);
}

ScheduleInfoWindow.prototype = new BMap.Control();


ScheduleInfoWindow.prototype.initialize = function (map) {

    var ct = $('<div style="border:1px solid #f5f5f5;background: #fff;width:200px;padding:10px;display: none;"></div>');


    map.getContainer().appendChild(ct[0]);
    // 将DOM元素返回

    this.el = ct;
    return ct[0];
}

ScheduleInfoWindow.prototype.show = function (sch, driver) {
    //var sContent =
    //    "<h4 style='margin:0 0 5px 0;padding:0.2em 0;color:#ff9300;'>任务</h4><hr style='margin:4px 0;'>" +
    //    "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;border-radius:50%;background: green;'></span>" + sch.startPoint + "</p>" +
    //    "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;'></span>" + this.formatTime(sch.start) + "</p>" +
    //    "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;border-radius:50%;background: red;'></span>" + sch.wayPoint + "</p>" +
    //    "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;'></span>" + this.formatTime(sch.end) + "</p>";
    var sContent='';
    if (driver && driver.driverStatus ==='LEAVE' ) {
        sContent +=
            "<h4 style='margin:0 0 5px 0;padding:0.2em 0;'>" + driver.name + "<span class='tag red'>请假</span></h5><hr style='margin:4px 0;'>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'>驾照类型：" + driver.licenseClass +
            "<p style='margin:0;line-height:1.5;font-size:13px;'>手机号：" + (driver.mobile ? driver.mobile : '无') + (driver.phone ? " (" + driver.phone + ")" : '') + "</p><br>";
            //"<p style='margin:0;line-height:1.5;font-size:13px;'>请假时间：" + "</p><br>" ;
    }else if(driver){
        sContent +=
            "<h4 style='margin:0 0 5px 0;padding:0.2em 0;'>" + driver.name + "</h5><hr style='margin:4px 0;'>" +
            //"<p style='margin:0;line-height:1.5;font-size:13px;'>车牌号：" + "浙F97846" + "</p>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'>手机号：" + (driver.mobile ? driver.mobile : '无') + (driver.phone ? " (" + driver.phone + ")": '') + "</p>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'>驾照类型：" + driver.licenseClass + "</p><br>" ;
    }
    if(sch) {
        sContent +=
            "<h4 style='margin:0 0 5px 0;padding:0.2em 0;color:#ff9300;'>任务</h4><hr style='margin:4px 0;'>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;border-radius:50%;background: green;'></span>" + sch.startPoint + "</p>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;'></span>" + this.formatDateTime(sch.start) + "</p>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;border-radius:50%;background: red;'></span>" + sch.wayPoint + "</p>" +
            "<p style='margin:0;line-height:1.5;font-size:13px;'><span style='display:inline-block;width:10px;height:10px;'></span>" + this.formatDateTime(sch.end) + "</p>";
        if(sch.scheduleCars){
            for(var i = 0; i < sch.scheduleCars.length; ++i){
                var car = sch.scheduleCars[i].car? sch.scheduleCars[i].car.badge : '';
                var driver = sch.scheduleCars[i].driver? sch.scheduleCars[i].driver.name : '';
                sContent +=
                    "<hr style='margin:4px 0;'><p style='margin:0;line-height:1.5;font-size:13px;'>车辆: " + car + "</p>" +
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>驾驶员: " + driver + "</p>";
            }
        }
    }

    this.el.html(sContent);
    this.el.show();
}


ScheduleInfoWindow.prototype.hide = function (map) {
    this.el.hide();
}

ScheduleInfoWindow.prototype.formatTime = function (date) {
    var day = new Date(Date.parse(date));
    var h = day.getHours();
    var m = day.getMinutes();
    return (h < 10 ? ('0' + h) : h) + ':' + (m < 10 ? ('0' + m) : m);
}

ScheduleInfoWindow.prototype.formatDateTime = function(date){
    var day = new Date(Date.parse(date));
    return moment(day).format('YYYY-MM-DD HH:mm');
};

//global function for sending broadcast
window.sendBroadcast = function(event){
    var e = event || window.event;
    var key = e.keyCode || e.which;
    var fn = angular.element(document.body).scope().sendBroadcast;
    if(e.which == 13){
        var target = $(e.target);
        var targetCar = target.attr('broadcast-target-car');
        if(targetCar && target.val()){
            fn(targetCar, target.val(), function(){
                target.val('');
            });
        }
    }
};

//global function for sending broadcast
window.initBroadcastInput = function(event){
    var e = event || window.event;
    angular.element(document.body).scope().initBroadcastInput(e.target);
};