'use strict';

function PathSlider(){
    // 默认停靠位置和偏移量
    this.defaultAnchor = BMAP_ANCHOR_BOTTOM_LEFT;
    this.defaultOffset = new BMap.Size(0, 50);
}

// 通过JavaScript的prototype属性继承于BMap.Control
PathSlider.prototype = new BMap.Control();

// 自定义控件必须实现自己的initialize方法,并且将控件的DOM元素返回
// 在本方法中创建个div元素作为控件的容器,并将其添加到地图容器中
PathSlider.prototype.initialize = function(map){
    var that = this;
    //icon: status.carState == 'IDLE' ? 'images/marker_car_idle_80x80.png' : (status.carState == 'INUSE' ? 'images/marker_car_busy_80x80.png' : 'images/marker_car_repair_80x80.png'),

    var sliderTooltip = function(event, ui) {
        var index = ui.value || 0;
        if(that.points && that.points.length > 0 && that.points[index]){
            var p = that.points[index];
            var tip = '<div class="tip">' + that.formatTime(p.sampleTime) + '</div>';
            $('.ui-slider-handle').html(tip);

            if(that.currentMarker){
                that.currentMarker.remove();
            }
            var pt = new BMap.Point(p.lng, p.lat);
            var myIcon = new BMap.Icon('images/icon-sprite02-160x40.png', new BMap.Size(40,40), {
                anchor: new BMap.Size(20,40),
                imageOffset: new BMap.Size(0, 0)
            });
            var infoWindow = new BMap.InfoWindow(that.getMarkerInfo(p), {offset: new BMap.Size(0,-40)});
            that.currentMarker = new BMap.Marker(pt,{icon:myIcon});  // 创建标注
            that.currentMarker.addEventListener("click", function() {
                this.openInfoWindow(infoWindow);
            });
            map.addOverlay(that.currentMarker);
            that.currentMarker.openInfoWindow(infoWindow);
        }
    };

    var sliderTooltipAndZoom = function(event, ui) {
        sliderTooltip(event, ui);
        var index = ui.value || 0;
        if(that.points && that.points.length > 0) {
            var p = that.points[index];
            map.centerAndZoom(new BMap.Point(p.lng, p.lat), 15);
        }
    };

    var ct = $('<div class="path-slider-wrapper"><div class="start-time"></div><div class="path-slider"></div><div class="end-time"></div>');
    ct.find('.path-slider').slider({
        orientation: "horizontal",
        range: "min",
        max: 100,
        value: 0,
        create: sliderTooltip,
        slide: sliderTooltipAndZoom,
        change: sliderTooltip
    });

    map.getContainer().appendChild(ct[0]);
    // 将DOM元素返回

    this.el = ct;
    this.points = [];
    this.currentMarker = null;
    return ct[0];
}

PathSlider.prototype.show = function(cfg){
    var points = cfg.points;
    this.el.find('.start-time').html(this.formatTime(points[0].start));
    this.el.find('.end-time').html(this.formatTime(points[points.length - 1].end));
    var result  = [];
    for(var i = 0; i < points.length; ++i){
        var gpsPoints = points[i].gpsPoints;
        for(var j = 0; gpsPoints && j < gpsPoints.length; ++j) {
            gpsPoints[j].car = points[i].car;
            gpsPoints[j].driver = points[i].driver;
            gpsPoints[j].alarms = points[i].alarms;
            gpsPoints[j].moving = points[i].moving;
            if(!points[i].moving){
                gpsPoints[j].stopTime = this.getTimeDiff(points[i].start, points[i].end);
            }
        }
        result = result.concat(gpsPoints)
    }
    this.points = result;

    this.el.find('.path-slider').slider('option',{
        max: this.points.length - 1,
        value:0
    });
    this.el.css('left', '50%');
    this.el.show();
}

PathSlider.prototype.showPathPoint = function(index){
    this.el.find('.path-slider').slider('option',{
        value:index
    });
}

PathSlider.prototype.hide = function(map){
    this.el.hide();
}

PathSlider.prototype.formatTime = function(date){
    var day = new Date(Date.parse(date));
    return moment(day).format('HH:mm');
};

PathSlider.prototype.formatDateTime = function(date){
    var day = new Date(Date.parse(date));
    return moment(day).format('YYYY-MM-DD HH:mm');
};

PathSlider.prototype.getTimeDiff = function(start, end){
    var startTime = moment(Date.parse(start)), endTime = moment(Date.parse(end));
    var duration = moment.duration(endTime.diff(startTime));
    var h = duration.get("hours");
    var m = duration.get("minutes");
    var s = duration.get("seconds");
    return (h > 0?  h + '小时' : '') + (m > 0?  m + '分' : '') + (s > 0?  s + '秒' : '');
}

PathSlider.prototype.getMarkerInfo = function(p){
    var stateTag = '<span class="tag red">超速</span>';
    if(p.moving && !p.alarms){
        stateTag = '<span class="tag blue">正常</span>';
    } else if(!p.moving && !p.alarms){
        stateTag = '<span class="tag yellow">停车</span>';
    }
    var sContent =
        "<h4 style='margin:0 0 5px 0;padding:0.2em 0'>" + p.car.badge + stateTag + "</h4><hr>" +
        "<p style='margin:0;line-height:1.5;font-size:13px;'>车队: " + (p.car.fleet ? p.car.fleet.name : '') + "</p>" +
        "<p style='margin:0;line-height:1.5;font-size:13px;'>驾驶员: " + (p.driver? p.driver.name : '无') + "</p>" +
        "<p style='margin:0;line-height:1.5;font-size:13px;'>手机号: " + (p.driver.mobile ? p.driver.mobile : '无') + (p.driver.phone ? " (" + p.driver.phone + ")" : '') + "</p>" +
        (p.stopTime? "": "<p style='margin:0;line-height:1.5;font-size:13px;'>行驶速度: " + p.speed + "km/h</p>") +
        (p.stopTime? "<p style='margin:0;line-height:1.5;font-size:13px;color:#2ea9df;'>停留时长: " + p.stopTime + "</p>" : "") +
        "<p style='margin:0;line-height:1.5;font-size:13px;'>采样时间: " + this.formatDateTime(p.sampleTime) + "</p>" +
        "</div>";
    return sContent;
}