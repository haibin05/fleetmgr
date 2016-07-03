/*
 * Services
 */
'use strict';

angular.module('mobileApp').factory('applicationService', ['Restangular',
    function(Restangular) {

    var _mockData = [];

    var _id = 0;

    return {
        getAll: function (cb) {
            Restangular.all('applications').getList().then(cb);
        },
        query: function (params, cb) {
            Restangular.all('applications', params).getList(params).then(cb);
        },
        getById: function (applyId, cb) {
            Restangular.one('applications', applyId).get().then(cb);
        },
        addApplication: function (application, cb) {
            Restangular.one("applications").post("", application).then(cb);
        },
        cancelApplication: function (application, cb) {     // 取消申请
            var status = {
                "status":"8"
            };
            Restangular.one('applications/' + application.id + '/status').customPUT(status).then(cb);
        },
        approveApplication: function (application, cb) {    // 审核通过
            var status = {
                "status":"3"
            };
            Restangular.one('applications/' + application.id + '/status').customPUT(status).then(cb);
        },
        refuseApplication: function (application, cb) {     // 审批退回
            var status = {
                "status":"5"
            };
            Restangular.one('applications/' + application.id + '/status').customPUT(status).then(cb);
        },
        rejectApplication: function (application, cb) {     // 调度退回
            var status = {
                "status":"6"
            };
            Restangular.one('applications/' + application.id + '/status').customPUT(status).then(cb);
        },
        deleteApplication: function (application, cb) {     // 申请调度作废
            var status = {
                "status":"7"
            };
            Restangular.one('applications/' + application.id + '/status').customPUT(status).then(cb);
        },
        updateApplication: function (application, cb) {
            Restangular.one('applications/' + application.id).customPUT(application,'', params).then(cb);
        },
        rateApplication: function(applyId, rate, cb){
            Restangular.one('applications', applyId).one('rateOfApplication').customPUT(rate,'', '').then(cb);
        },
        rateDriver: function(applyId, carId, rate, cb){
            Restangular.one('applications', applyId).one('cars', carId).one('rateOfDriver').customPUT(rate,'', '').then(cb);
        },
        getApplicationRate: function(applyId, cb){
            Restangular.one('applications', applyId).one('rateOfApplication').get().then(cb);
        },
        getDriverRate: function(applyId, carId, cb){
            Restangular.one('applications', applyId).one('cars', carId).one('rateOfDriver').get().then(cb);
        },
        candidateCars: function(applyId, params, cb){
            Restangular.one('applications/' + applyId).all('candidateCars').getList(params).then(cb);
        },
        candidateDrivers: function(applyId, carId, params, cb){
            if(!!carId) {
                Restangular.one('applications', applyId).one('cars', carId).all('candidateDrivers').getList(params).then(cb);
            }
        }
    }
}]);
