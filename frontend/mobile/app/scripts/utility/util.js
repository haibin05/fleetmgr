'use strict';

angular.module('mobileApp').service('utilService', ['$timeout', 'constants', function (timeout, constants) {

    this.showLoader = function () {
        timeout(function () {
            // sample task to be done after 2000 milli sec
        }, 2000);
    };

    this.dismissProgress = function () {
        timeout(function () {
            // sample task to be done after 2000 milli sec
        }, 2000);
    };

    this.formatTime = function (date) {
        var day = new Date(Date.parse(date));
        var h = day.getHours();
        var m = day.getMinutes();
        return (h < 10 ? ('0' + h) : h) + ':' + (m < 10 ? ('0' + m) : m);
    };

    this.carStatus = function () {
        return constants.CAR_STATUS.concat();
    };

    this.driverStatus = function () {
        return constants.DRIVER_STATUS.concat();
    };

    this.carStatusMap = function () {
        var m = {};
        for (var i = 0; i < constants.CAR_STATUS.length; ++i) {
            m[constants.CAR_STATUS[i].value] = constants.CAR_STATUS[i];
        }
        return m;
    };

    //get the workday {{plus}} days after {{date}}
    this.getNextWorkday = function (date, plus) {
        date = new Date(date);
        var millisceonds = date.getTime();
        millisceonds += (plus || 1) * 24 * 60 * 60 * 1000;
        date.setTime(millisceonds);
        while (1) {
            if (date.getDay() != 0 && date.getDay() != 6) break;
            millisceonds += 24 * 60 * 60 * 1000;
            date.setTime(millisceonds);
        }
        return date;
    };
    //get the workday {{plus}} days after {{date}}
    this.getNextDate = function (date, plus) {
        date = new Date(date);
        var millisceonds = date.getTime();
        millisceonds += (plus || 1) * 24 * 60 * 60 * 1000;
        date.setTime(millisceonds);
        return date;
    };

}]);