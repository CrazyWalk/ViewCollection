package org.luyinbros.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import org.luyinbros.data.Address;
import org.luyinbros.dialog.core.DialogUtils;
import org.luyinbros.dialog.view.AddressPickerDialog;


public class AddressPickerDialogBuilder {
    private FragmentManager mFragmentManager;
    private Context mContext;
    private OnAddressPickListener mOnAddressPickListener;

    public AddressPickerDialogBuilder(Context context) {
        mContext = DialogUtils.getDialogContext(context);
        mFragmentManager = DialogUtils.getFragmentManager(context);
    }

    public AddressPickerDialogBuilder setOnAddressPickListener(OnAddressPickListener onAddressPickListener) {
        this.mOnAddressPickListener = onAddressPickListener;
        return this;
    }

    public void show() {
        if (mContext != null && mFragmentManager != null) {
            AddressPickerDialog addressPickerDialog = new AddressPickerDialog();
            addressPickerDialog.setOnAddressPickListener(mOnAddressPickListener);
            DialogFactory.showDialog(addressPickerDialog, mFragmentManager, new Bundle(), DialogFactory.ADDRESS_PICKER_DIALOG_NAME);
        }
    }

    public interface OnAddressPickListener {
        void onPicked(@Nullable Address province, @Nullable Address city, @Nullable Address district);
    }


}
