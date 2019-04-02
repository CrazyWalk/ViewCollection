package org.luyinbros.demo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import org.luyinbros.data.Address;
import org.luyinbros.dialog.AddressPickerDialogBuilder;
import org.luyinbros.dialog.DatePickerDialogBuilder;
import org.luyinbros.widget.R;
import org.luyinbros.widget.status.DefaultStatusLayoutController;
import org.luyinbros.widget.status.OnPageRefreshListener;
import org.luyinbros.widget.status.SampleEmptyStatusPage;
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
        statusLayoutController.addStatusPage(new SampleEmptyStatusPage(this));
        statusLayoutController.addStatusPage(StatusViewFactory.defaultFailurePage(this));
        statusLayoutController.addStatusPage(StatusViewFactory.defaultRefreshPage(this));
        statusLayoutController.setOnPageRefreshListener(new OnPageRefreshListener() {
            @Override
            public void onPageRefresh() {
                Log.d(TAG, "onPageRefresh: ");
            }
        });
    }

    public void onContentClick(View v) {
//        new DatePickerDialogBuilder(this)
//                .setOnDatePickListener(new DatePickerDialogBuilder.OnDatePickListener() {
//                    @Override
//                    public void onPick(int year, int month, int day) {
//                        Log.d(TAG, "onPick: " + year + " " + month + " " + day);
//                    }
//                })
//
//                .setMinDate(1993, 12, 18)
//                .setMaxDate(2000, 12, 18)
//                .setCurrentDate(193, 12, 19)
//                .show();
        new AddressPickerDialogBuilder(this)
                .setOnAddressPickListener(new AddressPickerDialogBuilder.OnAddressPickListener() {
                    @Override
                    public void onPicked(@Nullable Address province, @Nullable Address city, @Nullable Address district) {
                        Log.d(TAG, "onPicked: " + province.getName() + city.getName() + district.getName());
                    }
                })
                .show();
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
