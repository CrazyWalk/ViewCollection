package org.luyinbros.widget.status;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import org.luyinbros.widget.R;

public class SampleFailureStatusPage extends ConstraintLayout implements StatusLayoutController.StatusPage {

    public SampleFailureStatusPage(Context context) {
        super(context);
        View.inflate(context, R.layout.status_page_failure_default, this);
    }


    @Override
    public int getStatus() {
        return StatusLayoutController.STATUS_PAGE_FAILURE;
    }
}
