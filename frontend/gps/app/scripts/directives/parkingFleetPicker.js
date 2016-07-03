angular.module('gpsApp').directive('parkingFleetPicker', ['$compile',
    function ($compile) {
    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/parkingFleetPicker.html',
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
                    scope.toggleGpsInstalled = function(v, k){
                        scope.gpsInstalled = {};
                        scope.gpsInstalled[k] = v;
                        scope.value.gpsInstalled = v;
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
