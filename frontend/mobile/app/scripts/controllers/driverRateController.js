'use strict';

angular.module('mobileApp').controller('DriverRateCtrl', ['$routeParams', '$location', '$scope', '$timeout', 'applicationService','h5ContainerService',
    function ($routeParams, $location, $scope, $timeout, applicationService, h5ContainerService) {

        $scope.finished = false;

        $scope.close = function (){h5ContainerService.closeH5Container()};

        $scope.submit = function (form) {
            $scope.processing = true;
            applicationService.rateDriver($scope.applyId, $scope.carId, $scope.rate, function () {
                $scope.processing = false;
                $scope.done = true;
            });
        };

        (function () {
            document.title = '评价';

            $scope.processing = false;
            $scope.done = false;
            $scope.rate = {
                driverRate: 5,
                carRate: 5,
                fleetRate: 5
            };
            $scope.applyId = $routeParams.id;
            $scope.carId = $routeParams.carId;
            applicationService.getDriverRate($routeParams.id, $routeParams.carId, function (rate) {
                if (!!rate.driverRate && rate.driverRate > 0) {
                    $scope.rate.driverRate = rate.driverRate;
                    $scope.rate.driverConcern = rate.driverConcern || '';
                    $scope.rate.carRate = rate.carRate;
                    $scope.rate.carConcern = rate.carConcern || '';
                    $scope.rate.fleetRate = rate.fleetRate;
                    $scope.rate.fleetConcern = rate.fleetConcern || '';
                    $scope.finished = true;
                }
            });
        })();

    }]);