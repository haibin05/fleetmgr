'use strict';

angular.module('gpsApp').controller('MapDebugCtrl', ['$scope', 'carsService',
    function($scope, carsService) {

        $scope.cars = [];

        $scope.params = {};

        $scope.doSearch = function() {
            //$scope.params.fleet = $scope.params.badge;
            carsService.getAll($scope.params, function(cars){
                $scope.cars = cars;
            });
        };
        $scope.doSearch();

    }]);