/*
 * Services
 */
'use strict';

angular.module('gpsApp').factory('carsService', ['Restangular', 'constants','$cookies',
    function(Restangular, constants, $cookies) {

    return {
        getAll: function (params, cb) {
            Restangular.all('cars').getList(params).then(cb);
        },
        getById: function (carId, cb) {
            Restangular.one('cars', carId).get().then(cb);
        },
        getCurrentStatus: function (carId, cb) {
            Restangular.one('cars', carId).one('state').get({now: 1}).then(cb);
        },
        getStatus: function (carId, datetime, cb) {
            Restangular.one('cars', carId).one('state').get({datetime: datetime}).then(cb);
        },
        getCarPaths: function (params, cb) {
            Restangular.one('cars', params.id).one('paths').get({
                start: params.start,
                end: params.end
            }).then(cb);
        },
        exportCarPathsToExcel: function(params){
            var exportParams={
                start:params.start,
                end:params.end,
                access_token:$cookies.get('token')
            }
            return constants.BASE_URL + '/cars/' + params.id + '/paths.xlsx?'+$.param(exportParams);
        },
        setDepotById:function (carId,depot,cb){
            Restangular.one('cars', carId).one('depot').customPUT(depot).then(cb);
        },
        getCarPathById: function (carId, pathId, cb) {
            Restangular.one('cars', carId).one('paths', pathId).get().then(cb);
        },
        getCarSchedules: function (params, cb) {
            Restangular.one('cars', params.id).one('schedules').get({
                start: params.start,
                end: params.end
            }).then(cb);
        },
        getCarWayPoints: function (carId, datetime, cb) {
            Restangular.one('cars', carId).one(datetime).one('waypoints').get().then(cb);
        },
        getCarSinglePaths: function (params, cb) {
            Restangular.one('cars', params.id).one('singlePaths').get({
                start: params.start,
                end: params.end
            }).then(cb);
            /*
             Restangular.one('cars', 'b0ff40e2416646e68143181dbcf71189').one('paths').get({
             start: '2015-09-11T03:00:00.000Z',
             end: '2015-09-11T23:55:00.000Z'
             }).then(cb);
             */

        },
        sendBroadcast: function(carId, msg, success, error){
            Restangular.one('cars', carId).one('voiceMessage').post("", msg).then(success, error);
        }
    }
}]);
