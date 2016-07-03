angular.module('mobileApp').controller('LoginCtrl', ['$scope', '$routeParams', '$window', '$location', '$cacheFactory', '$cookies', '$timeout', 'Auth', 'h5ContainerService',
    function ($scope, $routeParams, $window, $location, $cacheFactory, $cookies, $timeout, Auth, h5ContainerService) {
        $scope.userName = '00180671';
        $scope.password = 'jx123456';
        $scope.authority = true;
        $scope.doLogin = function () {
            var $httpDefaultCache = $cacheFactory.get('$http');
            $httpDefaultCache.removeAll();
            $timeout(function (){
                Auth.login({
                    userName: $scope.userName,
                    password: $scope.password
                }).then(function () {
                    $window.location.href = '#/';
                }).catch(function (err) {
                    console.log(err)
                });
            });
        };
        var loginUser = Auth.getCurrentUser();
        if(!loginUser.roles){
            $scope.authority = false;
        } else {
            $scope.roles = loginUser.roles;
            if (loginUser.roles.indexOf('Coordinator') > -1) {
                $scope.coordinatorAuth = true;
            }
            if (loginUser.roles.indexOf('Approver') > -1) {
                $scope.auditorAuth = true;
            }
            if (loginUser.roles.indexOf('Supervisor') > -1) {
                $scope.dispatcherAuth = true;
            }
            if (loginUser.roles.indexOf('CentralDispatcher') > -1) {
                $scope.dispatcherAuth = true;
            }
            if (loginUser.roles.indexOf('Driver') > -1) {
                $scope.driverAuth = true;
            }
            if (loginUser.roles.indexOf('Manager') > -1) {
                $scope.managerAuth = true;
            }
            if (!$scope.coordinatorAuth && !$scope.dispatcherAuth && !$scope.managerAuth) {
                $scope.authority = false;
            }
            // TODO
            $scope.coordinatorAuth = true;
            $scope.auditorAuth = true;
            $scope.dispatcherAuth = true;
            $scope.driverAuth = true;
            $scope.authority = true;
        }
        $scope.goStartPage = function() {
            if($window.history.length > 1) {
                $window.history.back();
            } else {
                h5ContainerService.closeH5Container();
            }
        };
        $scope.gotoPage = function(url) {
            $location.path(url).replace();
        };
    }]);