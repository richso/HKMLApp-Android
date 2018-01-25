package net.netrogen.hkmlapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private final static int EXTERNAL_STORAGE_PERMISSION_REQUEST = 100;

    private WebView webview;
    private ConstraintLayout container;
    final private String base_path = "http://www.hkml.net/Discuz/";
    final private String mainUrl = "http://www.hkml.net/Discuz/index.php";
    final private String jqCDN_url = "http://code.jquery.com/jquery-1.12.4.min.js";
    final private String touchSwipeUrl = "https://raw.githubusercontent.com/mattbryson/TouchSwipe-Jquery-Plugin/master/jquery.touchSwipe.min.js";

    final private String js_begin_url = "https://raw.githubusercontent.com/richso/hkmlApp/master/public_html/hkmlApp_ios.js";

    private BottomNavigationView navigation;
    private ProgressBar progressBar;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessageArray;
    private ImagePicker imagePicker;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_home:
                    webview.loadUrl(mainUrl);
                    navigation.setSelected(false);
                    return true;
                case R.id.action_back:
                    webview.goBack();
                    navigation.setSelected(false);
                    return true;
                case R.id.action_forward:
                    webview.goForward();
                    navigation.setSelected(false);
                    return true;
                case R.id.action_share:
                    String url = webview.getUrl();
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_TEXT, url);

                    startActivity(Intent.createChooser(share, "分享"));

                    navigation.setSelected(false);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

        java.net.CookieManager cookieManager = new java.net.CookieManager();
        CookieHandler.setDefault(cookieManager);

        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelected(false);

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

        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();

        Fresco.initialize(this, config);

        new WebTask().execute(this);

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

    private class WebTask extends AsyncTask<MainActivity, Integer, Boolean> {

        private String js_begin = "";
        private String jqCDN = "";
        private String jsTouchSwipeUrl = "";
        private MainActivity ma;

        protected Boolean doInBackground(MainActivity... mas) {
            ma = mas[0];

            jqCDN = getFromHttp(MainActivity.this.jqCDN_url);
            jsTouchSwipeUrl = getFromHttp(MainActivity.this.touchSwipeUrl);
            js_begin = getFromHttp(MainActivity.this.js_begin_url);

            return true;
        }

        protected void initWebview(String url) {
            final String urlstr = url;

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

                    navigation.getMenu().findItem(R.id.action_back).setEnabled(webview.canGoBack());
                    navigation.getMenu().findItem(R.id.action_forward).setEnabled(webview.canGoForward());

                    // cater for if .goBack() is called and the setTitle event is not fired
                    ma.setTitle(view.getTitle());
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.startsWith("facebookshare:")) {
                        // open fb share dialog

                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, url);

                        startActivity(Intent.createChooser(share, "分享"));

                        return true;
                    } else if (! url.startsWith("http://www.hkml.net/")) {
                        // open with OS default browser
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        startActivity(browserIntent);

                        return true;
                    }

                    return super.shouldOverrideUrlLoading(view, url);
                }

            });

            webview.setWebChromeClient(new WebChromeClient() {

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
                        navigation.getMenu().findItem(R.id.action_back).setEnabled(webview.canGoBack());
                        navigation.getMenu().findItem(R.id.action_forward).setEnabled(webview.canGoForward());
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

                        View video = frame.getFocusedChild();
                        frame.removeView(video);
                        MainActivity.this.setContentView(video);

                    }
                }
                public void onHideCustomView() {
                    MainActivity.this.setContentView(R.layout.activity_main);
                    WebTask.this.initWebview(webview.getUrl());
                }
            });

            webview.loadUrl(url);
        }

        protected void onPostExecute(Boolean result) {
            initWebview(mainUrl);
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

        private String encodeScript(String script_text) {
            Pattern pDQ = Pattern.compile("\"");
            Pattern pCR = Pattern.compile("\r");
            Pattern pLF = Pattern.compile("\n");

            script_text = pDQ.matcher(script_text).replaceAll("\\\\\"");
            script_text = pCR.matcher(script_text).replaceAll("\\\\r");
            script_text = pLF.matcher(script_text).replaceAll("\\\\n");

            //Log.v("HKMLApp", "@script: " + script_text);

            return script_text;
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

                        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                                .setFailureImage(R.drawable.ic_broken_image_124dp)
                                .setPlaceholderImage(R.drawable.ic_preimage_124dp);

                        new ImageViewer.Builder(mContext, urls)
                                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                                .setStartPosition(idx).show();
                    }
                });
            }
        }
    }

}
