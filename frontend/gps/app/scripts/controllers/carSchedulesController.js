'use strict';

angular.module('gpsApp').controller('CarSchedulesCtrl', ['$scope', '$routeParams', '$timeout', 'carsService', 'mapService', 'utilService', 'constants',
    function($scope, $routeParams, $timeout, carsService, mapService, utilService, constants) {

    $scope.carId = $routeParams.id;

    $scope.selectedIndex = -1;

    $scope.schedules = [];

    $scope.carStatusMap = utilService.carStatusMap();

    $scope.status = {
        opened: false,
        td: new Date()
    };

    $scope.params = {
        day: new Date(),
        startTime: '00:00',
        endTime: '23:59'
    };

    $scope.openCalendar = function($event){
        $scope.status.opened = true;
        //hack to fix position
        setTimeout(function(){
            $('#searchPanel .dropdown-menu').css({
                left: 'auto',
                right: '-70px'
            });
        }, 1);
    };

    $scope.doSearch = function(){
        var startTime = $scope.params.startTime, endTime = $scope.params.endTime;
        var start = new Date($scope.params.day);
        start.setHours(parseInt(startTime.slice(0, startTime.indexOf(':'))), parseInt(startTime.slice(startTime.indexOf(':') + 1)), 0, 0);

        var end = new Date($scope.params.day);
        end.setHours(parseInt(endTime.slice(0, endTime.indexOf(':'))), parseInt(endTime.slice(endTime.indexOf(':') + 1)), 0, 0);
        carsService.getCarSchedules({
            id: $scope.carId,
            start: JSON.stringify(start).slice(1, -1),
            end: JSON.stringify(end).slice(1, -1)
        }, function(schedules){
            $scope.schedules = schedules;
        });
    };

    $scope.drawRoute = function(sch, index){
        $scope.selectedIndex = index;
        mapService.showScheduleInfoWindow(sch);
        var currentDate = new Date();
        if((new Date(sch.start)).getTime() <= currentDate.getTime() && currentDate.getTime() < (new Date(sch.end)).getTime()){//current schedule
            /*carsService.getCarPaths({
                id: $scope.carId,
                start: sch.start,
                end: JSON.stringify(currentDate).slice(1, -1)
            }, function(resp){
                mapService.drawPaths(resp);
            });*/
            if($scope.carStatus.gpsPoint){
                mapService.drawDrivingRoute($scope.carStatus.gpsPoint, sch.wayPoint);
            } else {
                mapService.drawDrivingRoute(sch.startPoint, sch.wayPoint);
            }
        } else {
            mapService.drawDrivingRoute(sch.startPoint, sch.wayPoint);
        }
    };

    var timer = null;
    var interval = constants.CAR_GPS_POLL_INTERVAL;
    var carMarker = null;
    var historyPoints = [];
    var syncGpsPosition = function(autoCenter){
        carsService.getCurrentStatus($scope.carId, function(status){
            $scope.carStatus = status;
            if(carMarker){
                mapService.removerMarker(carMarker);
            }
            if(historyPoints.length > 0){
                mapService.drawPolyline([historyPoints[historyPoints.length - 1], status.gpsPoint]);
            }
            historyPoints.push(status.gpsPoint);
            carMarker = mapService.showCarMarker($scope.car, status, !!autoCenter);
            timer = $timeout(syncGpsPosition, interval);
        });
    };

    $scope.$on("$destroy", function() {
        if(timer){
            $timeout.cancel(timer);
        }
    });

    (function(){
        $scope.doSearch();
        carsService.getById($scope.carId, function(car){
            $scope.car = car;
            syncGpsPosition(true);
        });
    })();

}]);