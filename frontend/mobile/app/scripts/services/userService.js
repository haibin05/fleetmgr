/*
 * Services
 */
'use strict';

angular.module('mobileApp')
  .factory('User',  ['Restangular', function(Restangular) {
        return {
            get: function () {
                return Restangular.one('users','me').get();
            }
        }
    }]);
