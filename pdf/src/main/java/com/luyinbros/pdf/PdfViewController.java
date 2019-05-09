package com.luyinbros.pdf;

public interface PdfViewController {

    void loadUrl(String url);

    void setPdfObserver(PdfObserver pdfObserver);

    interface PdfObserver {
        void onLoading(int progress);

        void onLoadSuccess();

        void onLoadFailure();
    }

    class Config {

    }
}
