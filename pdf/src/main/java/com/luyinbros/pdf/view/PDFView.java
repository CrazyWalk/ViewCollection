package com.luyinbros.pdf.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;

import java.io.File;
import java.lang.reflect.Field;

public class PDFView extends FrameLayout implements PDFViewController {
    private WebView mWebView;


    public PDFView(Context context) {
        this(context, null);
    }

    public PDFView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PDFView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mWebView = new WebView(getContext());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportZoom(true);
        if (Build.VERSION.SDK_INT > 16) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        addView(mWebView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mWebView.setVisibility(GONE);
        mWebView.stopLoading();
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.removeAllViews();
        removeAllViews();
        mWebView.setWebChromeClient(null);
        mWebView.setWebViewClient(null);
        mWebView.destroy();
        try {
            Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            sConfigCallback.setAccessible(true);
            sConfigCallback.set(null, null);
        } catch (Exception e) {
            //no Action
        }
    }


    @Override
    public void loadPdf(File file) {
        mWebView.loadUrl("file:///android_asset/pdfjs/web/viewer.html?file=" + file.getAbsolutePath());
        //mWebView.loadUrl("file:///android_asset/index.html?" + file.getAbsolutePath());
    }


}
