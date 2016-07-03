/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('scheduleCarService', ['Restangular',
    function(Restangular) {

    return {
        getById: function (scheduleCarId, cb) {
            Restangular.one('scheduleCars', scheduleCarId).get().then(cb);
        },
        getRecord: function(scheduleCarId, cb){
            Restangular.one("scheduleCars/" + scheduleCarId).one("record").get().then(cb);
        },
        updateRecord: function(scheduleCarId, record, cb){
            Restangular.one("scheduleCars/" + scheduleCarId).one("record").customPUT(record).then(cb);
        }
    }
}]);
