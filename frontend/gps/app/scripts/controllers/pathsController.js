'use strict';

angular.module('gpsApp').controller('PathsCtrl', ['$scope', 'carsService', 'fleetsService', 'utilService',
    function($scope, carsService, fleetsService, utilService) {

    $scope.cars = [];

    $scope.params = {gpsInstalled: 'true'};

    $scope.fleets = [];

    $scope.carStatus = utilService.carStatusMap();

    $scope.searchKeyDown = function(event) {
        if (event.keyCode === 13){
            event.preventDefault();
            $scope.doSearch();
        }
    };
    $scope.doSearch = function() {
        carsService.getAll($scope.params, function(cars){
            $scope.cars = cars;
        });
    };
    $scope.doSearch();


    fleetsService.getAll({}, function(fleets){
        $scope.fleets = fleets;
    });

}]);