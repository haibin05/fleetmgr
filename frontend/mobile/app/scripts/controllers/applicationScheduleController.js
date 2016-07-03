'use strict';

angular.module('mobileApp').controller('ApplicationScheduleCtrl', ['$routeParams', '$location', '$scope', 'applicationService', 'User', 'scheduleService', 'alertService', 'h5ContainerService',
    function ($routeParams, $location, $scope, applicationService, User, scheduleService, alertService, h5ContainerService) {

        $scope.processing = false;
        $scope.update = false;

        $scope.close = function () {
            h5ContainerService.closeH5Container()
        };

        $scope.addSchedule = function () {
            var applications = [], scheduleCars = [];
            angular.forEach($scope.applyList, function (o) {
                applications.push({id: o.id});
            });
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.car && o.car.id && o.driver && o.driver.id) {
                    scheduleCars.push({
                        car: {id: o.car.id},
                        driver: {id: o.driver.id}
                    });
                }
            });
            var schedule = {
                applications: applications,
                scheduleCars: scheduleCars
            }
            if (schedule.applications.length < 1 || schedule.scheduleCars.length < 1) {
                return false;
            }

            //check capacity
            if ($scope.isOutOfCapacity()) {
                alertService.show({msg: '乘客人数多于车辆载客数，请确认！'});
                return false;
            }


            $scope.processing = true;
            scheduleService.addSchedule(schedule, function () {
                $scope.processing = false;
                $scope.done = true;
                document.title = '派单成功';
            });
        };

        $scope.updateSchedule = function () {
            var applications = [], scheduleCars = [], capacity = 0, passengers = 0;
            angular.forEach($scope.applyList, function (o) {
                applications.push({id: o.id});
                passengers += o.passengers;
            });
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.car && o.car.id && o.driver && o.driver.id) {
                    scheduleCars.push({
                        id: o.id,
                        car: {id: o.car.id},
                        driver: {id: o.driver.id}
                    });
                    capacity += o.car.capacity; // 核定载客5人，允许申请单中人数不多于5人
                }
            });
            var schedule = {
                applications: applications,
                scheduleCars: scheduleCars
            }
            if (schedule.applications.length < 1 || schedule.scheduleCars.length < 1) {
                return false;
            }
            //check capacity
            if (capacity < passengers) {
                alertService.show({msg: '乘车人数超出车辆限载人数，请重新安排车辆！'});
                return false;
            }
            $scope.processing = true;
            scheduleService.updateSchedule($routeParams.scheduleId, schedule, function () {
                $scope.processing = false;
                $scope.done = true;
                document.title = '修改派单成功';
            });
        };

        $scope.removeApplication = function (applyId) {
            for (var i = 0; i < $scope.applyList.length; ++i) {
                if ($scope.applyList[i].id == applyId) {
                    $scope.applyList.splice(i, 1);
                    break;
                }
            }
        };

        $scope.addScheduleCar = function () {
            $scope.scheduleCars.push({});
        };

        $scope.removeScheduleCar = function (i) {
            $scope.scheduleCars.splice(i, 1);
        };

        $scope.rejectApplication = function () {    // 拒绝派单
            applicationService.rejectApplication($scope.application, function () {
                $scope.close();
            });
        };

        $scope.setDefaultDriver = function (car, i) {
            var scheduleCar = $scope.scheduleCars[i];
            if (!scheduleCar.driver && car.driver) {
                scheduleCar.driver = car.driver;
            }
        };

        $scope.isOutOfCapacity = function () {
            var capacity = 0, passengers = 0;
            angular.forEach($scope.applyList, function (o) {
                passengers += o.passengers;
            });
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.car && o.car.id && o.driver && o.driver.id) {
                    capacity += o.car.capacity; // 核定载客5人，允许申请单中人数不多于5人
                }
            });
            return capacity < passengers;
        };

        $scope.checkCar = function (car) {
            var boo = true;
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.car && o.car.id == car.id) {
                    boo = false;
                }
            });
            return boo;
        };

        $scope.checkDriver = function (driver) {
            var boo = true;
            angular.forEach($scope.scheduleCars, function (o) {
                if (o.driver && o.driver.id == driver.id) {
                    boo = false;
                }
            });
            return boo;
        };


        (function () {
            document.title = '派单';
            $scope.applyList = [];
            $scope.scheduleCars = [{}];
            $scope.application = {id: $routeParams.id};
            if (!!$routeParams.scheduleId && $routeParams.scheduleId != 0) {
                scheduleService.getScheduleById($routeParams.scheduleId, function (schedule) {
                    $scope.schedule = schedule;
                    $scope.scheduleCars = schedule.scheduleCars;
                    $scope.applyList = schedule.applications;
                    $scope.application = schedule.applications[0];
                    $scope.update = true;
                });
            } else {
                applicationService.getById($routeParams.id, function (application) {
                    $scope.application = application;
                    $scope.applyList.push(application);
                });
            }
        })();

    }]);