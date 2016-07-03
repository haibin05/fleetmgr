angular.module('gpsApp').directive('alarmTypePicker', ['$compile', 'utilService',
    function ($compile, utilService) {
    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/alarmTypePicker.html',
        scope: {
            value: '=ngModel',
            types: '=types',
            ngChange: '&'
        },
        compile: function compile(tElement, tAttrs, transclude) {

            $(tElement).addClass('status-picker');

            $(document).click(function(e){
                var tar = $(e.target);
                if(!tar.hasClass('picker-toggle') && tar.closest('.picker-menu', '.status-picker').length < 1){
                    $(tElement).removeClass('open');
                }
            });

            return {
                pre: function(scope, element, iAttrs) {
                    scope.alarmTypes = utilService.alarmTypes();
                    scope.toggleType = function(v, k){
                        scope.type = {};
                        scope.type[k] = v;
                        scope.value.alarm = v;
                        $(element).toggleClass('open');
                    };
                    scope.clearType = function(){
                        scope.type = {};
                        delete scope.value.alarm;
                    };
                },

                post: function(scope, element, iAttrs, controller) {
                    $(element).find('.picker-toggle').click(function () {
                        $(element).toggleClass('open');
                    });


                }
            }
        }
    };
}]);
