package com.abhinav.careerguideapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
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

        val settings = webView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.allowFileAccess = true
        settings.allowFileAccessFromFileURLs = true
        settings.allowUniversalAccessFromFileURLs = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {

                val url = request?.url.toString()

                // ✅ 1. Allow local files (your offline app)
                if (url.startsWith("file:///android_asset/www")) {
                    return false
                }

                // ✅ 2. External links → open in device browser
                if (
                    url.startsWith("http://") ||
                    url.startsWith("https://")
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)
                    return true
                }

                // Default: load inside WebView
                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                removeLovableBadge()
            }
        }

        webView.webChromeClient = WebChromeClient()

        // Load your offline app entry point
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
