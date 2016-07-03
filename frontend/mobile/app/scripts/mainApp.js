'use strict';

var mobileApp = angular.module('mobileApp', ['ngRoute','ngCookies','restangular', 'mobile-angular-ui', 'mobile-angular-ui.gestures', 'pickadate']);

mobileApp.run(["Auth","$transform","$rootScope",function(Auth,$transform,$rootScope) {
    window.$transform = $transform;






}]);

mobileApp.config(['$routeProvider', 'RestangularProvider', '$httpProvider', 'constants',
    function ($routeProvider, RestangularProvider, $httpProvider, constants) {
    RestangularProvider.setBaseUrl(constants.BASE_URL);
    RestangularProvider.setDefaultHttpFields({cache: false});

    $routeProvider.when('/application/form', {
        templateUrl: 'partials/application-form.html',
        controller: 'ApplicationFormCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }],
            pageType: function () {
                return "form";
            }
        }
    }).when('/applications/:id/cars/:carId/rate', {
        templateUrl: 'partials/driver-rate.html',
        controller: 'DriverRateCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/applications/:id/rate', {
        templateUrl: 'partials/application-rate.html',
        controller: 'ApplicationRateCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/applications/:id/schedule/step0', {
        templateUrl: 'partials/application-schedule-step0.html',
        controller: 'ApplicationFormCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }],
            pageType: function () {
                return "step0";
            }
        }
    }).when('/applications/:id/schedule/step1', {
        templateUrl: 'partials/application-schedule-step1.html',
        controller: 'ApplicationScheduleCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/applications/:id/schedule/step2/:scheduleId', {
        templateUrl: 'partials/application-schedule-step2.html',
        controller: 'ApplicationScheduleCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/applications/:id/schedule/step3', {
        templateUrl: 'partials/application-schedule-step3.html',
        controller: 'ApplicationScheduleCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/applications/:id', {
        templateUrl: 'partials/application-detail.html',
        controller: 'ApplicationFormCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }],
            pageType: function () {
                return "detail";
            }
        }
    }).when('/applications', {
        templateUrl: 'partials/applications.html',
        controller: 'ApplicationsCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/schedules', {
        templateUrl: 'partials/schedules.html',
        controller: 'SchedulesCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/schedules/:id', {   // 修改调度（申请作废，重新派单）
        templateUrl: 'partials/schedules-detail.html',
        controller: 'SchedulesCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/scheduleCars/:id/record', {
        templateUrl: 'partials/record-detail.html',
        controller: 'ScheduleCarRecordCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/scheduleCars/:id/record/start', { // 出厂
        //http://www.jxclgps.com/mobile/#/scheduleCars/6ae7f588d25045399f4dc17644e39d4f/record/start
        templateUrl: 'partials/record-start.html',
        controller: 'ScheduleCarRecordCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/scheduleCars/:id/record/end', {   // 回厂
        //http://www.jxclgps.com/mobile/#/scheduleCars/6ae7f588d25045399f4dc17644e39d4f/record/end
        templateUrl: 'partials/record-end.html',
        controller: 'ScheduleCarRecordCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/cars', {
        templateUrl: 'partials/cars.html',
        controller: 'CarsCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/drivers', {
        templateUrl: 'partials/drivers.html',
        controller: 'DriversCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/start', {
        templateUrl: 'partials/start.html',
        controller: 'LoginCtrl',
        resolve: {
            sso: ['sso', function(sso) {
                return sso.login();
            }]
        }
    }).when('/login', {
        templateUrl: 'partials/login.html',
        controller: 'LoginCtrl'
    }).otherwise({
        redirectTo: '/start'
    });
    $httpProvider.interceptors.push('authInterceptor');


}]).factory('authInterceptor', ['$rootScope', '$q', '$cookies', '$location',function ($rootScope, $q, $cookies, $location) {
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
}]);


jQuery.extend( jQuery.fn.pickadate.defaults, {
    monthsFull: [ '一月', '二月', '三月', '四月', '五月', '六月', '七月', '八月', '九月', '十月', '十一月', '十二月' ],
    monthsShort: [ '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '十一', '十二' ],
    weekdaysFull: [ '星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六' ],
    weekdaysShort: ['日', '一', '二', '三', '四', '五', '六'],
    today: '今日',
    clear: '清除',
    close: '关闭',
    firstDay: 1,
    format: 'mm月dd日',
    formatSubmit: 'yyyy/mm/dd'
});

jQuery.extend( jQuery.fn.pickatime.defaults, {
    format: 'HH:i',
    interval: 15,
    clear: '清除'
});
