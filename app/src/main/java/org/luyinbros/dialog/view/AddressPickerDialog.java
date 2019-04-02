package org.luyinbros.dialog.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import org.luyinbros.data.Address;
import org.luyinbros.data.ViewAssetsDataHelper;
import org.luyinbros.dialog.AddressPickerDialogBuilder;
import org.luyinbros.widget.R;
import org.luyinbros.widget.WheelView;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddressPickerDialog extends DialogFragment implements DialogInterface {

    private AddressPickerDialogBuilder.OnAddressPickListener mOnAddressPickListener;
    private View negativeButton;
    private View positiveButton;
    private Disposable mDisposable;
    private WheelView provinceListView;
    private WheelView cityListView;
    private WheelView districtListView;
    private List<Address> mAddressList;

    private int mSelectedProvinceIndex = -1;
    private int mSelectedCityIndex = -1;
    private int mSelectedDistrictIndex = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BottomDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_address_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        negativeButton = view.findViewById(R.id.negativeButton);
        positiveButton = view.findViewById(R.id.positiveButton);
        provinceListView = view.findViewById(R.id.provinceListView);
        cityListView = view.findViewById(R.id.cityListView);
        districtListView = view.findViewById(R.id.districtListView);

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissAllowingStateLoss();
            }
        });
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnAddressPickListener != null) {
                    if (mSelectedProvinceIndex != -1 && mSelectedCityIndex != -1 && mSelectedDistrictIndex != -1) {
                        mOnAddressPickListener.onPicked(mAddressList.get(mSelectedProvinceIndex),
                                mAddressList.get(mSelectedProvinceIndex).getChild().get(mSelectedCityIndex),
                                mAddressList.get(mSelectedProvinceIndex).getChild().get(mSelectedCityIndex).getChild().get(mSelectedCityIndex));
                    }
                }
                dismissAllowingStateLoss();
            }
        });

        initWheelView(provinceListView);
        initWheelView(cityListView);
        initWheelView(districtListView);

        provinceListView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (index > -1) {
                    mSelectedProvinceIndex = index;
                    mSelectedCityIndex = 0;
                    mSelectedDistrictIndex = 0;
                    cityListView.setAdapter(new AddressAdapter(mAddressList.get(mSelectedProvinceIndex).getChild()));
                    cityListView.setCurrentItem(mSelectedCityIndex);
                    districtListView.setAdapter(new AddressAdapter(mAddressList.get(mSelectedProvinceIndex).getChild().get(mSelectedCityIndex).getChild()));
                    districtListView.setCurrentItem(mSelectedDistrictIndex);
                }
            }
        });

        cityListView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                if (index > -1) {
                    mSelectedCityIndex = index;
                    mSelectedDistrictIndex = 0;
                    districtListView.setAdapter(new AddressAdapter(mAddressList.get(mSelectedProvinceIndex).getChild().get(mSelectedDistrictIndex).getChild()));
                }
            }
        });

        districtListView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int index) {
                mSelectedDistrictIndex = index;
            }
        });

        ViewAssetsDataHelper.getAddress(getContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Address>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(List<Address> addresses) {
                        mAddressList = addresses;
                        provinceListView.setAdapter(new AddressAdapter(addresses));
                        List<Address> cityList = addresses.get(0).getChild();
                        cityListView.setAdapter(new AddressAdapter(cityList));
                        if (cityList != null && cityList.size() > 0) {
                            districtListView.setAdapter(new AddressAdapter(cityList.get(0).getChild()));
                        }
                        mSelectedProvinceIndex = 0;
                        mSelectedCityIndex = 0;
                        mSelectedDistrictIndex = 0;
                        provinceListView.setCurrentItem(0);
                        cityListView.setCurrentItem(0);
                        districtListView.setCurrentItem(0);

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Context context = getContext();
        Dialog dialog = getDialog();
        if (context != null && dialog != null) {
            dialog.setCanceledOnTouchOutside(true);
            Window window = dialog.getWindow();
            if (window == null) {
                return;
            }
            WindowManager.LayoutParams wlp = window.getAttributes();
            wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
            wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            wlp.gravity = Gravity.BOTTOM;
            window.setAttributes(wlp);
        }
    }

    private void initWheelView(WheelView wheelView) {
        wheelView.setCyclic(false);
        wheelView.setGravity(Gravity.CENTER);
        wheelView.setTextSize(20);
        wheelView.setTextColorCenter(Color.BLACK);
        wheelView.setTextColorOut(Color.BLACK);
        wheelView.setDividerColor(Color.BLACK);
    }

    public void setOnAddressPickListener(AddressPickerDialogBuilder.OnAddressPickListener onAddressPickListener) {
        this.mOnAddressPickListener = onAddressPickListener;
    }

    private static class AddressAdapter extends WheelView.WheelAdapter<String> {
        private List<Address> list;

        public AddressAdapter(List<Address> list) {
            this.list = list;
        }

        @Override
        public int getItemsCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public String getItem(int index) {
            return list.get(index).getName();
        }


    }

    @Override
    public void cancel() {
        Dialog dialog = getDialog();
        dialog.cancel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}
