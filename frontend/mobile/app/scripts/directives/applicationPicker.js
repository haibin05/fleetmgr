angular.module('mobileApp').directive('applicationPicker', ['$compile', 'applicationService',
    function ($compile, applicationService) {

    return  {
        restrict: 'EA',
        replace: false,
        require: 'ngModel',
        templateUrl:'partials/directive/application-picker.html',
        scope: {
            value: '=ngModel',
            status: '=status',
            ngChange: '&'
        },
        compile: function compile(tElement, tAttrs, transclude) {
            tElement.addClass('passenger-picker');
            return {
                pre: function(scope, element, iAttrs) {
                    scope.params  = {
                        $limit: 20,
                        $offset: 0,
                        status: scope.status? scope.status: '2:3',
                        $orderby: 'start'
                    };
                    scope.applys = [];
                    scope.loadMore = function(){
                        scope.loading = true;
                        applicationService.query(scope.params, function(applys){
                            for(var i = 0; i < applys.length; ++i){
                                for(var j = 0; j < scope.value.length; ++j){
                                    if(applys[i].id == scope.value[j].id){
                                        applys[i].selected = true;
                                        break;
                                    }
                                }
                                scope.applys.push(applys[i]);
                            }
                            scope.loading = false;
                        });
                        scope.params.$offset += scope.params.$limit;
                    };
                    scope.doSearch = function(){
                        scope.applys = [];
                        scope.params.$offset = 0;
                        scope.loadMore();
                    };
                    scope.doSearch();

                    scope.active = false;
                    scope.togglePicker = function(){
                        scope.active = !scope.active;
                        if(scope.active){
                            for(var i = 0; i < scope.applys.length; ++i){
                                scope.applys[i].selected = false;
                                for(var j = 0; j < scope.value.length; ++j){
                                    if(scope.applys[i].id == scope.value[j].id){
                                        scope.applys[i].selected = true;
                                        break;
                                    }
                                }
                            }
                        }
                    };
                    scope.selectItem = function(p){

                        if(!p.selected) {
                            p.selected = true;
                            scope.value.push(p);
                        } else {
                            p.selected = false;
                            for(var i = 0; i < scope.value.length; ++i){
                                if(scope.value[i].id == p.id){
                                    scope.value.splice(i, 1);
                                    break;
                                }
                            }
                        }
                    };
                },

                post: function(scope, element, iAttrs, controller) {

                }
            }
        }
    };
}]);