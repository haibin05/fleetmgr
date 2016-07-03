/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('carsService',  ['Restangular', function(Restangular) {
    return {
        query: function (params, cb) {
            Restangular.all('cars', params).withHttpConfig({cache: true}).getList(params).then(cb);
        }
    }
}]);
