$j(document).ready(function() {
    
    var $ = $j;
    
    var appHead = $('html').attr('hkmlApp_head');
    
    if (! appHead) {
        showPopupText = function(){}
        setcopy = function(){}
        
        $('html').attr('hkmlApp_head', 'done'); // prevent double invoke
        
        var hashSeg = location.href.split(/\#/)[0];
        var urlParts = hashSeg.split(/\//);
        var lastLocSeg = urlParts[urlParts.length-1].split(/\?/)[0];
        var lastLocParams = urlParts[urlParts.length-1].split(/\?/)[1];
        var mainTable_q = $('body > center > .maintable');
    
        var jsonData = {}
    
        /* check if index page */
        if (/^viewthread\.php$/.test(lastLocSeg) || /^redirect\.php$/.test(lastLocSeg)) {
            
            var bbJsonData = [];
            
            $('.t_row > tbody > tr > td:nth-child(2) img:not([smilieid]):not([src^="images/d-xite"]):not([src^="images/common"]):not([src^="http://www.hkml.net/Discuz/images/common"]):not([src^="http://hkml.net/Discuz/images/common"]):not([src^="images/attachicons"]):not([src^="http://wpa.qq.com/pa?p="]):not([src^="http://web.icq.com/whitepages/online?icq="]):not([src^="http://edit.yahoo.com/config/send_webmesg?.target="]):not([src^="http://blog.roodo.com/onion_club/"])').each(function(i,n){
                bbJsonData.push({
                    img: $(n).attr('src')
                });
            });
            
            jsonData.photos = bbJsonData;
            
            var logoutLink = $('a[href^="logging.php?action=logout"]')
            jsonData.loggedIn = !!logoutLink.length;
            
            // for ios
            if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlApp != "undefined") {
                window.webkit.messageHandlers.hkmlApp.postMessage(jsonData);
            }
        }
        
        console.log(jsonData);
    
        // for ios
        /* if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlApp != "undefined") {
            window.webkit.messageHandlers.hkmlApp.postMessage(jsonData);
        } */
    }
});