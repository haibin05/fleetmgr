'use strict';

angular.module('gpsApp')
  .factory('User',  ['Restangular', function(Restangular) {
        return {
            get: function () {
                return Restangular.one('users','me').get();
            }
        }
    }]);
