'use strict';

angular.module('gpsApp').service('utilService', ['$timeout', 'constants',
    function(timeout, constants) {

    this.formatTime = function(date){
        var day = new Date(Date.parse(date));
        return moment(day).format('HH:mm');
    };

    this.formatDateTime = function(date){
        var day = new Date(Date.parse(date));
        return moment(day).format('YYYY-MM-DD HH:mm');
    };

    this.alarmTypes = function(){
        return constants.ALARMS_TYPE.concat();
    };

    this.alarmTypesMap = function(){
        var m = {};
        for(var i = 0; i < constants.ALARMS_TYPE.length; ++i){
            m[constants.ALARMS_TYPE[i].value] = constants.ALARMS_TYPE[i].name;
        }
        return m;
    };

    this.carStatus = function(){
        return constants.CAR_STATUS.concat();
    };

    this.carStatusMap = function(){
        var m = {};
        for(var i = 0; i < constants.CAR_STATUS.length; ++i){
            m[constants.CAR_STATUS[i].value] = constants.CAR_STATUS[i];
        }
        return m;
    };


    this.driverStatus = function(){
        return constants.DRIVER_STATUS.concat();
    };

    this.driverStatusMap = function(){
        var m = {};
        for(var i = 0; i < constants.DRIVER_STATUS.length; ++i){
            m[constants.DRIVER_STATUS[i].value] = constants.DRIVER_STATUS[i];
        }
        return m;
    };

}]);