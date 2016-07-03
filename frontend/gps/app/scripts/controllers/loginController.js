angular.module('gpsApp').controller('LoginCtrl', ['$scope', '$window', 'Auth', 'alertService',
    function ($scope, $window, Auth, alertService) {

    $scope.doLogin = function () {
        Auth.login({
            userName: $scope.userName,
            password: $scope.password
        }).then(function () {
                $window.location.href = '#/';
        }).catch(function (err) {
            console.log(err);
            alertService.add({
                msg: '登录失败, 工号或密码错误！',
                type: "danger"
            });
        })



    }
}]);