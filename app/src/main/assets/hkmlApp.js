$j(document).ready(function() {
    // test ¤¤¤å
    
    var $ = $j;
    
    var userAgent = window.navigator.userAgent;
    var usrname = $('body > center > div.menu > .maintable > .bold > a').text();
    var appvl_flag = (!usrname || usrname == 'apptest');
    
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
        $('.t_row > tbody > tr > td:nth-child(2) img:not([smilieid]):not([src^="images/d-xite"]):not([src^="images/common"]):not([src^="http://www.hkml.net/Discuz/images/common"]):not([src^="http://hkml.net/Discuz/images/common"]):not([src^="images/attachicons"]):not([src^="http://wpa.qq.com/pa?p="]):not([src^="http://web.icq.com/whitepages/online?icq="]):not([src^="http://edit.yahoo.com/config/send_webmesg?.target="]):not([src^="http://blog.roodo.com/onion_club/"]):not([src^="http://amos1.taobao.com/"])').css({'width': '100%', 'height':'auto'});
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
        
        if (appvl_flag && userAgent.match(/iPhone/i)) {
            $('body > center > div.menu').css('display', 'none');
        }
        
        var hashSeg = location.href.split(/\#/)[0];
        var urlParts = hashSeg.split(/\//);
        var lastLocSeg = urlParts[urlParts.length-1].split(/\?/)[0];
        var lastLocParams = urlParts[urlParts.length-1].split(/\?/)[1];
        var mainTable_q = $('body > center > .maintable');
        
        /* check if index page */
        if (mainTable_q.length && /^index\.php$/.test(lastLocSeg)) {
            var mainTable = mainTable_q[0];
            
            // new message boxes
            var sph_q = $('> .spaceborder', mainTable);
            $(sph_q[0]).css('display', 'none');

            var sph = sph_q[1];
            $('<div id="hkmlApp-forumBox"></div>').insertBefore(sph);
            $('> table:first-child table', sph).each(function(i, n){
                $(n).css('width', '100%');
                $('#hkmlApp-forumBox').append(n);
            })

            // billboard
            var billboard_div = sph_q[2];
            $('<div id="hkmlApp-billboard"></div>').insertBefore(billboard_div);
            $('<div id="hkmlApp-themepark"></div>').insertAfter('#hkmlApp-billboard');
            $('> table > tbody > tr > td > table', billboard_div).each(function(i, n){
                if (i==0) {
                    var btd = $('> tbody > tr:nth-child(2) > td', n);
                    $('<div id="billboard-inner"></div>').insertAfter($('> table', btd[0]));
                    $('> table > tbody > tr > td', btd[0]).each(function(j, n2){
                        $('#billboard-inner').append('<div style="width:50%; text-align: center;float:left;">'+n2.innerHTML+'</div>');
                        $(n2).html('');
                    })
                    $('#hkmlApp-billboard').append(n);
                    $('> table', btd[0]).remove();
                } else {
                    $('#hkmlApp-themepark').append(n);
                }
            })
            
            // other boxes below
            for(var x=3; x < Math.min(sph_q.length, 9); x++) {
                if (x!=6) {
                    $('> table > tbody:nth-child(2) > tr > td:not(:nth-child(1)):not(:nth-child(2))', sph_q[x]).css('display', 'none');
                } else {
                    // merchant's boxes
                    var td = $('<td />');
                    $('> table > tbody:nth-child(2) > tr > td', sph_q[x]).each(function(i, n){
                        $('> *', n).appendTo(td).css('width', '50%').attr('align', 'left');
                        $(n).remove();
                    });
                    $('> table > tbody:nth-child(2) > tr', sph_q[x]).remove();
                    $('> table > tbody:nth-child(2)', sph_q[x]).append('<tr />');
                    $('> table > tbody:nth-child(2) > tr', sph_q[x]).append(td);
                }
            }
            for(var x=9; x < sph_q.length; x++) {
                $(sph_q[x]).css('display', 'none');
            }
        }

        if (/^forumdisplay\.php$/.test(lastLocSeg)) { 
            alterReplyBox();
            
            $('.tableborder[style^="width: 400px;"]').css('width', '80%');
            $('form[action="forumdisplay.php"] .subtable').css('height', 'auto');
        }
        
        if (/^logging\.php$/.test(lastLocSeg)) { 
            $('form[name="login"] > div.spaceborder > table > tbody > tr:not(:first-child) > td:first-child').css('display', 'none');
            $('form[name="login"] input[name="username"]').attr('placeholder', 'login name');
            $('form[name="login"] input[name="password"]').attr('placeholder', 'password');
            $('form[name="login"] input[name="answer"]').css('width', '100%').attr('placeholder', 'answer');
        }
        
        if (/^misc\.php$/.test(lastLocSeg)) { 
            $('form center').css('display', '');
        }
        
        if (/^my\.php$/.test(lastLocSeg)) { 
            $('td.subject a[target="_blank"]').attr('target', '_self');
            $('td a[href^="forumdisplay"][target="_blank"]').attr('target', '_self');
            $('> td:nth-child(3)', $('td.subject').parent('tr')).css('display', 'none');
            $('> td:nth-child(3)', $('tr.category')).css('display', 'none');
            $('> table > tbody > tr > td:first-child', $('.maintable')[2]).css('display', 'none');
            $('input[type="text"]').css('width', '100%');
        }
        
        if (/^pm\.php$/.test(lastLocSeg)) { 
            var mt = $('.maintable')[2];
            
            $('> table > tbody > tr > td:first-child', mt).each(function(i, n){
                var toggleDiv = $('<img id="foruminfo_img" src="images/d-xite_blue/collapsed_no.gif" style="position:absolute; border: none;">');
                $(toggleDiv).on('click', function(){
                    $(toggleDiv).attr('src', $('> div.spaceborder', n).css('display') == 'none' ? 'images/d-xite_blue/collapsed_no.gif' : 'images/d-xite_blue/collapsed_yes.gif');
                    $('> div.spaceborder', n).toggle();
                });
                
                $(n).css('width', 'auto').prepend(toggleDiv)
            });
        }

        if (/^post\.php$/.test(lastLocSeg) || (/^pm\.php$/.test(lastLocSeg) && lastLocParams == 'action=send')) {
            $('#postform > .spaceborder > table > tbody > tr:not(tr:first-child) > td:first-child').css('display', 'none');
    
            var formTables = $('#postform > .spaceborder > table > tbody > tr.bottom table');
            $('#postform input[name="subject"]').attr('placeholder', 'Title');
            $('#postform #posteditor_textarea').attr('placeholder', 'Content');
            
            $('#postform input[name="msgto"]').attr('placeholder', 'To:');
            $('#postform textarea[name="message"]').attr('placeholder', 'Message');
            
            // file upload set
            var eb = $(formTables).filter(function(){
                return $(this).hasClass('editor_button');
            })[0];
            var editor_table = $('> tbody > tr:nth-child(2) > td > table', eb)[0];
            $('> tbody > tr > td:first-child', editor_table).css('width', 'auto !important');
            $('> tbody > tr > td:nth-child(2)', editor_table).each(function(i, n){
                if (i!=0) {
                    $('input', n).attr('placeholder', 'desc.');
                    $('input', n).appendTo($('> td:first-child', $(n).parent()).append('<br/>'));
                }
                $(n).remove();
            });
            
            var ctrlTd = $('<td></td>');
            $('#postform #posteditor_controls > table').each(function(i, n){
                //var fc = $('> tbody > tr > td:first-child', n);
                $('> tbody > tr > td', n).each(function(j, n2){
                    if (! n2.id) {
                        $('> *', n2).each(function(k, n3){
                            if (n3.tagName != 'IMG' && -1 == $.inArray(n3.id, ['posteditor_cmd_createlink', 'posteditor_cmd_unlink', 'posteditor_cmd_email'])) {
                                $(n3).appendTo(ctrlTd);
                            }
                        });
                    }
                    $(n2).remove();
                });
                $('> tbody > tr', n).append(ctrlTd);
            });
            $('#postform')[0].onsubmit = function(){
                var msg = hkmlapp_replace_smilies($('#postform #posteditor_textarea').val());
                $('#postform #posteditor_textarea').val(msg);

                return validate(this);
            }

        }
        
        /* apply to content page only */
        if (/^redirect\.php$/.test(lastLocSeg) || /^viewthread\.php$/.test(lastLocSeg)) { 
            
            // show the JPEG file which the current forum version will not show directly
            $('.msgheader').each(function(k, mh){
                if (! /QUOTE/.test($(mh).html())) {
                    $(mh).css('display', 'none');
                    $('.msgborder', $(mh).parent()).css('border', 'none');
                    $('.msgborder .t_attachlist', $(mh).parent()).css('border', 'none');
                    $('.msgborder .t_attachlist', $(mh).parent()).each(function(i, n){
                        $('> *', n).each(function(j, m){
                            if (j<4) {
                                $(m).css('display', 'none');
                            }
                            if (j==2 && /\.jpeg$/.test($(m).html())) {
                                $(n).append('<img src="'+$(m).attr('href')+'" style="width: 100%; height: auto;"/>');
                            }
                        });
                    })
                }
            });
            
            try {
                var thumbnails = $('.t_row > tbody > tr > td:nth-child(2) img:not([smilieid]):not([src^="images/d-xite"]):not([src^="images/common"]):not([src^="http://www.hkml.net/Discuz/images/common"]):not([src^="http://hkml.net/Discuz/images/common"]):not([src^="images/attachicons"]):not([src^="http://wpa.qq.com/pa?p="]):not([src^="http://web.icq.com/whitepages/online?icq="]):not([src^="http://edit.yahoo.com/config/send_webmesg?.target="]):not([src^="http://blog.roodo.com/onion_club/"]):not([src^="http://amos1.taobao.com/"])');
                var urls = [];
                thumbnails.each(function(idx, n){
                    urls.push($(n).attr('src'));
                });
                
                // ios
                if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlAppThumbnail != "undefined") {
                    thumbnails.each(function(idx, n){
                        $(n).off('click').off('mouseover').off('mousewheel')
                                .removeAttr('onclick').removeAttr('onmouseover').removeAttr('onmousewheel')
                                .attr('onclick', 'window.webkit.messageHandlers.hkmlAppThumbnail.postMessage({idx: '+ idx + ',images: '+ JSON.stringify(urls) + '})');
                    });
                }
                
                // android
                if (typeof AndroidFunction != 'undefined') {
                    thumbnails.each(function(idx, n){
                        $(n).off('click').off('mouseover').off('mousewheel')
                                .removeAttr('onclick').removeAttr('onmouseover').removeAttr('onmousewheel')
                                .attr('onclick', 'AndroidFunction.onImageClick('+ idx + ', '+ JSON.stringify(urls) + ')');
                    });
                }
                
            } catch (e) {
                
            }
            
            $('a[href="###"][onclick="scroll(0,0)"]').on('click', function(){scrollTo(0,0);}).attr('href', 'javascript:void(0);')
            $('a[target="_blank"]').attr('target', '_self');
            
            if (mainTable_q.length > 2) { // only hide this box if this is not end of section posts message
                $(mainTable_q[0]).css('display', 'none');
            }
            if ($('#pmprompt.maintable').length) {
                $(mainTable_q[2]).css('display', 'none');
            } else {
                $(mainTable_q[1]).css('display', 'none');
            }
            var section_link;
            $('a', mainTable_q[0]).each(function(i, n){
                if (i==1) {
                    section_link = n;
                }
            })
            
            var usercnt = '';
            $('form[name="delpost"] > div.spaceborder > table > tbody > tr > td').each(function(i, n){
                if ((i+1) % 2==1) {
                    var img = $('.avatar > img', n).css({height: '20px', width: 'auto'});
                    $(n).prepend(img);
                    $('> div.smalltxt', n).remove();
                    usercnt = $(n).html();
                    $(n).remove();
                } else {
                    var node = $('<div>'+usercnt+'</div>');
                    $('table', node[0]).attr('align', 'left').attr('width', '').attr('valign', 'middle');
                    node.prependTo(n);
                }
            });
             
            $('a[href^="misc.php"]').parent('div').parent('td').parent('tr.header').css('background-image', 'none');
            $('a[href^="misc.php"]').parent('div').parent('td').parent('tr.header').css('background-color', '#eeeeee');
            var p = $('a[href^="misc.php"]').parent('div').parent('td');
            $(p[0]).prepend("<br/>");
            $(section_link).css('font-size', '12px').prependTo(p[0]);
            $('a[href^="misc.php"]').parent('div').removeClass('right').appendTo(p[0]);
            
            var utilLinkParent = $('a[href^="misc.php?action=emailfriend"]').parent();
            $('a[href^="misc.php?action=emailfriend"]').remove();
            $('a[href^="viewthread.php?action=printable"]').remove();
            var span = $('<span></span>')
            $('> *', utilLinkParent).each(function(i, n){
                if (i > 0) {
                    $(span).append(' | ');
                }
                $(n).appendTo(span);
            });
            $(utilLinkParent).empty().append(span);
            
            // like frame
            try {
                var fs0 = $('fieldset');
                $.each(fs0, function(idx, fs) {
                    var tb0 = $('table', fs);
                    
                    var nameList = tb0[0];
                    $('> tbody > tr > td:not(:first-child):not(:last-child)', nameList).css('display', 'none');
                    
                    var tb = tb0[1]
                    if (tb) {
                        var slt = $('<select id="likeActions" />');
                        var opt = $('<option value="">---</option>');
                        $(slt).append(opt);
                        $('td a', tb).each(function(i, n){
                            var label = $('font', n).text();
                            opt = $('<option value="'+$(n).attr('href')+'">'+label+'</option>');
                            $(slt).append(opt);
                        });
                        $(slt).on('change', function(){
                            if (this.value) {
                                location = this.value;
                            }
                        });
                        $(tb).remove();
                        $(fs).append('Like:');
                        $(fs).append(slt);
                    } 
                });
            } catch (e) {
                //alert(e.message);
            }
            
            // replace youtube link with in place youtube box
            var w = $(window).width();
            var vw = w * 0.9;
            var vh = vw * 315 / 560;
            var q = $('a[href*=".youtube.com"], a[href*="youtu.be"]');
            q.each(function(i, n){
                var tube = $(n).attr('href');
                var match = tube.match(/^http[s]{0,1}\:\/\/(?:[^\.]+\.)youtube\.com\/watch\?v\=([^\&]+)/);
                if (! match) {
                    match = tube.match(/^http[s]{0,1}\:\/\/(?:[^\.]+\.){0,1}youtu\.be\/([^\/]+)/);
                }
                if (match) {
                    $(n).replaceWith('<div style="text-align: center;"><iframe webkit-playsinline width="'+vw+'" height="'+vh+'" src="https://www.youtube.com/embed/'+match[1]+'?playsinline=1" frameborder="0" allowfullscreen></iframe></div>');
                }
            });
            
            // unicode smilies from system
            $('.t_msgfont').each(function(i, n){
                $(n).html($(n).html().replace(/\&amp\;(\#[0-9]{1,7}\;)/g, '<span style="font-size:24px;">&$1</span>'));
            });
            
            try {
                alterReplyBox();
            } catch (e) {}
            
            // the last visited select box at page end
            var mt = $('.maintable');
            $('select', mt[mt.length-3]).css({
                width: '80%',
                'margin-left': 'auto',
                'margin-right': 'auto',
                display: 'block'
            });
            
        }    
        
        if (/^search\.php$/.test(lastLocSeg)) { 
            if (lastLocParams) {
                $('td.subject a[target="_blank"]').attr('target', '_self');
                $('td a[href^="forumdisplay"][target="_blank"]').attr('target', '_self');
                $('td a[href^="space"][target="_blank"]').attr('target', '_self');
                $('> td:nth-child(4)', $('td.subject').parent('tr')).css('display', 'none');
                $('> td:nth-child(5)', $('td.subject').parent('tr')).css('display', 'none');
                $('> td:nth-child(4)', $('tr.category')).css('display', 'none');
                $('> td:nth-child(5)', $('tr.category')).css('display', 'none');
                $('> tr:first-child > td:nth-child(4)', $('td.subject').parent('tr').parent('tbody')).css('display', 'none');
                $('> tr:first-child > td:nth-child(5)', $('td.subject').parent('tr').parent('tbody')).css('display', 'none');
            } else {
                $('input[type="text"]').css('width', '100%');
                $('input[name="srchtxt"]').attr('placeholder', 'Keywords');
                $('input[name="srchuname"]').attr('placeholder', 'User name');
                $('form[action="search.php"] > div > table > tbody > tr > td:first-child').css('display', 'none');
                $('form[action="search.php"] > div > table > tbody > tr > td:nth-child(3)').css('display', 'none');
            }
        }
        
        if (/^toptendetails\.php$/.test(lastLocSeg)) {
            
            var tbTop = $('.tableborder');
            
            // search bar
            var nav = tbTop[0];
            if (nav) {
                var div = $('<div />');
                $('> tbody > tr > td', nav).each(function(i, n){
                    $.each(n.childNodes, function(){
                        $(div).append(n.childNodes[0]);
                    }); 
                    $(n).remove();
                });
                var td = $('<td />');
                $(td).append(div);
                $('> tbody > tr', nav).append(td);
            }
            
            
            // top ten table
            var tb0 = tbTop[1];
            var tb1 = $('> tbody > tr > td > table', tb0);
            if (tb0 && tb1[0]) {
                var tb = tb1[0];
                $('> tbody > tr > td', tb).each(function(i, td){
                    var div = $('<div class="toptenblock" />');
                    $('> *', td).each(function(j, n){
                        $(div).append(n);
                    });
                    $(tb0).append(div);
                });
                $(tb1).remove();
                $('.toptenblock:last-child')
                        .css({
                            display: 'block', 
                            width: '100%', 
                            border: 'none'
                        });
            }
            $('.toptenblock').css({display: 'inline-block', width: 'calc(50% - 15px)'});
            $('.toptenblock:last-child').css({width: '100%'});
            $('.toptenblock > div:nth-child(2)').css({height: '150px', 'overflow-y': 'hidden'});
            
        }
        
        try {
            $('a[href="javascript:void(0)"]').each(function(i, n){
                var out = n.outerHTML;
                var mth = out.match(/onclick\=\"window\.open\(\'([^\']+)\'/);

                if (mth && mth[1]) {
                    var fb_sharer = 'http://www.facebook.com/sharer/sharer.php?u=';
                    if (/^http[s]{0,1}\:\/\/www\.facebook\.com/.test(mth[1])) {
                        $(n).attr('href', 'facebookshare:' + mth[1].substring(fb_sharer.length).replace(/\&amp;/g, '&'));
                    } else {
                        $(n).attr('href', mth[1]);
                    }
                    $(n)[0].onclick = null;
                }
            });
            
            $("body").swipe( {
                //Generic swipe handler for all directions
                swipeLeft:function(event, direction, distance, duration, fingerCount, fingerData) {
                    var links = $('a[href^="redirect.php"][href$="&goto=nextnewset"]');
                    if (links.length) {
                        location = links.attr('href');
                    } else {
                        history.forward();
                    }
                },
                swipeRight:function(event, direction, distance, duration, fingerCount, fingerData) {
                    var links = $('a[href^="redirect.php"][href$="&goto=nextoldset"]');
                    if (links.length) {
                        location = links.attr('href');
                    } else {
                        history.back();
                    }
                },
                //Default is 75px, set to 0 for demo so any distance triggers swipe
                 threshold:120
            });
        } catch (e) {
            //alert(e.message);
        }
        
        try {
            var cks = document.cookie.split(/\; /);
            var aCk = [];
            cks.forEach(function(ck){
                var ppt = ck.split(/\=/);
                aCk.push({
                    name: ppt[0],
                    value: unescape(ppt[1])
                });
            });
            // for ios
            if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlAppCookie != "undefined") {
                window.webkit.messageHandlers.hkmlAppCookie.postMessage(aCk);
            }
        } catch (e) {
            
        }
        
        try {
            if (!appvl_flag && userAgent.match(/iPhone/i)) {
                $('<div style="height: 60px;"></div>').appendTo('body');
                var d = $('<div style="position: fixed; bottom:0; width: calc(100% - 40px); height: 60px; background-color: #dddddd; padding: 0 20px;"></div>')
                        .append('<a href="javascript:void(0);" onclick="window.history.back()" style="float: left; padding: 5px; font-size: 24px;">&#8617;</a>')
                        .append('<a href="javascript:void(0);" onclick="window.history.forward()" style="float: left; padding: 5px; font-size: 24px;">&#8618;</a>')
                        .append('<a href="javascript:void(0);" onclick="location=\'./index.php\';" style="float: right; padding: 5px; font-size: 24px;">&#127968;</a>')
                        .append('<a href="facebookshare:'+location.href+'" style="float: right; padding: 5px; font-size: 24px;">&#9734;</a>')
                        .append('<div style="clear: both;"></div>');
                d.appendTo('body');
            }
        } catch (e) {}

        function alterReplyBox() {
            // postform
            try {
                $('#postform > .spaceborder > table > tbody > tr:not(tr:first-child) > td:first-child, '+
                '#postform > .maintable > .spaceborder > table > tbody > tr:not(tr:first-child) > td:first-child, '+
                '#postform > .maintable > .spaceborder > table > tbody > tr:not(tr:first-child) > td:nth-child(2), '+
                '#postform > .maintable > .spaceborder > table > tbody > tr:not(tr:first-child) > td:last-child').css('display', 'none');
                $('#postform #attachbody tr.row2 > td, #postform #attachbody tr.row1 > td').css({
                    'border-bottom': '1px dotted #555555', 
                    'padding-bottom': '20px', 
                    'padding-top': '10px'
                });
                $('#postform #attachbody tr.row2:last-child, #postform #attachbody tr.row1:last-child > td').css('border-bottom', 'none');

                var formTables = $('#postform > .maintable > .spaceborder > table > tbody > tr.bottom table');
                var formToolTable = formTables[0];
                $(formToolTable).css('display', 'none');
                $('#postform input[name="subject"]').attr('placeholder', 'Title (optional)');
                $('#postform #message').attr('placeholder', 'Content');

                // file upload set
                $('> tbody > tr > td:first-child', formTables[2]).css('width', 'auto !important');
                $('> tbody > tr > td:nth-child(2)', formTables[2]).each(function(i, n){
                    if (i!=0) {
                        $('input', n).attr('placeholder', 'desc.');
                        $('input', n).appendTo($('> td:first-child', $(n).parent()).append('<br/>'));
                    }
                    $(n).remove();
                });
            
                $('#postform')[0].onsubmit = function(){
                    var msg = hkmlapp_replace_smilies($('#postform #message').val());
                    $('#postform #message').val(msg);

                    return validate(this);
                }
                
                $('#smiliestable').insertAfter($('#postform #message').parent());
                $('#smiliestable [id^="smilie_"]').removeAttr('onmouseover');
                $('#smiliestable [id^="smilie_"]').removeAttr('onclick').on('click', function(){
                    var s = $('#message').prop("selectionStart");
                    var v = $('#message').val();
                    var newVal = v.substring(0, s) + $(this).attr('alt') + ' ' + v.substring(s, v.length);
                    $('#message').val(newVal).prop("selectionStart", s + $(this).attr('alt').length+1);
                    $('#message').focus().prop("selectionEnd", $('#message').prop("selectionStart"));
                });
            } catch(e) {
                //
            }

        }
        
        function hkmlapp_replace_smilies(str) {
            /*
            var div = $('<div />');
            var unicode = "&#129324;/&#128079;/&#128541;/&#128549;/&#128123;/&#128565;/&#9996;/&#128526;/&#129300;/&#128513;/&#127908;/&#128515;/&#128545;/&#128560;/&#129318;/&#128517;/&#129326;/&#128550;/&#128536;/&#128161;/&#128169;/&#129321;/&#128548;/&#128127;/&#128076;/&#128552;/&#128128;/&#127867;/&#128077;/&#10084;/&#129312;/&#128580;/&#128555;/&#9786;/&#128516;/&#128540;/&#128542;/&#128566;/&#129320;/&#128553;/&#128525;/&#128563;/&#129303;/&#129314;/&#129299;/&#128564;/&#128557;/&#128544;";
            $(div).html(unicode);
            unicode = $(div).html();
            var hkml_code = ":em93:/:em95:/:em89:/:em88:/:em94:/:em87:/:em96:/:em31:/:em32:/:em34:/:em97:/:lol/:Q/:em41:/:em72:/:em73:/:em42:/:em40:/:em39:/:em36:/:em35:/:em37:/:em66:/:em69:/:em70:/:em71:/:em65:/:em64:/:em63:/:em46:/:em45:/:em44:/:em43:/:em47:/:em48:/:em49:/:em62:/:em61:/:em59:/:em58:/:em57:/:em56:/:em55:/:em54:/:em53:/:em52:/:em51:/:em50:";

            var arUnicode = unicode.split("/");
            var arHKMLCode = hkml_code.split("/");

            $.each(arUnicode, function(i, u) {
                var p = RegExp(u, 'g');
                str = str.replace(p, arHKMLCode[i]);
            });
            */
            return str;
        }            
    }
})

