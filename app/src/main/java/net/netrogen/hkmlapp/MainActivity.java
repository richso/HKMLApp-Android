package net.netrogen.hkmlapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
// import android.support.design.widget.BottomNavigationView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

//import com.facebook.drawee.backends.pipeline.Fresco;
//import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
//import com.facebook.imagepipeline.core.ImagePipelineConfig;
//import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
//import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.bumptech.glide.Glide;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
//import com.squareup.picasso.Picasso;
//import com.stfalcon.frescoimageviewer.ImageViewer;
import com.stfalcon.imageviewer.StfalconImageViewer;
import com.stfalcon.imageviewer.loader.ImageLoader;
import android.widget.ImageView;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    private ConstraintLayout container;
    final private String base_path = "http://www.hkml.net/Discuz/";
    final private String mainUrl = "http://www.hkml.net/Discuz/index.php";
    //final private String jqCDN_url = "http://code.jquery.com/jquery-1.12.4.min.js";
    //final private String touchSwipeUrl = "https://raw.githubusercontent.com/mattbryson/TouchSwipe-Jquery-Plugin/master/jquery.touchSwipe.min.js";

    final private String js_begin_url = "https://raw.githubusercontent.com/richso/hkmlApp/master/public_html/hkmlApp.js";

    final private String fb_hkml_group = "https://www.facebook.com/groups/86899893467/";
    final private String js_fb = "https://raw.githubusercontent.com/richso/hkmlApp/master/public_html/hkmlApp_fb.js";

    final private String fbsharekey = "facebookshare:";

    private String startUrl = mainUrl;

    private WebView webview;
    private ProgressBar progressBar;
    private WebView fbWebview;
    private ProgressBar fbProgressBar;

    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageArray;
    private ImagePicker imagePicker;

    private Bundle savedState;

    private TabHost tabhost;

    private boolean fbTabLoaded = false;
    private String fbStartUrl = fb_hkml_group;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == Picker.PICK_IMAGE_DEVICE) {
                if(imagePicker == null) {
                    imagePicker = new ImagePicker(MainActivity.this);
                    imagePicker.setImagePickerCallback(newImagePickerCallback());
                }
                imagePicker.submit(data);
            }
        }
    }

    private ImagePickerCallback newImagePickerCallback() {
        return new ImagePickerCallback(){
            @Override
            public void onImagesChosen(List<ChosenImage> images) {
                // Display images
                if (null == mUploadMessage && mUploadMessageArray == null) return;
                Uri result = Uri.parse(images.get(0).getQueryUri());
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                } else if (mUploadMessageArray != null) {
                    Uri[] uris = new Uri[1];
                    uris[0] = result;
                    mUploadMessageArray.onReceiveValue(uris);
                }
            }

            @Override
            public void onError(String message) {
                // Do error handling
                Log.v("@hkmlApp", message);
            }
        };
    }

    private void pickFile(String type) {
        imagePicker = new ImagePicker(MainActivity.this);
        imagePicker.setImagePickerCallback(newImagePickerCallback());
        // imagePicker.allowMultiple(); // Default is false
        // imagePicker.shouldGenerateMetadata(false); // Default is true
        // imagePicker.shouldGenerateThumbnails(false); // Default is true
        imagePicker.pickImage();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);

        savedState = savedInstanceState;

        java.net.CookieManager cookieManager = new java.net.CookieManager();
        CookieHandler.setDefault(cookieManager);

        setContentView(R.layout.activity_main);

        tabhost = (TabHost) findViewById(R.id.tabhost);
        tabhost.setup();

        TabHost.TabSpec websiteTab = tabhost.newTabSpec("forum");
        websiteTab.setIndicator(createNewTabText("論壇"));
        websiteTab.setContent(R.id.forum);
        tabhost.addTab(websiteTab);

        TabHost.TabSpec fbTab = tabhost.newTabSpec("facebook");
        fbTab.setIndicator(createNewTabText("facebook"));
        fbTab.setContent(R.id.facebook);
        tabhost.addTab(fbTab);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        final SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) this.findViewById(R.id.swipeContainer);
        mySwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    webview.reload();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
            }
        );

        requestExternalStoragePermission();

        /*
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(this, config); */

        // see if any deep link need to be processed
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        if (data != null) {
            Log.v("@intent", data.toString());
            startUrl = data.toString();
            Log.v("@startUrl", startUrl);
        }
        // -

        new WebTask().execute(this);

        fbWebview = (WebView) findViewById(R.id.fbWebview);
        fbProgressBar = (ProgressBar) findViewById(R.id.fbProgressBar);

        tabhost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            @Override
            public void onTabChanged(String tabId) {

                int i = tabhost.getCurrentTab();

                if (i == 1 && !fbTabLoaded) { // facebook tab
                    new FBWebTask().execute(MainActivity.this);
                }

            }
        });

    }

    private View createNewTabText(String txt) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.tabTextView);
        tv.setText(txt);
        return view;
    }

    private void syncCookies() {
        // sync cookies between webview and HttpURLConnection

        String cookiesStr = android.webkit.CookieManager.getInstance().getCookie(this.base_path);
        List<HttpCookie> cookies = parseCookies(cookiesStr);

        Log.v("@cookievalstr", cookiesStr);

        // Get cookie manager for HttpURLConnection
        java.net.CookieStore cookieStore = ((java.net.CookieManager)
                CookieHandler.getDefault()).getCookieStore();

        try {
            for (HttpCookie ck : cookies) {
                Log.v("@cookieval", ck.getName());
                cookieStore.add(new URI(this.base_path), ck);
            }
        } catch (Exception e) {
            Log.v("@cookie", e.getMessage());
        }
    }

    private ArrayList<HttpCookie> parseCookies(String str) {
        ArrayList<HttpCookie> cookies = new ArrayList<HttpCookie>();

        String[] strs = str.split(";");
        for(String ckstr: strs) {
            String[] ckNV = ckstr.split("=");
            try {
                HttpCookie ck = new HttpCookie(ckNV[0].trim(), java.net.URLDecoder.decode(ckNV[1], "UTF-8"));
                ck.setDomain("www.hkml.net");
                ck.setPath("/Discuz/");
                ck.setVersion(0);
                cookies.add(ck);
            } catch (Exception e) {
                Log.e("@cookie-encode", e.getMessage());
            }
        }

        return cookies;
    }

    private void requestExternalStoragePermission() {
        if ((ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)) {

            Log.v("@hkmlapp", "getting permissions");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST);
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState ) {
        webview = (WebView) findViewById(R.id.webview);
        super.onSaveInstanceState(outState);
        webview.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        webview = (WebView) findViewById(R.id.webview);
        super.onRestoreInstanceState(savedInstanceState);
        webview.restoreState(savedInstanceState);
    }

    private void dummy() throws Exception {
        String url = "123";

        url.substring(1);



    }
    private class WebTask extends AsyncTask<MainActivity, Integer, Boolean> {

        private String js_begin = "";
        private String jqCDN = "";
        private String jsTouchSwipeUrl = "";
        private MainActivity ma;

        protected Boolean doInBackground(MainActivity... mas) {
            Context context = MainActivity.this;
            AssetManager am = context.getAssets();

            ma = mas[0];

            js_begin = getFromHttp(MainActivity.this.js_begin_url);

            try {
                InputStream is = am.open("jquery-1.12.4.min.js");
                jqCDN = readStream(is);
            } catch (IOException e) {
                Log.e("@background", e.getMessage(), e);
            }

            try {
                InputStream is = am.open("jquery.touchSwipe.min.js");
                jsTouchSwipeUrl = readStream(is);
            } catch (IOException e) {
                Log.e("@background", e.getMessage(), e);
            }

            if (js_begin.equals("")) {
                try {
                    InputStream is = am.open("hkmlApp.js");
                    js_begin = readStream(is);
                } catch (IOException e) {
                    Log.e("@backgropund", e.getMessage(), e);
                }
            }

            return true;
        }

        protected void initWebview(String url) {

            webview = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = webview.getSettings();
            CustomJavascriptInterface myJavaScriptInterface = new CustomJavascriptInterface(MainActivity.this);
            webview.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
            webSettings.setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //syncCookies();

                    // note: prevent "back" load history page will not invoke title event
                    // js - added code to prevent double invoke of jQuery operations
                    injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
                    injectScript(view, jsTouchSwipeUrl);
                    injectScript(view, js_begin);
                    // --

                    // cater for if .goBack() is called and the setTitle event is not fired
                    ma.setTitle(view.getTitle());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith(fbsharekey)) {
                        // open fb share dialog

                        Log.v("@share", url);

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, url.substring(fbsharekey.length()));

                        startActivity(Intent.createChooser(share, "分享"));

                        return true;
                    } else if (! url.startsWith("http://www.hkml.net/")) {

                        if (url.startsWith("https://www.facebook.com/") ||
                                url.startsWith("https://m.facebook.com/") ||
                                url.startsWith("https://facebook.com/")) {

                            Log.v("@fb url", url);

                            if (fbTabLoaded) {
                                fbWebview.loadUrl(url);
                            } else {
                                fbStartUrl = url;
                            }
                            tabhost.setCurrentTab(1);
                        } else {
                            // open with OS default browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }

                        return true;
                    }

                    return super.shouldOverrideUrlLoading(view, url);
                }

            });

            webview.setWebChromeClient(new WebChromeClient() {

                FrameLayout videoFrame;

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    ma.setTitle(title);

                    injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
                    injectScript(view, jsTouchSwipeUrl);
                    injectScript(view, js_begin);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //Log.v("HKMLApp", "@progress: " + Integer.toString(newProgress) + "\n");
                    progressBar.setProgress(newProgress);

                    if (newProgress >= 50) {
                        // let the "back" pages change to App layout faster
                        injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
                        injectScript(view, jsTouchSwipeUrl);
                        injectScript(view, js_begin);
                    }
                }

                @SuppressWarnings("unused")
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                    mUploadMessage = uploadMsg;
                    pickFile(AcceptType);
                }

                @SuppressWarnings("unused")
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                    mUploadMessage = uploadMsg;
                    pickFile(AcceptType);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    mUploadMessage = uploadMsg;
                    pickFile("image/*");

                }

                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
                    mUploadMessageArray = uploadMsg;
                    pickFile("image/*");
                    return true;
                }

                @Override
                public void onShowCustomView(View view, CustomViewCallback callback) {
                    super.onShowCustomView(view, callback);
                    if (view instanceof FrameLayout){

                        FrameLayout frame = (FrameLayout) view;

                        videoFrame = frame;

                        View video = frame.getFocusedChild();
                        frame.removeView(video);
                        video.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
                        MainActivity.this.getSupportActionBar().hide();
                        tabhost.setVisibility(View.INVISIBLE);
                        View wvc = findViewById(R.id.webviewContainer);
                        wvc.setVisibility(View.INVISIBLE);
                        ConstraintLayout vdc = (ConstraintLayout) findViewById(R.id.videoContainer);
                        vdc.addView(video, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        vdc.setVisibility(View.VISIBLE);
                        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
                    }
                }
                public void onHideCustomView() {
                    MainActivity.this.getSupportActionBar().show();
                    View vdc = findViewById(R.id.videoContainer);
                    vdc.setVisibility(View.INVISIBLE);
                    View wvc = findViewById(R.id.webviewContainer);
                    wvc.setVisibility(View.VISIBLE);
                    tabhost.setVisibility(View.VISIBLE);
                    getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
                }
            });

            if (MainActivity.this.savedState == null) {
                webview.loadUrl(url);
            } else {
                webview.restoreState(MainActivity.this.savedState);
            }
        }

        protected void onPostExecute(Boolean result) {
            initWebview(startUrl);
        }

        private void injectScript(WebView view, String script) {
            //Log.v("HTMLApp", "@script: " + script);

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = "";
            try {
                encoded = Base64.encodeToString(script.getBytes("Big5"), Base64.NO_WRAP);
            } catch (java.io.UnsupportedEncodingException e) {
                // do nothing
                Log.v("HKMLApp","@script_error: " + e.getMessage());
            }
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");

        }

        private String getFromHttp(String urlstr) {
            String content = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlstr);

                //Log.v("HKMLApp", "@url: " + urlstr);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                int responseCode = urlConnection.getResponseCode();

                //Log.v("HKMLApp", "@responsecode: " + Integer.toString(responseCode));

                if(responseCode == HttpURLConnection.HTTP_OK){
                    content = readStream(urlConnection.getInputStream());
                    //Log.v("HKMLApp", "@content: " + content);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HKMLApp", "@error: " + e.getMessage() + "\n", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return content;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in, "big5"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

    }

    private class FBWebTask extends AsyncTask<MainActivity, Integer, Boolean> {

        private String js_begin = "";
        private String jqCDN = "";
        private String jsTouchSwipeUrl = "";
        private MainActivity ma;

        protected Boolean doInBackground(MainActivity... mas) {
            Context context = MainActivity.this;
            AssetManager am = context.getAssets();

            ma = mas[0];

            js_begin = getFromHttp(MainActivity.this.js_fb);

            try {
                InputStream is = am.open("jquery-1.12.4.min.js");
                jqCDN = readStream(is);
            } catch (IOException e) {
                Log.e("@background", e.getMessage(), e);
            }

            try {
                InputStream is = am.open("jquery.touchSwipe.min.js");
                jsTouchSwipeUrl = readStream(is);
            } catch (IOException e) {
                Log.e("@background", e.getMessage(), e);
            }

            if (js_begin.equals("")) {
                try {
                    InputStream is = am.open("hkmlApp_fb.js");
                    js_begin = readStream(is);
                } catch (IOException e) {
                    Log.e("@backgropund", e.getMessage(), e);
                }
            }

            return true;
        }

        protected void initWebview(String url) {

            MainActivity.this.fbTabLoaded = true;

            WebSettings webSettings = fbWebview.getSettings();
            //CustomJavascriptInterface myJavaScriptInterface = new CustomJavascriptInterface(MainActivity.this);
            //webview.addJavascriptInterface(myJavaScriptInterface, "AndroidFunction");
            webSettings.setJavaScriptEnabled(true);
            fbWebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    //syncCookies();

                    // note: prevent "back" load history page will not invoke title event
                    // js - added code to prevent double invoke of jQuery operations
                    injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
                    //injectScript(view, jsTouchSwipeUrl);
                    injectScript(view, js_begin);
                    // --

                    // cater for if .goBack() is called and the setTitle event is not fired
                    ma.setTitle(view.getTitle());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Log.v("@fbwebview url:", url);

                    if (! (url.startsWith("https://www.facebook.com/") || url.startsWith("https://m.facebook.com/") || url.startsWith("https://facebook.com/"))) {
                        if (url.startsWith("http://www.hkml.net/")) {

                            webview.loadUrl(url);

                            tabhost.setCurrentTab(0);
                        } else {
                            // open with OS default browser
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(browserIntent);
                        }

                        return true;
                    }

                    return super.shouldOverrideUrlLoading(view, url);
                }

            });

            fbWebview.setWebChromeClient(new WebChromeClient() {

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    ma.setTitle(title);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //Log.v("HKMLApp", "@progress: " + Integer.toString(newProgress) + "\n");
                    fbProgressBar.setProgress(newProgress);

                    if (newProgress >= 50) {
                        // let the "back" pages change to App layout faster
                        injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
                        //injectScript(view, jsTouchSwipeUrl);
                        injectScript(view, js_begin);
                    }
                }

                @SuppressWarnings("unused")
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                    mUploadMessage = uploadMsg;
                    pickFile(AcceptType);
                }

                @SuppressWarnings("unused")
                public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                    mUploadMessage = uploadMsg;
                    pickFile(AcceptType);
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    mUploadMessage = uploadMsg;
                    pickFile("image/*");

                }

                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {
                    mUploadMessageArray = uploadMsg;
                    pickFile("image/*");
                    return true;
                }

            });

            fbWebview.loadUrl(fbStartUrl);

        }

        protected void onPostExecute(Boolean result) {
            initWebview(MainActivity.this.fb_hkml_group);
        }

        private void injectScript(WebView view, String script) {
            //Log.v("HTMLApp", "@script: " + script);

            // String-ify the script byte-array using BASE64 encoding !!!
            String encoded = "";
            try {
                encoded = Base64.encodeToString(script.getBytes("Big5"), Base64.NO_WRAP);
            } catch (java.io.UnsupportedEncodingException e) {
                // do nothing
                Log.v("HKMLApp","@script_error: " + e.getMessage());
            }
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");

        }

        private String getFromHttp(String urlstr) {
            String content = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(urlstr);

                //Log.v("HKMLApp", "@url: " + urlstr);

                urlConnection = (HttpURLConnection) url
                        .openConnection();

                int responseCode = urlConnection.getResponseCode();

                //Log.v("HKMLApp", "@responsecode: " + Integer.toString(responseCode));

                if(responseCode == HttpURLConnection.HTTP_OK){
                    content = readStream(urlConnection.getInputStream());
                    //Log.v("HKMLApp", "@content: " + content);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("HKMLApp", "@error: " + e.getMessage() + "\n", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            return content;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuffer response = new StringBuffer();
            try {
                reader = new BufferedReader(new InputStreamReader(in, "big5"));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    response.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return response.toString();
        }

    }

    public class CustomJavascriptInterface {
        Context mContext;
        CustomJavascriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public int getVersionCode() {
            int vcode = 0;
            try {
                PackageInfo pInfo = MainActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
                vcode = pInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return vcode;
        }

        @JavascriptInterface
        public void onImageClick(final int idx, final String[] urls) {
            for(int i=0; i<urls.length; i++) {
                if (!(urls[i].indexOf("http://") == 0 || urls[i].indexOf("https://") == 0)) {
                    urls[i] = base_path + urls[i];
                }
            }

            Log.v("@js", Integer.toString(idx));
            Log.v("@js", Arrays.toString(urls));

            if (urls.length > 0 && idx >= 0) {

                MainActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        syncCookies();

                        /*
                        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                                .setFailureImage(R.drawable.ic_broken_image_124dp)
                                .setPlaceholderImage(R.drawable.ic_preimage_124dp);

                        new ImageViewer.Builder(mContext, urls)
                                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                                .setStartPosition(idx).show();
                         */
                        new StfalconImageViewer.Builder<String>(mContext, urls, new ImageLoader<String>(){
                            public void loadImage(ImageView imageView, String image) {
                                Glide.with(mContext).load(image)
                                        .placeholder(R.drawable.ic_preimage_124dp)
                                        .error(R.drawable.ic_broken_image_124dp)
                                        .into(imageView);
                            }
                        }).withStartPosition(idx).show();
                    }
                });
            }
        }
    }

}
