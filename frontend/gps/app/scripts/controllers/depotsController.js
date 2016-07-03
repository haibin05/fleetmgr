'use strict';

angular.module('gpsApp').controller('DepotsCtrl', ['$scope', 'depotsService',
    function($scope, depotsService) {

        $scope.mode = 'notInDepot';

        $scope.carsNotInDepot = {};
        $scope.carsInDepot = {};

        $scope.status = {
            inDepotOpened: false,
            notInDepotOpened: false,
            td: new Date()
        };
        $scope.openCalendar = function (opened) {
            $scope.status[opened] = true;
        };

        var day = new Date();
        day.setDate(day.getDate() - 1);
        $scope.notInDepotParams = {
            day: day
        };

        day = new Date();
        day.setDate(day.getDate() - 1);
        $scope.inDepotParams = {
            day: day,
            startTime: '00:00',
            endTime: '23:59'
        };



//  the response is an array of car and time objects
//        [
//          {
//            car :{id: id, budge:budge...},
//            event:time
//          }
//        ]
	    var processResp = function(cars){
            var result = {}, i = 0;
            for(; i < cars.length; ++i){
                var car = cars[i].car;
                var fleet = car.fleet && car.fleet.id? car.fleet.id : '-1';

                if(!result[fleet]){
                    result[fleet] = fleet == '-1'? {name: '其它', cars:[]}: {name: car.fleet.name, cars:[]};
                }
                result[fleet].cars.push({
                    id: car.id,
                    badge: car.badge,
                    carState: car.carState,
                    driver:car.driver,
                    eventTime: moment(cars[i].eventTime)
                });
            }
            return result;
	    };
        $scope.doSearch = function(){
            if($scope.mode == 'notInDepot'){
                var date = new Date($scope.notInDepotParams.day);
                date.setHours(0, 0, 0, 0);
                depotsService.getCarsNotInDepot({
                    date: JSON.stringify(date).slice(1, -1)
                }, function(resp){
                    $scope.carsNotInDepot = processResp(resp);
                });
            } else {
                var startTime = $scope.inDepotParams.startTime, endTime = $scope.inDepotParams.endTime;
                var start = new Date($scope.inDepotParams.day);
                start.setHours(parseInt(startTime.slice(0, startTime.indexOf(':'))), parseInt(startTime.slice(startTime.indexOf(':') + 1)), 0, 0);
                var end = new Date($scope.inDepotParams.day);
                end.setHours(parseInt(endTime.slice(0, endTime.indexOf(':'))), parseInt(endTime.slice(endTime.indexOf(':') + 1)), 0, 0);
                depotsService.getCarsInDepot({
                    start: JSON.stringify(start).slice(1, -1),
                    end: JSON.stringify(end).slice(1, -1)
                }, function(resp){
                    $scope.carsInDepot = processResp(resp);
                });
            }
        };

        $scope.toggleMode = function(mode) {
            $scope.mode = mode;
            $scope.doSearch();
        };

        $scope.isEarly = function(date) {
            return (new Date(date)).getHours() <= 6;
        };

        $scope.isLate = function(date) {
            return (new Date(date)).getHours() >= 21;
        };

        $scope.doSearch();

}]);