angular.module('mobileApp').directive('carStatusPicker', ['$compile', 'utilService',
    function ($compile, utilService) {
    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/car-status-picker.html',
        scope: {
            value: '=ngModel',
            fleets: '=fleets',
            ngChange: '&'
        },
        compile: function compile(tElement, tAttrs, transclude) {

            $(tElement).addClass('status-picker');

            return {
                pre: function(scope, element, iAttrs) {
                    scope.state = {};
                    scope.moving = {};
                    scope.fleet = {};
                    scope.carStatusOpts = utilService.carStatus();
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
                        scope.clearState();
                        scope.clearMoving();
                        scope.clearFleet();
                    };
                    scope.ok = function(){
                        scope.ngChange.apply();
                        $(element).removeClass('open');
                        $('.scrollable-content').css('overflow', 'auto');//hack to fix poor scroll performance issue
                    };
                },

                post: function(scope, element, iAttrs, controller) {
                    $(element).find('.picker-toggle').click(function () {
                        $(element).find('.picker-menu').height($(document.body).height() - 90);
                        if($(element).hasClass('open')){
                            $(element).removeClass('open');
                            $('.scrollable-content').css('overflow', 'auto');//hack to fix poor scroll performance issue
                        } else {
                            $(element).addClass('open');
                            $('.scrollable-content').css('overflow', 'hidden');//hack to fix poor scroll performance issue
                        }
                    });
                }
            }
        }
    };
}]);
