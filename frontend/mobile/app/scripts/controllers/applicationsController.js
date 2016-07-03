'use strict';

angular.module('mobileApp').controller('ApplicationsCtrl', ['$location', '$scope', 'applicationService', 'h5ContainerService',
    function ($location, $scope, applicationService, h5ContainerService) {

        $scope.close = function (){h5ContainerService.closeH5Container()};

        $scope.toggleFilter = function (filter) {
            $scope.applys = [];
            $scope.filter = filter;
            $scope.params = {
                $limit: 20,
                $offset: 0,
                status: $scope.filter,
                $orderby: 'start desc'
            };
            $scope.loadMore();
        };

        $scope.loadMore = function () {
            $scope.loading = true;
            if ($scope.params.$offset == 0 || $scope.params.$offset % $scope.params.$limit == 0) {
                applicationService.query($scope.params, function (applys) {
                    if (applys.length < $scope.params.$limit) {
                        $scope.params.$offset += applys.length;
                    } else {
                        $scope.params.$offset += $scope.params.$limit;
                    }
                    angular.forEach(applys, function (v) {
                        $scope.applys.push(v);
                    });
                    $scope.loading = false;
                });
            } else {
                $scope.loading = false;
            }
        };

        $scope.toggleFilter(2);
    }]);