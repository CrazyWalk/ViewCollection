package org.luyinbros.widget.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.luyinbros.widget.R;
import org.luyinbros.widget.status.DefaultStatusLayoutController;
import org.luyinbros.widget.status.StatusLayoutController;
import org.luyinbros.widget.status.StatusViewFactory;

public class StatusViewActivity extends AppCompatActivity {
    private static final String TAG = "StatusViewActivity";
    private StatusLayoutController statusLayoutController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_view);
        statusLayoutController = new DefaultStatusLayoutController(findViewById(R.id.mContentView));
        statusLayoutController.addStatusPage(StatusViewFactory.defaultEmptyPage(this));
        statusLayoutController.addStatusPage(StatusViewFactory.defaultFailurePage(this));
        statusLayoutController.addStatusPage(StatusViewFactory.defaultRefreshPage(this));
        statusLayoutController.setOnPageRefreshListener(new StatusLayoutController.OnPageRefreshListener() {
            @Override
            public void onPageRefresh() {
                Log.d(TAG, "onPageRefresh: ");
            }
        });
    }

    public void onContentClick(View v) {
        statusLayoutController.showContentView();
    }

    public void onEmptyClick(View v) {
        statusLayoutController.showEmptyPage();
    }

    public void onFailureClick(View v) {
        statusLayoutController.showFailurePage();
    }

    public void onPageRefreshClick(View v) {
        statusLayoutController.showRefreshPage();
    }
}
