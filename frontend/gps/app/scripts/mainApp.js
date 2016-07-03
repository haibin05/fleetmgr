'use strict';
var gpsApp = angular.module('gpsApp', ['ngRoute', 'ngCookies', 'restangular', 'ui.bootstrap', 'ngSanitize', 'angular-loading-bar','angularMoment']);

gpsApp.config(['$routeProvider', 'RestangularProvider', '$httpProvider', 'constants',
    function ($routeProvider, RestangularProvider, $httpProvider, constants) {
    //don't change the url below
    //use OS env FLEET_SERVER instead before gulp serve
    //for example: set FLEET_SERVER=http://localhost:8080 on windows
    //by default, the server use http://221.11.84.227:8080
    //for guy behind corp proxy
    //use OS env http_proxy
    //for example: set http_proxy=http://proxy.host.name:3128 on windows
    RestangularProvider.setBaseUrl(constants.BASE_URL);
    RestangularProvider.setDefaultHttpFields({cache: false});

    $routeProvider.when('/cars/:id/schedules', {
        templateUrl: 'partials/carSchedules.html',
        controller: 'CarSchedulesCtrl'
    }).when('/cars/:id/paths', {
        templateUrl: 'partials/carPaths.html',
        controller: 'CarPathsCtrl'
    }).when('/cars', {
        templateUrl: 'partials/cars.html',
        controller: 'CarsCtrl'
    }).when('/drivers/:id/schedules', {
        templateUrl: 'partials/driverSchedules.html',
        controller: 'DriverSchedulesCtrl'
    }).when('/drivers', {
        templateUrl: 'partials/drivers.html',
        controller: 'DriversCtrl'
    }).when('/paths', {
        templateUrl: 'partials/paths.html',
        controller: 'PathsCtrl'
    }).when('/alarms', {
        templateUrl: 'partials/alarms.html',
        controller: 'AlarmsCtrl'
    }).when('/depots', {
        templateUrl: 'partials/depots.html',
        controller: 'DepotsCtrl'
    }).when('/parking', {
        templateUrl: 'partials/parking.html',
        controller: 'ParkingCtrl'
    }).when('/mapdebug', {
        templateUrl: 'partials/mapDebug.html',
        controller: 'MapDebugCtrl'
    }).when('/cars/:id/mapdebug', {
        templateUrl: 'partials/carMapDebug.html',
        controller: 'CarMapDebugCtrl'
    }).when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginCtrl'
    })
        .otherwise({
            redirectTo: '/cars'
        })
    $httpProvider.interceptors.push('authInterceptor');


}])
    .factory('authInterceptor', ['$rootScope', '$q', '$cookies', '$location',function ($rootScope, $q, $cookies, $location) {
        return {
            // Add authorization token to headers
            request: function (config) {
                config.headers = config.headers || {};
                if ($cookies.get('token')) {
                    config.headers.Authorization = 'Bearer ' + $cookies.get('token');
                }
                return config;
            },

            // Intercept 401s and redirect you to login
            responseError: function (response) {
                if (response.status === 401) {
                    $location.path('/login');
                    // remove any stale tokens
                    $cookies.remove('token');
                    return $q.reject(response);
                }
                else {
                    return $q.reject(response);
                }
            }
        };
    }])
    .run(['constants', '$rootScope','alarmsService' ,'$interval','$cookies', function ( constants, $rootScope,alarmsService, $interval,$cookies) {


    var alarmInterval;
    $rootScope.$on("login.event", function (evt) {
        alarmsService.getAlarms(
            {
                $limit:1

            },function(resultData){
                var start = moment();
                if(resultData) {
                    var alarms = resultData.alarms;
                    if (alarms.length > 0) {
                        start = moment(alarms[0].start);
                    }
                }

                if (!!alarmInterval) {
                    $interval.cancel(alarmInterval);
                }
                alarmInterval=$interval(function(){
                    if ($cookies.get('token')){
                        var startclone = start.clone();
                        alarmsService.getAlarms(
                            {
                                start: startclone.add(1, 's').toISOString()
                            },
                            function(resultData){
                                if(resultData) {
                                    var alarms = resultData.alarms;
                                    if (alarms.length > 0) {
                                        start = moment(alarms[0].start);
                                        alarms = alarms.reverse();
                                        alarms.forEach(function (alarm) {
                                            $rootScope.$broadcast("fleet.events", alarm);
                                        })
                                    }
                                }
                            }
                        )
                    }else{
                        if (!!alarmInterval){
                            $interval.cancel(alarmInterval);
                            alarmInterval=null;
                        }
                    }

                }, 30000)
            }
        );



    });
    $rootScope.$on("logout.event", function (evt) {
        if (!!alarmInterval){
            $interval.cancel(alarmInterval);
            alarmInterval=null;
        }
    })
    if ($cookies.get('token')) {
        $rootScope.$broadcast("login.event");
    }

}]);