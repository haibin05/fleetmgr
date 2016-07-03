angular.module('gpsApp').directive('carStatusPicker', ['$compile', 'utilService',
    function ($compile, utilService) {
    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/carStatusPicker.html',
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
                    scope.gpsInstalled = {'众业GPS车辆':true};
                    scope.state = {};
                    scope.moving = {};
                    scope.fleet = {};
                    scope.carStatusOpts = utilService.carStatus();
                    scope.toggleGpsInstalled = function(v, k){
                        scope.gpsInstalled = {};
                        scope.gpsInstalled[k] = v;
                        scope.value.gpsInstalled = v;
                    };
                    scope.toggleState = function(v, k){
                        scope.state = {};
                        scope.state[k] = v;
                        scope.value.state = v;
                    };
                    scope.toggleMoving = function(v, k){
                        scope.moving = {};
                        scope.moving[k] = v;
                        scope.value.moving = v;
                    };
                    scope.toggleFleet = function(v, k){
                        scope.fleet = {};
                        scope.fleet[k] = v;
                        scope.value.fleetId = v;
                    };
                    scope.clearGpsInstalled = function(){
                        scope.gpsInstalled = {};
                        delete scope.value.gpsInstalled;
                    };
                    scope.clearState = function(){
                        scope.state = {};
                        delete scope.value.state;
                    };
                    scope.clearMoving = function(){
                        scope.moving = {};
                        delete scope.value.moving;
                    };
                    scope.clearFleet = function(){
                        scope.fleet = {};
                        delete scope.value.fleetId;
                    };
                    scope.clear = function(){
                        scope.clearGpsInstalled();
                        scope.clearState();
                        scope.clearMoving();
                        scope.clearFleet();
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
