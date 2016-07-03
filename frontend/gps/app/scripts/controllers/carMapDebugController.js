'use strict';

angular.module('gpsApp').controller('CarMapDebugCtrl', ['$scope', '$routeParams', 'carsService', 'mapService',
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

    $scope.openCalendar = function($event){
        $scope.status.opened = true;
        //hack to fix position
        setTimeout(function(){
            $('#searchPanel .dropdown-menu').css({
                left: 'auto',
                right: '-60px'
            });
        }, 1);
    };

    $scope.doSearch = function(){
        var params = {};
        var start = new Date($scope.params.day);
        start.setHours(parseInt($scope.params.startTime.slice(0, 2)), parseInt($scope.params.startTime.slice(3, -1)));
        var end = new Date($scope.params.day);
        end.setHours(parseInt($scope.params.endTime.slice(0, 2)), parseInt($scope.params.endTime.slice(3, -1)));
        carsService.getCarSinglePaths({
            id: $scope.carId,
            start: JSON.stringify(start).slice(1, -1),
            end: JSON.stringify(end).slice(1, -1)
        }, function(resp){
            //$scope.car = resp.car;
            $scope.paths = resp;
            for(var i = 0; i < resp.length; ++i){
                (function(path){
                    mapService.getLocation(path.gpsPoints[path.gpsPoints.length - 1], function(location){
                        path.targetLocation = location;
                    });
                })(resp[i]);
            }
            mapService.drawPaths(resp);
        });
    };


    $scope.showPathPortion = function(index){
        $scope.selectedIndex = index;
        mapService.showPathPortionWithMarker($scope.paths, index);
    };

    carsService.getById($scope.carId, function(car){
        $scope.car = car;
    });

}]);