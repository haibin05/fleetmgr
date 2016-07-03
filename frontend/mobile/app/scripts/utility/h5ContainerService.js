'use strict';

angular.module('mobileApp').service('h5ContainerService', ['$location', '$timeout', function ($location, $timeout) {

    function connectWebViewJavascriptBridge(callback) {
        if (window.WebViewJavascriptBridge) {
            callback(WebViewJavascriptBridge);
        } else {
            document.addEventListener('WebViewJavascriptBridgeReady', function () {
                callback(WebViewJavascriptBridge);
            }, false);
        }
    };

    $timeout(function() {
        connectWebViewJavascriptBridge(function (bridge) {
            bridge.init(function (message) {
                console.log("init h5 container iphone");
            });
            bridge.registerHandler('testJavascriptHandler', function (data) {
                console.log("registerHandler h5 container iphone");
            });
        });
    }, 50);

    return {
        closeH5Container: function () {
            console.log("start close h5 container");
            if (!!window.H5Container) {
                window.H5Container.closeH5Container();
            } else if (!!window.WebViewJavascriptBridge) {
                console.log("start close h5 container iphone");

                connectWebViewJavascriptBridge(function (bridge) {
                    bridge.callHandler('close', "close", function (response) {
                        console.log("callback close h5 container iphone");
                    });
                });

            } else {
                $location.path("/#start");
            }
        }
    }
}]);