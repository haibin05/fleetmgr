'use strict';

angular.module('mobileApp').controller('ApplicationRateCtrl', ['$routeParams', '$location', '$scope', 'applicationService', 'h5ContainerService',
    function ($routeParams, $location, $scope, applicationService, h5ContainerService) {

        $scope.close = function (){h5ContainerService.closeH5Container()};

        $scope.submit = function (form) {
            $scope.processing = true;
            applicationService.rateApplication($routeParams.id, $scope.rate, function () {
                $scope.processing = false;
                $scope.done = true;
            });
        };

        (function () {
            document.title = '评价';
            $scope.processing = false;
            $scope.done = false;
            $scope.rate = {
                regularApplication: true,
                regularBookingTime: true,
                regularRoute: true
            };
            applicationService.getApplicationRate($routeParams.id, function (rate) {
                if (!rate.regular) {
                    $scope.rate.regularApplication = rate.irregularReason.indexOf("申请单") < 0;
                    $scope.rate.regularBookingTime = rate.irregularReason.indexOf("时间") < 0;
                    $scope.rate.regularRoute = rate.irregularReason.indexOf("路线") < 0;
                    $scope.finished = true;
                }
            });
        })();
    }]);