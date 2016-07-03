'use strict';

angular.module('mobileApp').controller('CarsCtrl', ['$location', '$scope', '$timeout', 'carsService', 'fleetsService','h5ContainerService',
    function ($location, $scope, $timeout, carsService, fleetsService,h5ContainerService) {

        $scope.fleets = [];

        $scope.cars = [];

        $scope.params = {
            $limit: 20,
            $offset: 0
        };

        $scope.close = function (){h5ContainerService.closeH5Container()};

        $scope.loadMore = function () {
            $scope.loading = true;
            carsService.query($scope.params, function (cars) {
                angular.forEach(cars, function (o) {
                    $scope.cars.push(o);
                });
                $scope.loading = false;
            });
            $scope.params.$offset += $scope.params.$limit;
        };

        $scope.doSearch = function () {
            $scope.cars = [];
            $scope.params.$offset = 0;
            $scope.loadMore();
        };

        $scope.$watch('params.keyword', function (val) {
            $scope.doSearch();
        });


        (function () {
            document.title = '车辆查询';

            $timeout(function() {
                fleetsService.getAll($scope.params, function (fleets) {
                    $scope.fleets = fleets;
                });
            });
        })();
    }]);