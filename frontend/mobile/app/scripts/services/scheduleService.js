/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('scheduleService', ['Restangular',
    function(Restangular) {

    return {
        query: function (params, cb) {
            Restangular.all('schedules', params).getList(params).then(cb);
        },
        addSchedule: function (schedule, cb) {
            Restangular.one("schedules").post("", schedule).then(cb);
        },
        updateSchedule: function (scheduleId, schedule, cb) {
            Restangular.one("schedules", scheduleId).post("", schedule).then(cb);
        },
        deleteSchedule: function (scheduleId, cb) {
            Restangular.one("schedules").customDELETE(scheduleId).then(cb);
        },
        getScheduleById: function (scheduleId, cb) {
            Restangular.one("schedules", scheduleId).get().then(cb);
        }
    }
}]);
