/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('fleetsService', ['Restangular', 'constants',
    function(Restangular, constants) {

    return {
        getAll: function (params, cb) {
            Restangular.all('fleets').withHttpConfig({cache: params.cache !== false}).getList().then(cb);
        }
    }
}]);
