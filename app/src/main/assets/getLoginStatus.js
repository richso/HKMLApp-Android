$j(document).ready(function() {
    
    var $ = $j;
    
    var appHead = $('html').attr('hkmlApp_head');
    
    if (! appHead) {
        showPopupText = function(){}
        setcopy = function(){}
        
        $('html').attr('hkmlApp_head', 'done'); // prevent double invoke
        
        $('html > head').append('<meta name="viewport" content="width=device-width, initial-scale=1">')
            .append('<style type="text/css">[class*=myalbum-thumbs-], .myalbum-thumbs {overflow: hidden; margin: 0 auto !important;}\n'+
            '#divStayTopLeft {display: none;}\n' +
            '#postform input[type="file"], #postform input[type="text"] {width: 100% !important;}\n' +
            '#postform #posteditor_textarea {height: 90px !important;}\n' + 
            '.lightbutton {padding: 0 5px !important; color: #050505 !important; background-image: url(../../images/d-xite_blue/header_bg.gif); background-repeat: repeat-x; background-position: 0 50%; outline: 1px solid #4691C8; border: 1px solid #FFF !important; height: 19px !important; line-height: 17px !important;}\n' +
            '#posteditor_controls .editor_buttonnormal, #posteditor_controls .editor_buttonhover, #posteditor_controls .editor_buttonselected {float: left;}' +
            '.myalbum-thumbss {width: 100% !important; height: auto !important;}\n' +
            '.myalbum-thumbss > a > img {width: 100% !important; height: auto !important;}\n' +
            '.header {background: none; background-color: #eeeeee;}\n'+
            'body {background: none !important; background-color: white !important;}\n'+
            '.menu {background: none !important; background-color: #dddddd !important; margin-top: 0;}\n'+
            'table tbody tr td {word-wrap: break-word; word-break: break-all;}\n'+
            'td.f_author {min-width: 80px;}\n' +
            'td.f_folder, td.f_icon, td.f_views {display: none;}\n' +
            '.msgborder, .msgheader { margin: 0 !important;}</style>');
        $('*').css('font-size', '16px');
        $('.logo').css('display', 'none');
        $('.t_row > tbody > tr > td:nth-child(2) img:not([smilieid]):not([src^="images/d-xite"]):not([src^="images/common"]):not([src^="http://www.hkml.net/Discuz/images/common"]):not([src^="http://hkml.net/Discuz/images/common"]):not([src^="images/attachicons"]):not([src^="http://wpa.qq.com/pa?p="]):not([src^="http://web.icq.com/whitepages/online?icq="]):not([src^="http://edit.yahoo.com/config/send_webmesg?.target="]):not([src^="http://blog.roodo.com/onion_club/"])').css({'width': '100%', 'height':'auto'});
        $('.t_row > tbody > tr > td:nth-child(2) a[href][target]').attr('target', '_self').css('color', 'blue')

        $('input[type="text"], input[type="password"], input[type="file"]').css('height', 'auto');
        $('input[type="checkbox"]').removeClass('checkbox');
        $('input[type="radio"]').removeClass('radio');
                
        /* hide the board instruction panel */
        $('body center > div.tableborder').css('display', 'none');
        /* hide the model brands links panel */
        $('body center center').css('display', 'none');
       
        $('body > center > div.menu + div').css('display', 'none');
        
        // hide unuseful links
        $('a[href="medals.php"]').remove();
        $('#memcp').remove();
        $('a[href="faq.php"]').remove();
        var span = $('<span></span>');
        $('body > center > div.menu > div.maintable > *').each(function(i, n){
            if (i > 1) {
                $(span).append(' | ');
            }
            $(n).appendTo(span);
        });
        $('body > center > div.menu > div.maintable').empty().append(span);
        
        $('body > center > div.menu a[href="my.php"]').attr('href', 'javascript:void(0);');
        
        var hashSeg = location.href.split(/\#/)[0];
        var urlParts = hashSeg.split(/\//);
        var lastLocSeg = urlParts[urlParts.length-1].split(/\?/)[0];
        var lastLocParams = urlParts[urlParts.length-1].split(/\?/)[1];
        var mainTable_q = $('body > center > .maintable');

        /* check if login page */
        if (/^logging\.php$/.test(lastLocSeg)) {
            $('form[name="login"] > div.spaceborder > table > tbody > tr:not(:first-child) > td:first-child').css('display', 'none');
            $('form[name="login"] input[name="username"]').attr('placeholder', 'login name');
            $('form[name="login"] input[name="password"]').attr('placeholder', 'password');
            $('form[name="login"] input[name="answer"]').css('width', '100%').attr('placeholder', 'answer');
            
            var logoutLink = $('a[href^="logging.php?action=logout"]')

            // for ios
            if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlAppCookie != "undefined" && logoutLink.length) {
                var cks = document.cookie.split(/\; /);
                var aCk = [];
                cks.forEach(function(ck){
                    var ppt = ck.split(/\=/);
                    aCk.push({
                        name: ppt[0],
                        value: unescape(ppt[1])
                    });
                });
                
                setTimeout(function(){
                    window.webkit.messageHandlers.hkmlAppCookie.postMessage(aCk);
                }, 5000);
            }
            
        } else {
            $('<div style="display: block; position: fixed; z-index: 1000; top: 0; left: 0; height: 100%; width: 100%; background: rgba( 50, 50, 50, .8 ) 50% 50% no-repeat;"></div>').appendTo('body');
            
            // for ios
            var logoutLink = $('a[href^="logging.php?action=logout"]')

            if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlAppCookie != "undefined" && logoutLink.length) {
                var cks = document.cookie.split(/\; /);
                var aCk = [];
                cks.forEach(function(ck){
                    var ppt = ck.split(/\=/);
                    aCk.push({
                        name: ppt[0],
                        value: unescape(ppt[1])
                    });
                });

                setTimeout(function(){
                    window.webkit.messageHandlers.hkmlAppCookie.postMessage(aCk);
                }, 3000);
            }
        }
    }
});