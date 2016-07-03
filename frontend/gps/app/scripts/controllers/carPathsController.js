'use strict';

angular.module('gpsApp').controller('CarPathsCtrl', ['$scope', '$routeParams', 'carsService', 'mapService',
    function($scope, $routeParams, carsService, mapService) {

    $scope.carId = $routeParams.id;

    $scope.selectedIndex = -1;

    $scope.paths = [];

    $scope.status = {
        opened: false,
        td: new Date()
    };

    $scope.params = {
        day: new Date(),
        startTime: '00:00',
        endTime: '23:59'
    };


    $scope.totalDistance = 0;
    $scope.scheduleDistance = 0;

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

    var getParams = function(){
        var startTime = $scope.params.startTime, endTime = $scope.params.endTime;
        var start = new Date($scope.params.day);
        start.setHours(parseInt(startTime.slice(0, startTime.indexOf(':'))), parseInt(startTime.slice(startTime.indexOf(':') + 1)), 0, 0);
        var end = new Date($scope.params.day);
        end.setHours(parseInt(endTime.slice(0, endTime.indexOf(':'))), parseInt(endTime.slice(endTime.indexOf(':') + 1)), 0, 0);
        return {
            id: $scope.carId,
            start: JSON.stringify(start).slice(1, -1),
            end: JSON.stringify(end).slice(1, -1)
        };
    };
    $scope.export = function(){
        return carsService.exportCarPathsToExcel(getParams());
    };
    $scope.doSearch = function(){
        carsService.getCarPaths(getParams(), function(resp){
            $scope.paths = resp;
            var d = 0;
            for(var i = 0; i < resp.length; ++i){
                (function(path){
                    mapService.getLocation(path.gpsPoints[path.gpsPoints.length - 1], function(location){
                        path.targetLocation = location;
                        $scope.$apply();
                    });
                })(resp[i]);
                d += resp[i].km;
            }
            $scope.totalDistance = d.toFixed(2);
            mapService.clearAll();
            mapService.drawPaths(resp);
        });

        carsService.getCarSchedules(getParams(), function(schedules){
            $scope.schedules = schedules;
            var d = 0;
            for(var i = 0; i < schedules.length; ++i){
                var scc = schedules[i].scheduleCars;
                if(scc && scc.length > 0){
                    d += scc[0].km;
                }
            }
            $scope.scheduleDistance = d.toFixed(2);
        });
    };


    $scope.showPathPortion = function(index){
        $scope.selectedIndex = index;
        mapService.showPathPortion($scope.paths, index);
    };


    (function(){
        carsService.getById($scope.carId, function(car){
            $scope.car = car;
        });
        $scope.doSearch();

    })();

}]);