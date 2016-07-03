'use strict';

angular.module('gpsApp').controller('DriversCtrl', ['$scope', '$window', '$location', 'driversService','fleetsService', 'mapService', 'utilService',
    function ($scope, $window, $location, driversService,fleetsService,mapService,utilService) {

    function getDrivers() {
        driversService.getAll($scope.params, function (driver) {
            $scope.drivers = driver;
        });
    }
    function getFleets() {
        fleetsService.getAll({}, function (fleets) {
            $scope.fleets = fleets;
        });
    }

    $scope.showDriverPos = function (driverId) {
        driversService.getCurrentState(driverId, function (status) {
            mapService.showMarker({
                position: status.gpsPoint,
                icon: status.carState == 'IDLE' ? 'images/marker_car_idle_80x80.png' : (status.carState == 'WORK' ? 'images/marker_car_busy_80x80.png' : 'images/marker_car_repair_80x80.png'),
                width: 80,
                height: 80
            });
        });
    };

    $scope.driverStatus = function (driver) {
        if (driver.driverStatus == 'LEAVE') {
            mapService.showScheduleInfoWindow(null,driver);
        } else {
            //#drivers/{{driver.id}}/schedules
            $window.location.href ='#drivers/'+driver.id+'/schedules';
        }
    };

    $scope.searchKeyDown = function (event) {
        if (event.keyCode === 13) {
            event.preventDefault();
            $scope.doSearch();
        }
    };

    $scope.doSearch = function () {
        getDrivers();
    };

    $scope.filterBy = function (type) {
        $scope.ui.filterType = type;
        type === 'All' ? $scope.params.status=null : $scope.params.status = type;
    };

    function init() {
        $scope.drivers = [];
        $scope.params = [];
        $scope.fleets = [];
        $scope.ui = {
            filterType: 'All'
        };
        $scope.driverStatusMap = utilService.driverStatusMap();
        getDrivers();
        getFleets();
    }

    init();

}]);