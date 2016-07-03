angular.module('mobileApp').directive('carPicker', ['$compile', '$routeParams', 'applicationService',
    function ($compile, $routeParams, applicationService) {

    return  {
        restrict: 'EA',
        replace: false,
        require: ['ngModel'],
        templateUrl:'partials/directive/car-picker.html',
        scope: {
            value: '=ngModel',
            applyId: '=applyId',
            ngChange: '&',
            beforeChange: '&',
            readonlyFlg: '=readonlyFlg'
        },
        compile: function compile(tElement, tAttrs, transclude) {
            tElement.addClass('passenger-picker');

            return {
                pre: function(scope, element, iAttrs) {
                    scope.params  = {
                        $limit: 20,
                        $offset: 0
                    };
                    scope.loadMore = function () {
                        scope.loading = true;
                        applicationService.candidateCars(scope.applyId, scope.params, function(cars){
                            angular.forEach(cars, function(o){
                                scope.cars.push(o);
                            });
                            scope.loading = false;
                        });
                        scope.params.$offset += scope.params.$limit;
                    };
                    scope.doSearch = function(){
                        scope.cars = [];
                        scope.params.$offset = 0;
                        scope.loadMore();
                    };

                    if (!$routeParams.scheduleId || $routeParams.scheduleId == 0) {
                        scope.doSearch();
                    }

                    scope.active = false;
                    scope.togglePicker = function(){
                        if(!scope.readonlyFlg) {
                            scope.active = !scope.active;
                            if(scope.active){
                                scope.doSearch();
                            }
                        }
                    };
                    scope.selectItem = function(p){
                        if(scope.beforeChange && !scope.beforeChange({car:p})){
                            scope.active = false;
                            return false;
                        }
                        scope.value = p;
                        if(scope.ngChange) {
                            scope.ngChange({car:p});
                        }
                        scope.active = false;
                    };

                    scope.$watch('params.keyword', function (val) {
                        if(!!val || val == "") {
                            scope.doSearch();
                        }
                    });

                },

                post: function(scope, element, iAttrs, controller) {

                }
            }
        }
    };
}]);