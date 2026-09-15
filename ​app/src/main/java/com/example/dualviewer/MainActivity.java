package com.example.dualviewer;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class MainActivity extends Activity {
    private WebView webView1;
    private WebView webView2;
    private View divider;
    private LinearLayout rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        rootLayout = findViewById(R.id.rootLayout);
        webView1 = findViewById(R.id.webView1);
        webView2 = findViewById(R.id.webView2);
        divider = findViewById(R.id.divider);

        setupWebView(webView1, "https://www.youtube.com");
        setupWebView(webView2, "https://www.amazon.co.jp");
        setupDividerTouchListener();
    }

    private void setupWebView(WebView webView, String url) {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            settings.setMediaPlaybackRequiresUserGesture(false);
        }

        String userAgent = "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
        settings.setUserAgentString(userAgent);

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        }

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(url);
    }

    private void setupDividerTouchListener() {
        divider.setOnTouchListener(new View.OnTouchListener() {
            private float lastY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getRawY();
                        float deltaY = currentY - lastY;

                        int totalHeight = rootLayout.getHeight() - divider.getHeight();
                        if (totalHeight <= 0) return true;

                        int newHeight1 = webView1.getHeight() + (int) deltaY;
                        int minHeight = 150;

                        if (newHeight1 < minHeight) newHeight1 = minHeight;
                        if (newHeight1 > totalHeight - minHeight) newHeight1 = totalHeight - minHeight;

                        float weight1 = (float) newHeight1 / totalHeight;
                        float weight2 = 1.0f - weight1;

                        LinearLayout.LayoutParams p1 = (LinearLayout.LayoutParams) webView1.getLayoutParams();
                        LinearLayout.LayoutParams p2 = (LinearLayout.LayoutParams) webView2.getLayoutParams();

                        p1.weight = weight1;
                        p2.weight = weight2;

                        webView1.setLayoutParams(p1);
                        webView2.setLayoutParams(p2);

                        lastY = currentY;
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (webView1.canGoBack()) {
            webView1.goBack();
        } else if (webView2.canGoBack()) {
            webView2.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
