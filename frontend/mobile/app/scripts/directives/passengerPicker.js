angular.module('mobileApp').directive('passengerPicker', ['$compile', 'passengerService',
    function ($compile, passengerService) {

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
        templateUrl:'partials/directive/passenger-picker.html',
        scope: {
            value: '=ngModel',
            ngChange: '&'
        },
        compile: function compile(tElement, tAttrs, transclude) {
            tElement.addClass('passenger-picker');
            return {
                pre: function(scope, element, iAttrs) {
                    scope.params  = {
                        $limit: 20,
                        $offset: 0
                    };
                    scope.passengerGroups = _createGroups();
                    scope.loadMore = function(){
                        scope.loading = true;
                        passengerService.query(scope.params, function(passengers){
                            angular.forEach(passengers, function(o){
                                if(!o.namePinyin[0]) {
                                    o.namePinyin[0] = ' ';
                                }
                                scope.passengerGroups[o.namePinyin[0].slice(0, 1).toLowerCase()].push(o);
                            });
                            scope.loading = false;
                        });
                        scope.params.$offset += scope.params.$limit;
                    };
                    scope.doSearch = function(){
                        scope.passengerGroups = _createGroups();
                        scope.params.$offset = 0;
                        scope.loadMore();
                    };

                    scope.active = false;
                    scope.togglePicker = function(){
                        scope.active = !scope.active;
                    };
                    scope.selectItem = function(p){
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