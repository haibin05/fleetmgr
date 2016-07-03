/*
 * Services
 */
'use strict';

angular.module('gpsApp').factory('alarmsService', ['Restangular', 'constants','$cookies', function(Restangular, constants,$cookies) {
    return {
        getAlarms: function (params, cb) {
            Restangular.one('alarms').get(params).then(cb);
        },
        exportToExcel: function(params){
            var url = constants.BASE_URL + '/alarms/export.xlsx?';
            var exportParams={
                start:params.start,
                end: params.end,
                access_token:$cookies.get('token')
            }
            if(params.alarm){
                exportParams.alarm=params.alarm;Fa
            }
            return url+$.param(exportParams);
        }
    }
}]);
