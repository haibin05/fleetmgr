'use strict';

angular.module('mobileApp').factory('confirmService', function() {
    var confirmService = {};
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

    confirmService.show = function(cfg) {
        var el = $(tpl).appendTo(document.body);
        el.show().find('.msg-box-content').html(cfg.msg);

        var fbar = el.find('.msg-box-footer');
        for(var i = 0; i < cfg.buttons.length; ++i){
            var cls = i > 0? 'btn-default': 'btn-primary';
            var handler = cfg.buttons[i].handler;
            $('<button class="btn"></button>').addClass(cls).appendTo(fbar)
                .data('handler', handler)
                .html(cfg.buttons[i].text)
                .click(function(e){
                    $(this).data('handler')(e);
                    el.remove();
                });
        }
    };

    return confirmService;
});

