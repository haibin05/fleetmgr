/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('enumService', ['Restangular',
    function(Restangular) {

    return {
        getReasonTypes: function (cb) {
            Restangular.all("enums/ReasonType").withHttpConfig({cache: true}).getList().then(cb);
        }
    }
}]);
