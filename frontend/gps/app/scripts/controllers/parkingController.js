'use strict';


angular.module('gpsApp').controller('ParkingCtrl', ['$scope', '$timeout', 'mapService', 'carsService', 'depotsService', 'fleetsService', function ($scope, $timeout, mapService, carsService, depotsService, fleetsService) {

    $scope.cars = [];

    $scope.params = {gpsInstalled: 'true'};

    $scope.setting = false;

    $scope.newCar = null;

    $scope.fleets = [];

    $scope.selectedIndex = -1;

    var icon_src = {
        selIcon: 'images/icon_parking_select_64x64.png',
        temIcon: 'images/icon_parking_search_64x64.png',
        posIcon: 'images/icon_parking_position_54x54.png'
    }

    var markerIcon = {
        selMarker: new BMap.Icon(icon_src.selIcon, new BMap.Size(64, 64), {anchor: new BMap.Size(32, 64)}),
        temMarker: new BMap.Icon(icon_src.temIcon, new BMap.Size(64, 64), {anchor: new BMap.Size(32, 64)}),
        posMarker: new BMap.Icon(icon_src.posIcon, new BMap.Size(54, 54), {anchor: new BMap.Size(27, 54)})
    };

    var searchMarker = null;//

    var markerAll = [];     //所有停车场地图上覆盖物

    var markerCnt = [];

    var showMarker = null;  //当前覆盖物

    var map = mapService.init();

    function setPlace() {

        map.removeOverlay(searchMarker);

        function myFun() {
            $scope.newCar.depot.lng = local.getResults().getPoi(0).point.lng;
            $scope.newCar.depot.lat = local.getResults().getPoi(0).point.lat;

            searchMarker = mapService.showMarker({
                position: local.getResults().getPoi(0).point,
                icon: {
                    src: icon_src.temIcon,
                    w: 64,
                    h: 64,
                    x: 0,
                    y: 0
                }
            });
        }

        var local = new BMap.LocalSearch(map, { //智能搜索
            onSearchComplete: myFun
        });
        local.search($scope.newCar.depot.name);
    }

    function infoWindow(depot) {
        var marker = mapService.showMarker({
            position: depot,
            icon: {
                src: icon_src.posIcon,
                w: 54,
                h: 54,
                x: 0,
                y: 0
            }
        });
        markerAll[depot.lng + '_' + depot.lat] = marker;

        var sContent =
            '<div id="infoWindow">' +
            '<hr/>' +
            '<p>地址：' + depot.name + '</p>' +
            '<div style="width: 100%;display: block;text-align: center;">' +
            '<button class="btn btn-default text-center btn-setparking">设为停车场</button></div>' +
            '</div>';

        var infoWindow = new BMap.InfoWindow(sContent, {
            title: '<strong>停车场信息</strong>',
            offset: new BMap.Size(0, -64)
        });
        marker.addEventListener("click", function (e) {

            this.openInfoWindow(infoWindow);

            var btn = $('#infoWindow').find('.btn-setparking');

            btn.on('click', function (event) {

                var pt = infoWindow.getPosition();
                map.removeOverlay(searchMarker);
                mapService.getLocation(pt, function (rs) {
                    searchMarker = markerAll[pt.lng + '_' + pt.lat];
                    $timeout(function () {
                        $scope.newCar.depot.name = rs;
                        $scope.newCar.depot.lng = pt.lng;
                        $scope.newCar.depot.lat = pt.lat;
                        $scope.setting = false;
                        $scope.setFleetParking();

                    });
                    marker.closeInfoWindow();

                });
            });

            if (!$scope.setting || showMarker.marker === marker) {
                btn.hide();
            }

        });

        return marker;

    }

    function showMarkerAll(cars) {
        //map.clearOverlays();

        angular.forEach(cars, function (car) {
            var marker = null;
            if (car.depot) {
                if (markerAll[car.depot.lng + '_' + car.depot.lat]) {
                    markerCnt[car.depot.lng + '_' + car.depot.lat]++;
                } else {
                    markerCnt[car.depot.lng + '_' + car.depot.lat] = 1;
                    infoWindow(car.depot);
                }
            }

        });
    }

    function searchComplete(e) {
        if ($scope.setting === false) {
            ac.hide();
        }
    }

    var flag = false;
    map.addEventListener("click", function (e) {
        if ($scope.setting && !e.overlay && location.hash.indexOf('parking')>=0) {
            var pt = e.point;
            if (flag == true) {
                map.removeOverlay(searchMarker);
                flag = false;
            }
            mapService.getLocation(pt, function (rs) {
                $timeout(function () {
                    $scope.newCar.depot.name = rs;
                    $scope.newCar.depot.lng = e.point.lng;
                    $scope.newCar.depot.lat = e.point.lat;
                    if (!flag) {
                        searchMarker = new BMap.Marker(pt, {icon: markerIcon.temMarker});  // 创建标注
                        map.addOverlay(searchMarker);
                        flag = true;
                    }
                });
            });
        }
    });

    var ac = new BMap.Autocomplete(    //建立一个自动完成的对象
        {
            "input": "suggestId", "location": map, "onSearchComplete": searchComplete
        });

    ac.addEventListener("onconfirm", function (e) {    //鼠标点击下拉列表后的事件
        console.log('1');
        var _value = e.item.value;
        $timeout(function () {
            $scope.newCar.depot.name = _value.province + _value.city + _value.district + _value.street + _value.business;
            setPlace();
        });

    });

    $scope.searchKeyDown = function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            $scope.doSearch();
        }
    };

    $scope.searchAddressKeyDown = function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            $scope.doSearchAddress();
        }
    };

    $scope.doSearchAddress = function () {

        mapService.getPoint($scope.newCar.depot.name, function (point) {
            if (point) {
                map.removeOverlay(searchMarker);
                searchMarker = mapService.showMarker({
                    position: point,
                    icon: {
                        src: 'images/driver_busy_64x64.png',
                        w: 64,
                        h: 64,
                        x: 0,
                        y: 0
                    }
                });
                $scope.newCar.depot.lng = point.lng;
                $scope.newCar.depot.lat = point.lat;
            }
            //else {
            //    alert('没有此地址！');
            //}
        });

    };

    $scope.getCarPosition = function (car, index) {
        $scope.selectedIndex = index;
        if ($scope.setting) {
            $scope.showSetting(null, car);
        }
        if (showMarker) {
            showMarker.setIcon(markerIcon.posMarker);
            showMarker = null;
        }
        if (car.depot) {
            showMarker = markerAll[car.depot.lng + '_' + car.depot.lat];
            showMarker.setIcon(markerIcon.selMarker);
            map.centerAndZoom(new BMap.Point(car.depot.lng, car.depot.lat), map.getZoom());
        }
    };

    $scope.showSetting = function ($event, car) {
        map.removeOverlay(searchMarker);
        $scope.oldCar = car;
        $scope.newCar = angular.copy(car);
        if (!$scope.newCar.depot) {
            $scope.newCar.depot = {name: null, lng: null, lat: null};
        }
        $scope.setting = true;

        //$event.stopPropagation();
        //$event.preventDefault();
    };

    $scope.cancelSetting = function () {
        $scope.setting = false;
        if (searchMarker) {
            map.removeOverlay(searchMarker);
        }
    }

    $scope.setFleetParking = function () {
        if ($scope.newCar.depot.name) {
            carsService.setDepotById($scope.newCar.id, $scope.newCar.depot, function (car) {

                $scope.setting = false;


                if ($scope.oldCar.depot && --markerCnt[$scope.oldCar.depot.lng + '_' + $scope.oldCar.depot.lat] > 0) {
                    showMarker.setIcon(markerIcon.posMarker);
                } else {
                    map.removeOverlay(showMarker);
                }

                markerCnt[car.depot.lng + '_' + car.depot.lat]++;

                if (searchMarker) {
                    searchMarker.setIcon(markerIcon.posMarker);
                }
                markerAll[car.depot.lng + '_' + car.depot.lat] = searchMarker;

                showMarker = markerAll[car.depot.lng + '_' + car.depot.lat];

                showMarker.setIcon(markerIcon.selMarker);

                searchMarker = null;

                $timeout(function () {
                    var obj = angular.copy(car);
                    $scope.oldCar.depot = {
                        name: obj.depot.name,
                        lng: obj.depot.lng,
                        lat: obj.depot.lat
                    };
                });

            });
        } else {
            alert("地址不能为空");
        }
    }

    $scope.doSearch = function () {
        var params = {
            cache: false
        };
        angular.extend(params, $scope.params);
        carsService.getAll(params, function (cars) {
            $scope.cars = cars;
            showMarkerAll($scope.cars);
        });
    };

    fleetsService.getAll({cache: false}, function (fleets) {
        $scope.fleets = fleets;
    });

    $scope.doSearch();

}
])
;