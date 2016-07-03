'use strict';
angular.module('gpsApp').controller('AlarmsCtrl', ['$scope', 'alarmsService', 'carsService', 'mapService', 'utilService',
    function($scope, alarmsService, carsService, mapService, utilService) {

    $scope.alarms=[];

    $scope.alarmTypes = utilService.alarmTypesMap();

    $scope.selectedIndex = -1;

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

    $scope.$on("fleet.events",function(evt,message){
        $scope.doSearch();
    });

    var getParams = function(){
        var startTime = $scope.params.startTime, endTime = $scope.params.endTime;
        var start = new Date($scope.params.day);
        start.setHours(parseInt(startTime.slice(0, startTime.indexOf(':'))), parseInt(startTime.slice(startTime.indexOf(':') + 1)), 0, 0);
        var end = new Date($scope.params.day);
        end.setHours(parseInt(endTime.slice(0, endTime.indexOf(':'))), parseInt(endTime.slice(endTime.indexOf(':') + 1)), 0, 0);
        var params = {
            start: JSON.stringify(start).slice(1, -1),
            end: JSON.stringify(end).slice(1, -1)
        };
        if($scope.params.alarm){
            params.alarm = $scope.params.alarm;
        }
        return params;
    };
    $scope.export = function(){
        return alarmsService.exportToExcel(getParams());
    };
    $scope.doSearch = function(){
        alarmsService.getAlarms(getParams(), function(resultData){
            $scope.alarms = resultData.alarms;
            $scope.statistics = resultData.statistics;
            mapService.clearAll();
            $scope.$parent.alarmsOn = false;
        });
    };
    $scope.doSearch();

    $scope.showAlarmDetail = function(alarm, index){
        $scope.selectedIndex = index;
        alarm.visited = true;
        var carId = alarm.car.id;
        mapService.clearAll();
        carsService.getById(carId, function(car){
            carsService.getStatus(carId, alarm.gpsPoint.sampleTime, function(status){
                status.gpsPoint = alarm.gpsPoint;
                mapService.showCarMarker(car, status);
                if(alarm.car.depot && alarm.alarm != 'SPEEDING' && alarm.alarm != 'TIRED') {
                    mapService.drawCircle({
                        center: alarm.car.depot,
                        radius: 500
                    });
                    mapService.setViewport([alarm.gpsPoint, alarm.car.depot]);
                } else {
                    mapService.setViewport([alarm.gpsPoint]);
                }

            });
        });
    };

}]);
