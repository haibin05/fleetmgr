/*
 * Services
 */
'use strict';

angular.module('gpsApp').factory('mapService', ['$http', 'utilService', 'constants',
    function (http, utilService, constants) {

    var map = null;
    var pathSlider = null;
    var _scheduleInfoWindow = null;
    var _focusedPath = null;
    var _focusedMark = null;
    var _driving = null;

    var ROUTE_POLICY = [BMAP_DRIVING_POLICY_LEAST_TIME, BMAP_DRIVING_POLICY_LEAST_DISTANCE, BMAP_DRIVING_POLICY_AVOID_HIGHWAYS];
    var CURRENT_CITY = '嘉兴';

    var _drawRoute = function (cfg) {
        var route = cfg.routePlolicy ? ROUTE_POLICY[cfg.routePlolicy] : ROUTE_POLICY[0];
        var driving = new BMap.DrivingRoute(map, {
            renderOptions: {
                map: map,
                autoViewport: true
            },
            onPolylinesSet: function (lines, routes) {
                var points = lines[0].getPath();
                var polyline = new BMap.Polyline(points, {
                    strokeColor: 'blue',
                    strokeWeight: 4,
                    strokeOpacity: 0,
                    strokeStyle: 'dashed'
                });
                map.addOverlay(polyline);
            },
            policy: route
        });
        driving.search(cfg.start, cfg.end);
    };

    return {
        init: function () {
            // 百度地图API功能
            map = new BMap.Map("mapCt");    // 创建Map实例
            map.centerAndZoom(new BMap.Point(120.75, 30.771114), 15);  // 初始化地图,设置中心点坐标和地图级别
            //map.addControl(new BMap.MapTypeControl());   //添加地图类型控件
            map.addControl(new BMap.NavigationControl({
                offset: new BMap.Size(0, 70)
            }));  //添加地图缩放控件
            map.setCurrentCity(CURRENT_CITY);          // 设置地图显示的城市 此项是必须设置的
            map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放

            pathSlider = new PathSlider();
            _scheduleInfoWindow = new ScheduleInfoWindow();
            var _copyright = new CopyrightBadge();
            map.addControl(pathSlider);
            map.addControl(_scheduleInfoWindow);
            map.addControl(_copyright);

            return map;
        },
        showMarker: function (cfg) {
            var pt = new BMap.Point(cfg.position.lng, cfg.position.lat);
            var myIcon = new BMap.Icon(cfg.icon.src, new BMap.Size(cfg.icon.w, cfg.icon.h), {
                anchor: new BMap.Size(cfg.icon.w / 2, cfg.icon.h),
                imageOffset: new BMap.Size(cfg.icon.x, cfg.icon.y)
            });
            var marker = new BMap.Marker(pt, {icon: myIcon});  // 创建标注 isClear是否能被清楚
            map.addOverlay(marker);
            if(cfg.autoCenter !== false) {
                map.centerAndZoom(new BMap.Point(cfg.position.lng, cfg.position.lat), 15);
            }
            if (cfg.infoWindowContent) {
                var infoWindow = new BMap.InfoWindow(cfg.infoWindowContent, {offset: new BMap.Size(0, -40)});
                marker.addEventListener("click", function () {
                    this.openInfoWindow(infoWindow);
                });
                if(cfg.autoShowInfoWindow !== false) {
                    marker.openInfoWindow(infoWindow);
                }
            }
            return marker;
        },

        showCarMarker: function (car, status, autoCenter) {
            if (status.gpsPoint) {
                var carStatusMap = utilService.carStatusMap();
                var stateTag = '<span class="tag ' + carStatusMap[car.carState].cls + '">' +
                    carStatusMap[car.carState].name + '</span>';
                var sContent =
                    "<a href='#/cars/" + car.id + "/schedules'><h4 style='margin:0 0 5px 0;padding:0.2em 0'>" + car.badge + stateTag + "</h4></a><hr>" +
                    "<p><input type='text' style='width:100%;' placeholder='按Enter键发送语音播报' onmousedown='initBroadcastInput(event)' onkeydown='sendBroadcast(event)' broadcast-target-car='" + car.id + "'></p>"+
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>车队: " + (car.fleet ? car.fleet.name : '') + "</p>" +
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>驾驶员: " + (status.driver ? status.driver.name : '') + "</p>" +
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>手机号: " + (status.driver && status.driver.mobile ? status.driver && status.driver.mobile : '无') + (status.driver.phone ? " (" + status.driver.phone + ")": '') + "</p>" +
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>行驶速度: " + status.gpsPoint.speed + "km/h</p>" +
                    "<p style='margin:0;line-height:1.5;font-size:13px;'>采样时间: " + utilService.formatDateTime(status.gpsPoint.sampleTime) + "</p>" +
                    "<p style='text-align:center;'><a class='btn btn-default' style='font-size:13px;' href='#/cars/" + car.id + "/paths'>查看行车轨迹</a></p>";

                var marker = this.showMarker({
                    position: status.gpsPoint,
                    icon: {
                        src: 'images/icon-sprite02-160x40.png',
                        w: 40,
                        h: 40,
                        x: status.carState == 'INUSE' ? 0 : (status.carState == 'REPAIRING' ? -80 : -40),
                        y: 0
                    },
                    infoWindowContent: sContent,
                    autoCenter: autoCenter
                });
                return marker;
            }
        },

        removerMarker: function(marker){
            map.removeOverlay(marker);
        },

        showCarMarkerGroup: function (cars, autoCenter) {
            map.clearOverlays();
            var allPoints = [];
            for(var i = 0; i < cars.length; ++i){
                var car = cars[i];
                if (car.lastGps) {
                    var carStatusMap = utilService.carStatusMap();
                    var stateTag = '<span class="tag ' + carStatusMap[car.carState].cls + '">' +
                        carStatusMap[car.carState].name + '</span>';

                    var sContent = "<a href='#/cars/" + car.id + "/schedules'>" +
                            "<h4 style='margin:0 0 5px 0;padding:0.2em 0'>" + car.badge + stateTag + "</h4></a><hr>" +
                            "<p><input type='text' style='width:100%;' placeholder='按Enter键发送语音播报' onmousedown='initBroadcastInput(event)' onkeydown='sendBroadcast(event)' broadcast-target-car='" + car.id + "'></p>" +
                            "<p style='margin:0;line-height:1.5;font-size:13px;'>车队: " + (car.fleet ? car.fleet.name : '') + "</p>" +
                            "<p style='margin:0;line-height:1.5;font-size:13px;'>驾驶员: " + (car.driver ? car.driver.name : '无') + "</p>" +
                            "<p style='margin:0;line-height:1.5;font-size:13px;'>手机号: " + (car.driver && car.driver.mobile ? car.driver.mobile : '无') + (car.driver && car.driver.phone ? " (" + car.driver.phone + ")" : '') + "</p>" +
                            "<p style='margin:0;line-height:1.5;font-size:13px;'>行驶速度: " + car.lastGps.speed + "km/h</p>" +
                            "<p style='margin:0;line-height:1.5;font-size:13px;'>采样时间: " + utilService.formatDateTime(car.lastGps.sampleTime) + "</p>" +
                            "<p style='text-align:center;'><a class='btn btn-default' style='font-size:13px;' href='#/cars/" + car.id + "/paths'>查看行车轨迹</a></p>";
                    var marker = this.showMarker({
                        position: car.lastGps,
                        icon: {
                            src: 'images/icon-sprite02-160x40.png',
                            w: 40,
                            h: 40,
                            x: car.carState == 'INUSE' ? 0 : (car.carState == 'REPAIRING' ? -80 : -40),
                            y: 0
                        },
                        infoWindowContent: sContent,
                        autoShowInfoWindow: false,
                        autoCenter: false
                    });
                    var label = new BMap.Label(car.badge,{offset:new BMap.Size(-15,-25)});
                    label.setStyle({
                        display: 'block',
                        maxWidth:"200px",
                        minWidth:"70px",
                        height : "24px",
                        lineHeight : "24px",
                        background: '#fff',
                        textAlign: 'center',
                        border: '1px solid',
                        borderColor: carStatusMap[car.carState].color,
                        borderRadius: 12
                    });
                    marker.setLabel(label);
                    allPoints.push(new BMap.Point(car.lastGps.lng, car.lastGps.lat));
                }
            }
            if(autoCenter !== false) {
                map.setViewport(allPoints);
            }
        },

        drawCircle: function (cfg) {
            var point = new BMap.Point(cfg.center.lng, cfg.center.lat);
            var circle = new BMap.Circle(point, cfg.radius, {
                strokeColor: "red",
                strokeWeight: 2,
                strokeOpacity: 0.5,
                fillColor: '#2ea9df'
            });
            map.addOverlay(circle);
        },

        setViewport: function (points) {
            var allPoints = [];
            for (var i = 0; i < points.length; ++i) {
                var p = points[i];
                allPoints.push(new BMap.Point(p.lng, p.lat));
            }
            map.setViewport(allPoints);
        },

        getLocation: function (p, cb) {
            var point = new BMap.Point(p.lng, p.lat);
            var gc = new BMap.Geocoder();
            gc.getLocation(point, function (rs) {
                var addComp = rs.addressComponents;
                var result = addComp.district + ", " + addComp.street;
                if (addComp.streetNumber) {
                    result += ", " + addComp.streetNumber;
                }
                cb(result);
            });
        },

        getPoint: function (address, cb) {
            var gc = new BMap.Geocoder();
            gc.getPoint(address, function (point) {
                cb(point ? point : null);
            });
        },

        drawDrivingRoute: function (start, end) {
            var startPoint, endPoint;
            var myGeo = new BMap.Geocoder();
            if (start.lng) {
                startPoint = new BMap.Point(start.lng, start.lat);
            } else {
                myGeo.getPoint(start, function (point) {
                    if (point) {
                        startPoint = point;
                        if (endPoint) {
                            _drawRoute({
                                start: startPoint,
                                end: endPoint
                            });
                        }
                    } else {
                        console.log("无法解析出发地址:" + start);
                    }
                }, CURRENT_CITY);
            }
            if (end.lng) {
                endPoint = new BMap.Point(end.lng, end.lat);
                ;
                if (startPoint) {
                    _drawRoute({
                        start: startPoint,
                        end: endPoint
                    });
                }
            } else {
                myGeo.getPoint(end, function (point) {
                    if (point) {
                        endPoint = point;
                        if (startPoint) {
                            _drawRoute({
                                start: startPoint,
                                end: endPoint
                            });
                        }
                    } else {
                        console.log("无法解析目的地址:" + end);
                    }
                }, CURRENT_CITY);
            }
        },

        drawPaths: function (paths) {
            map.clearOverlays();

            var allPoints = [];
            for (var i = 0; i < paths.length; ++i) {
                var points = [];
                for (var j = 0; paths[i].adjustedPoints && j < paths[i].adjustedPoints.length; ++j) {
                    var p = paths[i].adjustedPoints[j];
                    var point = new BMap.Point(p.lng, p.lat);
                    points.push(point);
                    allPoints.push(point);
                }
                var polyline = new BMap.Polyline(points, {strokeColor: "blue", strokeWeight: 4, strokeOpacity: 0.5});
                map.addOverlay(polyline);
            }

            //map.centerAndZoom(new BMap.Point(paths[0].adjustedPoints[0].lng, paths[0].adjustedPoints[0].lat), 15);


            pathSlider.show({
                points: paths
            });

            map.setViewport(allPoints);
        },

        showPathPortion: function (paths, index) {
            var points = [];
            for (var i = 0; i < paths[index].adjustedPoints.length; ++i) {
                var p = paths[index].adjustedPoints[i];
                var point = new BMap.Point(p.lng, p.lat);
                points.push(point);
            }

            if (_focusedPath) {
                _focusedPath.remove();
            }
            _focusedPath = new BMap.Polyline(points, {strokeColor: "blue", strokeWeight: 6, strokeOpacity: 0});
            map.addOverlay(_focusedPath);
            map.setViewport(points);


            var result = 0;
            while (--index > -1) {
                result += paths[index].gpsPoints.length;
            }
            pathSlider.showPathPoint(result);
        },

        showPathPortionWithMarker: function (paths, index) {
            if (_focusedMark != null) {
                _focusedMark.remove();
            }
            if (_driving != null) {
                _driving.clearResults();
            }

            var point1 = paths[index].gpsPoints[0];
            var p1 = new BMap.Point(point1.lng, point1.lat);
            if (paths[index].gpsPoints.length > 1) {

                var point2 = paths[index].gpsPoints[paths[index].gpsPoints.length - 1];


                var p2 = new BMap.Point(point2.lng, point2.lat);
                _driving = new BMap.DrivingRoute(map, {renderOptions: {map: map, autoViewport: false}});
                _driving.search(p1, p2);
            } else {
                _focusedMark = new BMap.Marker(point1);  // 创建标注
                map.addOverlay(_focusedMark);               // 将标注添加到地图中
            }


            map.centerAndZoom(new BMap.Point(paths[index].gpsPoints[0].lng, paths[index].gpsPoints[0].lat), 16);


        },

        drawPolyline: function (gpsPoints) {
            var points = [];
            for (var i = 0; i < gpsPoints.length; ++i) {
                var p = gpsPoints[i];
                var point = new BMap.Point(p.lng, p.lat);
                points.push(point);
            }
            var polyline = new BMap.Polyline(points, {strokeColor: "blue", strokeWeight: 4, strokeOpacity: 0.5});
            map.addOverlay(polyline);
        },

        hidePathSlider: function () {
            pathSlider.hide();
        },

        showScheduleInfoWindow: function (sch,driver) {
            _scheduleInfoWindow.show(sch,driver);
        },

        hideScheduleInfoWindow: function (sch) {
            _scheduleInfoWindow.hide();
        },

        clearAll: function () {
            map.clearOverlays();
        },

        resetAll: function () {
            map.clearOverlays();
            map.centerAndZoom(new BMap.Point(120.75, 30.771114), 15);
        }

    };
}]);
