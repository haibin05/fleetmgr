/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('passengerService',  ['Restangular', function(Restangular) {
    return {
        query: function (params, cb) {
            Restangular.all('passengers', params).withHttpConfig({cache: true}).getList(params).then(cb);
        }
    }
}]);
