'use strict';

angular.module('mobileApp').controller('DriversCtrl', ['$location', '$scope', '$timeout', 'driversService', 'fleetsService', 'h5ContainerService',
    function ($location, $scope, $timeout, driversService, fleetsService, h5ContainerService) {

        function _createGroups() {
            var keys = 'a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z'.split(',');
            var groups = {};
            angular.forEach(keys, function (key) {
                groups[key] = [];
            });
            return groups;
        }

        $scope.params = {
            $limit: 20,
            $offset: 0
        };

        $scope.close = function (){h5ContainerService.closeH5Container()};

        $scope.driverGroups = _createGroups();
        $scope.loadMore = function () {
            $scope.loading = true;
            driversService.query($scope.params, function (drivers) {
                angular.forEach(drivers, function (o) {
                    $scope.driverGroups[o.namePinyin[0].slice(0, 1).toLowerCase()].push(o);
                });
                $scope.loading = false;
            });
            $scope.params.$offset += $scope.params.$limit;
        };
        $scope.doSearch = function () {
            $scope.driverGroups = _createGroups();
            $scope.params.$offset = 0;
            $scope.loadMore();
        };

        $scope.$watch('params.keyword', function (val) {
            $scope.doSearch();
        });

        (function () {
            document.title = '驾驶员查询';

            fleetsService.getAll($scope.params, function (fleets) {
                $scope.fleets = fleets;
            });
        })();
    }]);