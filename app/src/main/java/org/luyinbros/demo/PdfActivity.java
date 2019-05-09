package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.luyinbros.pdf.PdfView;

public class PdfActivity extends AppCompatActivity {
    private PdfView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pdfView=new PdfView(this);
        setContentView(pdfView);
        pdfView.loadUrl("https://hifidoc.oss-cn-hangzhou.aliyuncs.com/release/pdf/20190509140122903");
    }
}
