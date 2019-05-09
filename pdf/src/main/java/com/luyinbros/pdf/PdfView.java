package com.luyinbros.pdf;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.luyinbros.pdf.cache.PdfCache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PdfView extends FrameLayout implements PdfViewController {
    private WebView mWebView;
    private static volatile SoftReference<OkHttpClient> clientSoftReference;
    private PdfObserver pdfObserver;
    private PdfCache pdfCache;
    private Call call;

    public PdfView(Context context) {
        this(context, null);
    }

    public PdfView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PdfView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        try {
            pdfCache = new PdfCache((Application) context.getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
        }

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    if (pdfObserver != null) {
                        pdfObserver.onLoadSuccess();
                    }
                }
            }
        });
        addView(mWebView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelCall();
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

    @NonNull
    private static synchronized OkHttpClient getClient() {
        if (clientSoftReference != null) {
            OkHttpClient okHttpClient = clientSoftReference.get();
            if (okHttpClient != null) {
                return okHttpClient;
            }
        }
        OkHttpClient client = new OkHttpClient();
        clientSoftReference = new SoftReference<>(client);
        return client;
    }

    @Override
    public void setPdfObserver(PdfObserver pdfObserver) {
        this.pdfObserver = pdfObserver;
    }

    private void loadPdf(File file) {
        mWebView.loadUrl("file:///android_asset/index.html?" + file.getAbsolutePath());
    }

    private void cancelCall() {
        try {
            if (call != null && !call.isCanceled()) {
                call.cancel();
            }
            call = null;
        } catch (Throwable e) {
            //safe
        }
    }

    @Override
    public void loadUrl(final String url) {
        cancelCall();
        File file = pdfCache.getCacheFile(url);
        if (file != null && file.isFile()) {
            loadPdf(file);
        } else {
            call = getClient().newCall(new Request.Builder().url(url).build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    if (pdfObserver != null) {
                        pdfObserver.onLoadFailure();
                    }
                    Log.d("PDFView", "下载失败");
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    try {
                        ResponseBody responseBody = response.body();
                        File resultFile = pdfCache.saveDiskCache(url, responseBody.byteStream(), new PdfCache.OnSaveListener() {
                            @Override
                            public void onProgress(int progress) {
                                Log.d("PDFView", "下载中 进度:" + progress);
                            }
                        }, responseBody.contentLength());
                        loadPdf(resultFile);
                    } catch (Exception e) {
                        if (pdfObserver != null) {
                            pdfObserver.onLoadFailure();
                        }
                    }
                }
            });
        }
    }
}
