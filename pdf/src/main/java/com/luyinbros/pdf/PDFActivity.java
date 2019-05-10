package com.luyinbros.pdf;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.luyinbros.pdf.cache.PDFManager;
import com.luyinbros.pdf.view.PDFView;

import java.io.File;

public class PDFActivity extends AppCompatActivity {
    private PDFView pdfView;
    private PDFManager.SaveSession mSaveSession;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdfView = new PDFView(this);
        setContentView(pdfView);
        mSaveSession = PDFManager.getInstance(this).openSession();
        mSaveSession.setOnSaveListener(new PDFManager.OnSaveListener() {
            @Override
            public void onStart() {
                Log.d("progress", "onStart");
            }

            @Override
            public void onProgress(int progress) {
                Log.d("progress", "" + progress);
            }

            @Override
            public void onSuccess(final File file) {
                pdfView.loadPdf(file);
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
        loadUrl("https://hifidoc.oss-cn-hangzhou.aliyuncs.com/release/pdf/20190509140122903");
    }


    private void loadUrl(String url) {
        File file = PDFManager.getInstance(this).getCacheFile(url);
        if (file != null && file.isFile()) {
            pdfView.loadPdf(file);
        } else {
            mSaveSession.start(url);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSaveSession.dispose();
    }
}
