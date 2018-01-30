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
        if (mainTable_q.length && /^index\.php$/.test(lastLocSeg)) {
            var mainTable = mainTable_q[0];
            var sph_q = $('> .spaceborder', mainTable);
            
            // billboard
            
            var bbJsonData = [];
            
            var billboard_div = sph_q[2];
            //$('<div id="hkmlApp-billboard"></div>').insertBefore(billboard_div);
            //$('<div id="hkmlApp-themepark"></div>').insertAfter('#hkmlApp-billboard');
            $('> table > tbody > tr > td > table', billboard_div).each(function(i, n){
                if (i==0) {
                    var btd = $('> tbody > tr:nth-child(2) > td', n);
                    $('> table > tbody > tr > td', btd[0]).each(function(j, n2){
                        var obj = {}
                        $('> div', n2).each(function(k, n3){
                            if (k==0) {
                                var tmp = $('> b > span > a, > span > a', n3);
                                obj.author_href = tmp.attr('href');
                                obj.author = tmp.html();
                            }
                            if (k==1) {
                                obj.href = $('> a', n3).attr('href');
                                obj.img = $('> a > img', n3).attr('src');
                                obj.title = $('> a', n3).attr('alt');
                            }
                        });
                        bbJsonData.push(obj);
                    });
                }
            });
            
            jsonData.billboard = bbJsonData;
            // for ios
            if (window.webkit && window.webkit.messageHandlers && typeof window.webkit.messageHandlers.hkmlApp != "undefined") {
                window.webkit.messageHandlers.hkmlApp.postMessage(jsonData);
            }
        }
        
        if (/^toptendetails\.php$/.test(lastLocSeg)) {
            
            var bbJsonData = [];
            
            var tbTop = $('.tableborder');
            
            var tb0 = tbTop[1];
            var tb1 = $('> tbody > tr > td > table', tb0);
            if (tb0 && tb1[0]) {
                var tb = tb1[0];
                
                // only one page for the current search criteria
                
                $('> tbody > tr > td', tb).each(function(i, td){
                    var obj = {}
                    $('> div', td).each(function(j, div){
                        if (j==0) {
                            obj.href = $('> a', div).attr('href');
                            obj.img = $('> a > img', div).attr('src');
                        } else {
                            obj.author_href = $('> a', div).attr('href');
                            obj.author = $('> a', div).html();
                            var spans = $('> span', div);
                            var hgm = $(spans[0]).html().match(/ ([0-9]+)$/)
                            obj.highest = hgm[1];
                            obj.title = $('> a', spans[1]).html();
                            obj.section_href = $('> div > a', div).attr('href');
                            obj.section_title = $('> div > a', div).html();
                            obj.rank_date = $('> div > span', div).html();
                        }
                    });
                    bbJsonData.push(obj);
                });
            }
            
            jsonData.billboard = bbJsonData;
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