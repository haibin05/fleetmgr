angular.module('gpsApp').directive('driverStatusPicker', ['$compile', 'utilService',
    function ($compile, utilService) {
    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/driverStatusPicker.html',
        scope: {
            value: '=ngModel',
            fleets: '=fleets',
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
                    scope.state = {};
                    scope.moving = {};
                    scope.fleet = {};
                    scope.driverStatusOpts = utilService.driverStatus();
                    scope.toggleState = function(v, k){
                        scope.state = {};
                        scope.state[k] = v;
                        scope.value.status = v;
                    };
                    scope.toggleFleet = function(v, k){
                        scope.fleet = {};
                        scope.fleet[k] = v;
                        scope.value.fleetId = v;
                    };
                    scope.clearState = function(){
                        scope.state = {};
                        delete scope.value.status;
                    };
                    scope.clearFleet = function(){
                        scope.fleet = {};
                        delete scope.value.fleetId;
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
