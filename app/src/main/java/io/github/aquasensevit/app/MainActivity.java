package io.github.aquasensevit.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * AquaSense Android app: shows the AquaSense web app full screen.
 * Online it loads the live site (always the newest version); offline it falls back to the copy bundled in the APK.
 */
public class MainActivity extends Activity {

    private static final String HOME = "https://aquasense-vit.github.io/";
    private static final String HOST = "aquasense-vit.github.io";
    private static final String OFFLINE = "file:///android_asset/site/index.html";

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window w = getWindow();
        w.setStatusBarColor(Color.parseColor("#0c1614"));
        w.setNavigationBarColor(Color.parseColor("#0c1614"));

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#0c1614"));
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setCacheMode(WebSettings.LOAD_DEFAULT);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setUserAgentString(s.getUserAgentString() + " AquaSenseAndroid/1.0");

        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                String scheme = u.getScheme() == null ? "" : u.getScheme();
                if ("file".equals(scheme) || HOST.equals(u.getHost())) return false;
                // other websites (GitHub, the APK download, the weather service page) open in the phone's browser
                try { startActivity(new Intent(Intent.ACTION_VIEW, u)); } catch (Exception ignored) { }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame() && !OFFLINE.equals(view.getUrl())) {
                    String hash = request.getUrl().getFragment();
                    view.loadUrl(OFFLINE + (hash != null ? "#" + hash : ""));
                }
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(HOME);
        }
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (web != null) web.saveState(outState);
    }
}
