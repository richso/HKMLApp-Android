package net.netrogen.hkmlapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private WebView webview;
    private ConstraintLayout container;
    final private String mainUrl = "http://www.hkml.net/Discuz/index.php";
    final private String js_begin_url = "https://raw.githubusercontent.com/richso/hkmlApp/master/public_html/hkmlApp.js";
    final private String jqCDN_url = "http://code.jquery.com/jquery-1.12.4.min.js";
    private BottomNavigationView navigation;
    private ProgressBar progressBar;


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
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
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

        new WebTask().execute(this);

    }

    private class WebTask extends AsyncTask<MainActivity, Integer, Boolean> {

        private String js_begin = "";
        private String jqCDN = "";
        private MainActivity ma;

        protected Boolean doInBackground(MainActivity... mas) {
            ma = mas[0];
            Random r = new Random();

            jqCDN = getFromHttp(MainActivity.this.jqCDN_url);
            js_begin = getFromHttp(MainActivity.this.js_begin_url + "?" + Double.toString(r.nextDouble()));

            return true;
        }

        protected void onPostExecute(Boolean result) {
            webview = (WebView) findViewById(R.id.webview);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {

                    // note: prevent "back" load history page will not invoke title event
                    // js - added code to prevent double invoke of jQuery operations
                    injectScript(view, jqCDN + "\n $j = jQuery.noConflict(); ");
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
                    injectScript(view, js_begin);
                }

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //Log.v("HKMLApp", "@progress: " + Integer.toString(newProgress) + "\n");
                    progressBar.setProgress(newProgress);
                }
            });
            webview.loadUrl(mainUrl);
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

}
