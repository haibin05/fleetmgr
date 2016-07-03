'use strict';

angular.module('mobileApp').controller('ApplicationFormCtrl', ['$routeParams', '$location', '$timeout', '$scope', 'applicationService', 'enumService', 'Auth', 'utilService', 'alertService', 'h5ContainerService', 'passengerService', 'pageType',
    function ($routeParams, $location, $timeout, $scope, applicationService, enumService, Auth, utilService, alertService, h5ContainerService, passengerService, pageType) {

        $scope.processing = false;
        $scope.done = false;

        $scope.close = function () {
            h5ContainerService.closeH5Container()
        };

        $scope.submit = function (form) {
            if (form.$valid) {
                if ($scope.application.endDate.getTime() < $scope.application.startDate.getTime()) {
                    alertService.show({msg: '结束日期不能早于开始日期!'});
                    return;
                }

                $scope.processing = true;
                var params = {};
                angular.copy($scope.application, params);
                params.start = JSON.stringify(params.startDate).slice(1, -1);
                params.end = JSON.stringify(params.endDate).slice(1, -1);
                params.passenger = {userId: params.passenger.userId};
                params.coordinator = {userId: Auth.getCurrentUser().userId};
                params.reason = params.reasons;


                applicationService.addApplication(params, function () {
                    $scope.processing = false;
                    $scope.done = true;
                });
            }
        };

        $scope.cancelApplication = function () {    // 取消申请
            applicationService.cancelApplication($scope.application, function () {
                $scope.done = true;
            });
        };
        $scope.approveApplication = function () {   // 同意申请
            applicationService.approveApplication($scope.application, function () {
                $scope.done = true;
            });
        };
        $scope.refuseApplication = function () {    // 拒绝申请
            applicationService.refuseApplication($scope.application, function () {
                $scope.done = true;
            });
        };

        $scope.onStartDateChange = function (v) {
            if (!v.select)return;
            var start = new Date(v.select), endDate = $scope.application.endDate;

            // 变更开始时间和结束时间
            if ($scope.application.endDate && $scope.application.endDate.getDate() < start.getDate()) {
                start.setHours(endDate.getHours(), endDate.getMinutes(), 0, 0);
                $scope.application.endDate = start;//.setDate(start.getDate())
            }
            $scope.minEndDate = new Date(start);
            $scope.maxEndDate = new Date(start);
            $scope.maxEndDate.setDate(start.getDate() + 2);  // 最多用车时间天数-3天

            // 变更原因
            var today = new Date();
            var currentHour = today.getHours();
            var dateDiff = ($scope.application.startDate.getMonth() * 10 + $scope.application.startDate.getDate() - today.getMonth() * 10 - today.getDate());
            if ((dateDiff == 1 && currentHour >= 15) || (dateDiff == 0)) {
                $scope.reasonTypes = [{key: "03", value: "抢修"}];
                if (!$scope.application.id) {
                    $scope.application.reasons.key = "03";
                }
            } else {
                $scope.reasonTypes = $scope.fullReasonTypes;
                if (!$scope.application.id) {
                    $scope.application.reasons.key = "01";
                }
            }

            //if(moment(start).isSame(moment(), 'day')){//today         去掉限制
            //    $('#startTimePicker').pickatime('picker').set('min', [moment().hours(), moment().minutes()]);
            //} else {
            //    $('#startTimePicker').pickatime('picker').set('min', [0, 0]);
            //}
        };

        $scope.onStartTimeChange = function (v) {
            if (!v.select)return;
            var start = new Date(v.select), endDate = $scope.application.endDate;

            // 变更开始时间和结束时间
            if ($scope.application.endDate && $scope.application.endDate.getHours() < start.getHours()) {
                start.setHours(start.getHours() + 1, endDate.getMinutes(), 0, 0);
                $scope.application.endDate = start;//.setDate(start.getDate())
            }
            if ($scope.application.endDate && $scope.application.endDate.getMinutes() < start.getMinutes()) {
                start.setHours(endDate.getHours(), start.getMinutes(), 0, 0);
                $scope.application.endDate = start;//.setDate(start.getDate())
            }
        };

        (function () {
            $scope.minStartDate = new Date();
            $scope.currentTime = new Date();

            if ($routeParams.id) {
                if (pageType == "step0") {
                    document.title = '用车审核';
                } else {
                    document.title = '用车详情';
                }
                applicationService.getById($routeParams.id, function (application) {
                    $scope.application = application;
                });
            } else {
                document.title = '申请用车';

                //var start = utilService.getNextWorkday($scope.currentTime);
                var start = utilService.getNextDate($scope.currentTime);
                start.setHours(8, 0, 0, 0);

                var end = new Date(start);
                end.setHours(17, 30, 0, 0);

                $scope.application = {
                    startDate: start,
                    endDate: end,
                    reasons: {'key': '01'}
                };
                $scope.minEndDate = new Date(start);
                $scope.maxEndDate = new Date(start);
                $scope.maxEndDate.setDate(start.getDate() + 2);  // 最多用车时间天数-3天
            }
            enumService.getReasonTypes(function (reasonTypes) {
                if (!$scope.reasonTypes) {
                    $scope.reasonTypes = reasonTypes;
                }
                $scope.fullReasonTypes = reasonTypes;
            });
        })();

    }]);