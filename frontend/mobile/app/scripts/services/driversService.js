/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('driversService',  ['Restangular', function(Restangular) {
    return {
        query: function (params, cb) {
            Restangular.all('drivers', params).withHttpConfig({cache: true}).getList(params).then(cb);
        }
    }
}]);
