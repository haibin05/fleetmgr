'use strict';

angular.module('mobileApp').controller('SchedulesCtrl', ['$location', '$scope', '$routeParams', 'applicationService', 'scheduleService', 'h5ContainerService',
    function ($location, $scope, $routeParams, applicationService, scheduleService, h5ContainerService) {

        $scope.schedules = [];
        $scope.schedule;
        $scope.applyList = [];
        $scope.scheduleCars = [];
        $scope.finished = false;
        $scope.editMode = true;

        $scope.params = {
            $limit: 20,
            $offset: 0,
            $orderby: 'start desc'
        };

        $scope.close = function () {
            h5ContainerService.closeH5Container()
        };

        $scope.deleteApplication = function (applyId) {    // 撤销申请派单
            $scope.finished = true;
            for (var i = 0; i < $scope.applyList.length; ++i) {
                if ($scope.applyList[i].id == applyId) {
                    $scope.applyList.splice(i, 1);
                    break;
                }
            }
        };

        $scope.deleteSchedule = function () {    // 撤销派单
            scheduleService.deleteSchedule($routeParams.id, function () {
                $scope.close();
            });
        };

        $scope.addScheduleCar = function () {
            $scope.scheduleCars.push({});
            $scope.finished = true;
        };
        $scope.setDefaultDriver = function (car, i) {
            var scheduleCar = $scope.scheduleCars[i];
            if (!scheduleCar.driver && car.driver) {
                scheduleCar.driver = car.driver;
            }
        };
        $scope.removeScheduleCar = function (i) {
            $scope.scheduleCars.splice(i, 1);
            $scope.finished = true;
        };
        $scope.updateSchedule = function () {
            $location.path("/applications/" + $scope.application.id + "/schedule/step2/" + $scope.schedule.id);
        };

        $scope.loadMore = function () {
            $scope.loading = true;
            if ($scope.params.$offset == 0 || $scope.params.$offset % $scope.params.$limit == 0) {
                scheduleService.query($scope.params, function (schedules) {
                    if (schedules.length < $scope.params.$limit) {
                        $scope.params.$offset += schedules.length;
                    } else {
                        $scope.params.$offset += $scope.params.$limit;
                    }
                    angular.forEach(schedules, function (v) {
                        $scope.schedules.push(v);
                    });
                    $scope.loading = false;
                });
            } else {
                $scope.loading = false;
            }
        };

        $scope.checkCar = function (car) {
            var boo = true
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.car && o.car.id == car.id) {
                    boo = false;
                }
            });
            return boo;
        };

        $scope.checkDriver = function (driver) {
            var boo = true
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.driver && o.driver.id == driver.id) {
                    boo = false;
                }
            });
            return boo;
        };
        (function () {
            if (!$routeParams.id) {
                document.title = '车辆调度';
                $scope.loadMore();
            } else {
                document.title = '派单详情';
                scheduleService.getScheduleById($routeParams.id, function (schedule) {
                    $scope.schedule = schedule;
                    $scope.scheduleCars = schedule.scheduleCars;
                    $scope.applyList = schedule.applications;
                    if(schedule.applications.length > 0) {
                        $scope.application = schedule.applications[0];
                    }
                    if (schedule.start <= new Date()) {
                        $scope.editMode = false;
                    }
                    if (!!$scope.editMode) {
                        angular.forEach(schedule.scheduleCars, function (car) {
                            if (car.status == 'FINISHED' || (!!car.startMiles && car.startMiles > 0)) {
                                $scope.editMode = false;
                                return;
                            }
                        });
                    }
                });
            }
        })();

    }]);