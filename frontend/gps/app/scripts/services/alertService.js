'use strict';

angular.module('gpsApp').factory('alertService', function($rootScope, $timeout) {
    var alertService = {};
    var timer = null;
    var duration = 10000; //default display time

    $rootScope.alerts = [];

    alertService.add = function(obj) {
      var t = typeof(obj.duration) === 'number'? obj.duration: duration;

      $rootScope.alerts.unshift({
    	  'type': obj.type, 
    	  'msg': obj.msg,
    	  'close': function(){
    		  alertService.closeAlert(this);
    	  }
      });

      if(obj.type == 'alarm'){
        var sound = $('<embed id="" src="sound/alarm.wav" width="0" height="0" loop="true" autostart="true"></embed>').appendTo(document.body);
        $timeout(function(){sound.remove();}, 5000);
      }

      if(t !== 0) { //0 means not auto-closable
          timer = $timeout(function () {
              $rootScope.alerts.splice(0, 1);
          }, t);
      }
    };

    alertService.closeAlert = function(o) {
      $rootScope.alerts.splice($rootScope.alerts.indexOf(o), 1);
      if(timer){
    	  $timeout.cancel(timer);
      }
    };

    return alertService;
});

