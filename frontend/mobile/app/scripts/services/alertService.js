'use strict';

angular.module('mobileApp').factory('alertService', function($rootScope, $timeout) {

    var alertService = {};
    var tpl = [
        '<div class="msg-box">',
        '<table>',
        '<tr>',
        '<td align="center">',
        '<div class="msg-box-ct">',
        '<div class="msg-box-content"></div>',
        '<div class="msg-box-footer">',
        '</div>',
        '</div>',
        '</td>',
        ' </tr>',
        '</table>',
        '</div>'
    ].join('');

    alertService.show = function(cfg) {
        var el = $(tpl).appendTo(document.body);
        el.show().find('.msg-box-content').html(cfg.msg);

        var fbar = el.find('.msg-box-footer');
        $('<button class="btn btn-primary">确认</button>').appendTo(fbar)
            .click(function(e){
                el.remove();
            });
    };

    return alertService;
});

