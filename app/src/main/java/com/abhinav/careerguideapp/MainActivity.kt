package com.abhinav.careerguideapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                return if (url != null && url.startsWith("file:///android_asset/www/")) {
                    false
                } else {
                    // Force SPA routing inside WebView
                    view?.loadUrl("file:///android_asset/www/index.html")
                    true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                removeLovableBadge()
            }
        }


        webView.webChromeClient = WebChromeClient()

        // IMPORTANT — THIS IS YOUR REAL PATH NOW
        webView.loadUrl("file:///android_asset/www/index.html")
    }

    private fun removeLovableBadge() {
        val js = """
            var style = document.createElement('style');
            style.innerHTML = '[class*="lovable"], [data-lovable] { display: none !important; }';
            document.head.appendChild(style);
        """.trimIndent()
        webView.evaluateJavascript(js, null)
    }

    override fun onBackPressed() {
        if (this::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
