angular.module('mobileApp').directive('confirm', ['confirmService', function (confirmService) {

    return {
        restrict: 'A',
        replace: false,

        link: function (scope, element, attrs) { 
            element.bind('click', function (e) {
                confirmService.show({
                    msg: attrs.confirm,
                    buttons: [{
                        text: '确认',
                        handler: function () {
                            scope.$apply(attrs.confirmAction);
                        }
                    }, {
                        text: '取消',
                        handler: function () {}
                    }]
                });
            });
        }
    };
}]);