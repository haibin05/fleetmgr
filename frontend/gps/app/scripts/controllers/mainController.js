'use strict';

angular.module('gpsApp').controller('MainCtrl', ['$rootScope', '$scope', '$location', '$window', '$cacheFactory', 'mapService', 'Auth', 'alertService', 'utilService', 'carsService', 'constants',
    function($rootScope, $scope, $location, $window, $cacheFactory, mapService, Auth, alertService, utilService, carsService, constants) {

    $scope.alarmTypes = utilService.alarmTypesMap();

    $scope.currentNav = function () {
        var path = $location.path().substring(1);
        var i = path.indexOf('/');
        if(path.indexOf('cars') > -1 && path.indexOf('/paths') > 0){
            return 'paths';
        } else {
            return i > -1? path.slice(0, i) : path;
        }
    };

    $scope.logout = function(){
        Auth.logout();
	    $cacheFactory.get('$http').removeAll();
        $window.location.href = '#/login';
    };

    $scope.alarmsOn = false;
    $scope.$on("fleet.events",function(evt,message){
        var badge = message.car && message.car.badge? message.car.badge : '';
        alertService.add({
            msg: '<a href="#/alarms">' + badge + $scope.alarmTypes[message.alarm] + '</a>',
            type: "alarm",
            duration: 0
        });
        $scope.alarmsOn = true;
    });

    $scope.$on('$routeChangeSuccess', function (event, current, previous) {
        if(previous && previous.loadedTemplateUrl){
            if(previous.loadedTemplateUrl.indexOf('partials/carPaths') > -1){
                mapService.hidePathSlider();
            } else if(previous.loadedTemplateUrl.indexOf('partials/carSchedules') > -1
                || previous.loadedTemplateUrl.indexOf('partials/drivers') > -1
                || previous.loadedTemplateUrl.indexOf('partials/driverSchedules') > -1){
                mapService.hideScheduleInfoWindow();
            }
        }
    });
    $scope.$on('$routeChangeStart', function (event, next, current) {
        if(next && next.originalPath && next.originalPath.indexOf('/depots') > -1){
            $('#wrapper').addClass('nomap');
        } else {
            $('#wrapper').removeClass('nomap');
        }
        mapService.resetAll();
    });

    //register keydown event to send broadcast

    $scope.sendBroadcast = function(carId, msg, success){
        carsService.sendBroadcast(carId, {message: msg}, success, function(){
            alertService.add({
                msg: '语音播报发送失败！',
                type: "danger"
            });
        });
    };

    $scope.initBroadcastInput = function(input){
        $(input).autocomplete({
            minLength: 0,
            autoFocus: true,
            source: constants.BROADCAST_OPTIONS
        });
    };
    /*
    $(document).keydown(function(e){
        alert(222)
        if(e.which == 13){
            var target = $(e.target);
            var targetCar = target.attr('broadcast-target-car');
            //if(targetCar && target.val()){
                carsService.sendBroadcast(111, 222, function(){});
            //}
        }
    });
    */

    mapService.init();

}]);
