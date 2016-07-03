'use strict';

angular.module('gpsApp').controller('DriverSchedulesCtrl', ['$scope', '$routeParams', 'driversService', 'mapService', 'utilService',
    function ($scope, $routeParams, driversService, mapService, utilService) {

        $scope.driverId = $routeParams.id;

        $scope.schedules = [];

        $scope.status = {
            opened: false,
            td: new Date()
        };

        $scope.driverStatusMap = utilService.driverStatusMap();

        $scope.params = {
            day: new Date(),
            startTime: '00:00',
            endTime: '23:59'
        };

        $scope.openCalendar = function ($event) {
            $scope.status.opened = true;
            //hack to fix position
            setTimeout(function () {
                $('#searchPanel .dropdown-menu').css({
                    left: 'auto',
                    right: '-70px'
                });
            }, 1);

        };

        $scope.drawWay = function (sch, index) {
            $scope.selectedIndex = index;
            mapService.showScheduleInfoWindow(sch,$scope.driver);
            var currentDate = new Date();
            if ((new Date(sch.start)).getTime() <= currentDate.getTime() && currentDate.getTime() < (new Date(sch.end)).getTime()) {//current schedule
                /*driversService.getDriversPaths({
                    id: $scope.driverId,
                    start: sch.start,
                    end: JSON.stringify(currentDate).slice(1, -1)
                }, function (resp) {
                    if(resp) {
                        mapService.drawPaths(resp);
                    }
                });*/
                if($scope.driverStatus.gpsPoint) {
                    mapService.drawDrivingRoute($scope.driverStatus.gpsPoint, sch.wayPoint);
                } else {
                    mapService.drawDrivingRoute(sch.startPoint, sch.wayPoint);
                }
            } else {
                mapService.drawDrivingRoute(sch.startPoint, sch.wayPoint);
            }
            syncCarPositon();
        };

        $scope.doSearch = function () {
            var startTime = $scope.params.startTime, endTime = $scope.params.endTime;
            var start = new Date($scope.params.day);
            start.setHours(parseInt(startTime.slice(0, startTime.indexOf(':'))), parseInt(startTime.slice(startTime.indexOf(':') + 1)), 0, 0);
            var end = new Date($scope.params.day);
            end.setHours(parseInt(endTime.slice(0, endTime.indexOf(':'))), parseInt(endTime.slice(endTime.indexOf(':') + 1)), 0, 0);
            driversService.getDriverSchedules({
                id: $scope.driverId,
                start: JSON.stringify(start).slice(1, -1),
                end: JSON.stringify(end).slice(1, -1)
            }, function (schedules) {
                $scope.schedules = schedules;
            });
        };

        var carMarker = null;
        function syncCarPositon() {
            driversService.getById($scope.driverId, function (driver) {
                $scope.driver = driver;
                driversService.getCurrentState($scope.driverId, function (status) {
                    $scope.driverStatus = status;
                    status.driver = driver;
                    status.car.fleet = driver.fleet;
                    if(carMarker) {
                        mapService.removerMarker(carMarker);
                    }
                    carMarker = mapService.showCarMarker(status.car, status);
                });
            });
        }


        (function(){
            $scope.doSearch();
            syncCarPositon();
        })();


    }]);