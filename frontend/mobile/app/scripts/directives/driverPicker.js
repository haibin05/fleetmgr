angular.module('mobileApp').directive('driverPicker', ['$compile', 'applicationService',
    function ($compile, applicationService) {

    function _createGroups(){
        var keys = ' ,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z'.split(',');
        var groups = {};
        angular.forEach(keys, function(key){
            groups[key] = [];
        });
        return groups;
    }

    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/driver-picker.html',
        scope: {
            value: '=ngModel',
            applyId: '=applyId',
            carId: '=carId',
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
                    scope.driverGroups = _createGroups();
                    scope.loadMore = function(){
                        scope.loading = true;
                        applicationService.candidateDrivers(scope.applyId, scope.carId, scope.params, function(drivers){
                            angular.forEach(drivers, function(o){
                                if(!!!o.namePinyin[0]) {
                                    o.namePinyin[0] = ' ';
                                }
                                scope.driverGroups[o.namePinyin[0].slice(0,1).toLowerCase()].push(o);
                            });
                            scope.loading = false;
                        });
                        scope.params.$offset += scope.params.$limit;
                    };
                    scope.doSearch = function(){
                        scope.driverGroups = _createGroups();
                        scope.params.$offset = 0;
                        scope.loadMore();
                    };

                    scope.active = false;
                    scope.togglePicker = function(){
                        if(!scope.carId)return;

                        if(!scope.readonlyFlg) {
                            scope.active = !scope.active;
                            if(scope.active) {
                                scope.doSearch();
                            }
                        }
                    };
                    scope.selectItem = function(p){
                        if(scope.beforeChange && !scope.beforeChange({driver:p})){
                            scope.active = false;
                            return false;
                        }
                        scope.value = p;
                        scope.active = false;
                    };

                    scope.$watch('params.keyword', function (val) {
                        scope.doSearch();
                    });

                },

                post: function(scope, element, iAttrs, controller) {

                }
            }
        }
    };
}]);