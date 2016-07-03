/*
 * Services
 */
'use strict';

angular.module('gpsApp').factory('depotsService', ['Restangular', function(Restangular) {
    return {
        getCarsInDepot: function (params, cb) {
            Restangular.one('depots').one('carsInDepot').get(params).then(cb);
        },
        getAllDepot: function (cb) {
            Restangular.one('depots').get({
                timestamp: new Date().getTime()
            }).then(cb);
        },
        getCarsNotInDepot: function (params, cb) {
            Restangular.one('depots').one('carsNotInDepot').get(params).then(cb);
        }
    }
}]);
