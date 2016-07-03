'use strict';

angular.module('gpsApp').controller('CarsCtrl', ['$scope', '$timeout', 'carsService', 'fleetsService', 'alarmsService', 'mapService', 'utilService','constants',
    function($scope, $timeout, carsService, fleetsService, alarmsService, mapService, utilService, constants) {

    $scope.cars = [];

    $scope.fleets = [];

    $scope.params = {gpsInstalled: 'true'};

    $scope.carStatus = utilService.carStatusMap();

    $scope.searchKeyDown = function(event) {
        if (event.keyCode === 13){
            event.preventDefault();
            $scope.doSearch();
        }
    };

    var timer = null;
    var interval = constants.CARS_GROUP_GPS_POLL_INTERVAL;
    var syncGpsPosition = function(autoCenter){
        var params = {
            lastGps: true
            //cache: false
        };
        angular.extend(params, $scope.params);
        carsService.getAll(params, function(cars){
            mapService.showCarMarkerGroup(cars, !!autoCenter);
            timer = $timeout(syncGpsPosition, interval);
        });

        alarmsService.getAlarms({$limit:1},function(resp){
            var s = resp.statistics;
            //var s = {countOfAllCar:0,countOfGpsCar:0,countOfSchedule:0,countOfOutCar:0,countOfAlarm:0,countOfNotReturnCar:0}
            $('#mapCt .statistics-info-bar').remove();
            $(['<div class="statistics-info-bar clearfix">',
                    '<div class="bar-head"><i class="icon-statistics"></i>统计数据</div>',
                    '<a class="bar-section" data-value="all"><h5>', s.countOfAllCar ,'</h5><p>车辆数</p></a>',
                    '<div class="bar-splitter"></div>',
                    '<a class="bar-section" data-value="gps"><h5>', s.countOfGpsCar ,'</h5><p>众业GPS车辆</p></a>',
                    '<div class="bar-splitter"></div>',
                    '<div class="bar-section"><h5>', s.countOfSchedule ,'</h5><p>当日任务</p></div>',
                    '<div class="bar-splitter"></div>',
                    '<a class="bar-section" data-value="inuse"><h5>', s.countOfOutCar ,'</h5><p>当前出车</p></a>',
                    '<div class="bar-splitter"></div>',
                    '<a class="bar-section" href="#alarms"><h5>', s.countOfAlarm ,'</h5><p>当日告警</p></a>',
                    '<div class="bar-splitter"></div>',
                    '<a class="bar-section" href="#depots"><h5>', s.countOfNotReturnCar ,'</h5><p>昨日21:00未回厂</p></a>',
                '</div>'].join('')).appendTo($('#mapCt'));
            $('#mapCt .statistics-info-bar').on('click', '.bar-section', function(){
                var isolatedScope = angular.element($('#searchPanel .status-picker')[0]).isolateScope();
                isolatedScope.clear();
                switch($(this).data('value')){
                    case 'all': $scope.params = {}; $scope.doSearch(); break;
                    case 'gps': $scope.params = {gpsInstalled: 'true'}; isolatedScope.gpsInstalled = {'众业GPS车辆':true}; $scope.doSearch(); break;
                    case 'inuse': $scope.params = {state: 'INUSE',gpsInstalled: 'true'}; isolatedScope.state = {'出车':'INUSE'}; isolatedScope.gpsInstalled = {'众业GPS车辆':true}; $scope.doSearch(); break;
                }
            });
        });
    };
    $scope.doSearch = function() {
        carsService.getAll($scope.params, function(cars){
            $scope.cars = cars;
        });

        if(timer){
            $timeout.cancel(timer);
        }
        syncGpsPosition(true);
    };

    $scope.$on("$destroy", function() {
        if(timer){
            $timeout.cancel(timer);
        }
        $('#mapCt .statistics-info-bar').remove();
    });


    (function(){
        $scope.doSearch();

        fleetsService.getAll({}, function(fleets){
            $scope.fleets = fleets;
        });

    })();

}]);