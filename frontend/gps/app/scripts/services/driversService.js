/*
 * Services
 */
'use strict';

angular.module('gpsApp').factory('driversService', ['Restangular', function(Restangular) {
    return {
        getAll: function(params,cb) {
            Restangular.all('drivers').getList(params).then(cb);
        },
        getById: function(id, cb) {
            Restangular.one('drivers', id).get().then(cb);
        },
        getCurrentState: function(id, cb) {
            Restangular.one('drivers', id).one('state').get().then(cb);
        },
        getDriverSchedules: function(params, cb) {
            //Restangular.one('drivers', id).one('schedules').get().then(cb);
            Restangular.one('drivers', params.id).one('schedules').get({
                start: params.start,
                end: params.end
            }).then(cb);
        },
        getDriversPaths: function (params, cb) {
            Restangular.one('drivers', params.id).one('paths').get({
                start: params.start,
                end: params.end
            }).then(cb);
        }

    };
}]);
