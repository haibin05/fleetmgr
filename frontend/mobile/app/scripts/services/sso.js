'use strict';

angular.module('mobileApp')
    .factory('sso', ['Auth', '$q', '$cookies', '$timeout', '$interval', function (Auth, $q, $cookies, $timeout, $interval) {
        var appName = "feetMgrApp";


        var browserType = {
            versions: (function () {
                var u = navigator.userAgent;
                return {
                    trident: u.indexOf('Trident') > -1,                         //IE Core
                    presto: u.indexOf('Presto') > -1,                           //opera Core
                    webKit: u.indexOf('AppleWebKit') > -1,                      //Apple Google Core
                    gecko: u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //FireFox Core
                    mobile: !!u.match(/AppleWebKit.*Mobile.*/),                 //is Mobile
                    ios: !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),            //is IOS
                    android: u.indexOf('Android') > -1, //android and uc browser
                    iPhone: u.indexOf('iPhone') > -1,                           //is iPhone or QQHD browser
                    iPad: u.indexOf('iPad') > -1,                               //is iPad
                    webApp: u.indexOf('Safari') == -1,                          //is web app,no header and footer
                    weixin: u.indexOf('MicroMessenger') > -1,                   //is wechat
                    qq: u.match(/\sQQ/i) == " qq",                              //is QQ
                    ie: (u.toLowerCase().match(/msie ([\d.]+)/) || [])[1]
                };
            })(),
            language: (navigator.browserLanguage || navigator.language).toLowerCase()    //get language
        };

        function connectWebViewJavascriptBridge(callback) {
            if (window.WebViewJavascriptBridge) {
                callback(WebViewJavascriptBridge);
            } else {
                document.addEventListener('WebViewJavascriptBridgeReady', function () {
                    callback(WebViewJavascriptBridge);
                }, false);
            }
        }


        return {

            /**
             * sso 登录。因为要在几乎所有页面显示前完成sso，所以在支持sso的环境下返回promise
             * 作为$routeProvider的resolve参数
             * @returns {*}
             */
            login: function () {

                var defer = $q.defer();

                if (browserType.versions.mobile && browserType.versions.iPhone) {
                    if (!!$cookies.get('token')){
                        return;
                    }

                    connectWebViewJavascriptBridge(function (bridge) {
                        bridge.init(function (message, responseCallback) {
                        });

                        bridge.registerHandler('testJavascriptHandler', function (data, responseCallback) {
                        });
                        //2.以上均为定义好的方法名称，不需要修改。下面为配置的重要部分
                        //startLogon为iOS中接收到H5传递参数的方法名，需要两边一致。为了方便，和安卓定义同样的名称。如果修改，需要通知我一并进行修改

                        bridge.callHandler('startLogon',
                            appName,
                            function (appToken) {
                                if (!!appToken) {
                                    return Auth.shareTomeTokenLogin(appToken, function (err) {
                                        defer.resolve();
                                    })
                                } else {
                                    defer.resolve();
                                }
                            });
                    });
                    return defer.promise;
                }

                if (browserType.versions.android) {
                    // false:第一次登录；true:不是第一次登录
                    if(!!window.XTAccountService.getHasLogon() && !!$cookies.get('token')){
                        return;
                    }
                    var count = 3;
                    $timeout(function () {
                        function androidSSO() {
                            window.XTAccountService.startLogon(appName, function (appToken) {
                                if (!!appToken) {
                                    return Auth.shareTomeTokenLogin(appToken, function (err) {
                                        defer.resolve();
                                    })
                                } else {
                                    defer.resolve();
                                }
                            });
                        };
                        if (!!window.XTAccountService) {
                            androidSSO();
                        } else {
                            var stop = $interval(function () {
                                if (!!window.XTAccountService) {
                                    androidSSO();
                                    $interval.cancel(stop);
                                }
                                count--;
                                if (count === 0) {
                                    $interval.cancel(stop);
                                    defer.resolve();
                                }
                            }, 500)
                        }
                    })
                    return defer.promise;
                }
            }
        };
    }]);
