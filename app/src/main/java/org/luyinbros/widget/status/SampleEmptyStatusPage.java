package org.luyinbros.widget.status;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;

import org.luyinbros.widget.R;

public class SampleEmptyStatusPage extends ConstraintLayout implements StatusLayoutController.StatusPage {

    public SampleEmptyStatusPage(Context context) {
        super(context);
        View.inflate(context, R.layout.status_page_empty_default, this);
    }


    @Override
    public int getStatus() {
        return StatusLayoutController.STATUS_PAGE_EMPTY;
    }
}
