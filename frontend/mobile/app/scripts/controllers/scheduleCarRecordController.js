'use strict';

angular.module('mobileApp').controller('ScheduleCarRecordCtrl', ['$routeParams', '$location', '$scope', '$timeout', 'scheduleCarService', 'enumService', 'User', 'alertService', 'h5ContainerService',
    function ($routeParams, $location, $scope, $timeout, scheduleCarService, enumService, User, alertService, h5ContainerService) {

        $scope.status = {
            processing: false,
            done: false
        };

        $scope.editMode = false;
        $scope.finished = false;
        $scope.canceled = false;

        $scope.close = function () {
            h5ContainerService.closeH5Container()
        };

        $scope.changeToEdit = function () {
            if (location.hash.indexOf('/record/end') > 1) {
                $scope.record.end = new Date();
            }
            $timeout(function () {
                $scope.editMode = true;
                $scope.canceled = false;
            }, 100);
        };
        $scope.changeFromEdit = function () {
            $timeout(function () {
                $scope.editMode = false;
                $scope.canceled = true;
            }, 100);
        };

        $scope.startRecord = function (form) {
            if (form.$valid) {
                $scope.status.processing = false;
                $scope.record.start = new Date();
                scheduleCarService.updateRecord($scope.scheduleCar.id, $scope.record, function () {
                    $scope.status.processing = false;
                    $scope.status.done = true;
                    document.title = '出厂成功';
                });
            }
        };

        $scope.endRecord = function (form) {
            if (form.$valid) {
                if ($scope.record.endMile < $scope.record.startMile) {
                    alertService.show({msg: '结束里程数不能小于开始里程数!'});
                    return;
                }
                $scope.status.processing = true;
                scheduleCarService.updateRecord($scope.scheduleCar.id, $scope.record, function () {
                    $scope.status.processing = false;
                    $scope.status.done = true;
                    document.title = '回厂成功';
                });
            }
        };

        (function () {
            $timeout(function(){
                scheduleCarService.getById($routeParams.id, function (scheduleCar) {
                    $scope.scheduleCar = scheduleCar;
                });
                scheduleCarService.getRecord($routeParams.id, function (record) {
                    if (!!record.end) {
                        $scope.finished = true;
                    }
                    $scope.record = record;
                });
            });
            User.get().then(function (user) {
                $scope.user = user;
            });
            if (location.hash.indexOf('/record/start') > -1) {
                document.title = '任务执行';
            } else {
                if ($scope.finished) {
                    document.title = '任务详情';
                } else {
                    document.title = '任务执行';
                }
            }

        })();

    }]);