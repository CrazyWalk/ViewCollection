package org.luyinbros.dialog;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.DateRangeLimiter;

import org.luyinbros.dialog.core.DialogUtils;
import org.luyinbros.dialog.view.DatePickerDelegate;

import java.util.Calendar;

public class DatePickerDialogBuilder {
    private DatePickerDelegate.CompatDatePickerDialog mDialog;
    private FragmentManager mFragmentManager;
    private Context mContext;
    private OnDatePickListener onDatePickListener;

    public DatePickerDialogBuilder(@Nullable Context context) {
        mContext = DialogUtils.getDialogContext(context);
        mFragmentManager = DialogUtils.getFragmentManager(context);
        mDialog = DatePickerDelegate.CompatDatePickerDialog.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                if (onDatePickListener != null) {
                    onDatePickListener.onPick(year, monthOfYear, dayOfMonth);
                }
                view.dismiss();
            }
        });

    }

    public DatePickerDialogBuilder setOnDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
        return this;
    }

    public DatePickerDialogBuilder setCurrentDate(int year, int month, int day) {
        if (isDateValid(year, month, day)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            mDialog.setSelectableDays(new Calendar[]{calendar});
        }
        return this;
    }

    public DatePickerDialogBuilder setMinDate(int year, int month, int day) {
        if (isDateValid(year, month, day)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            mDialog.setMinDate(calendar);
        }
        return this;
    }

    public DatePickerDialogBuilder setMaxDate(int year, int month, int day) {
        if (isDateValid(year, month, day)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month - 1);
            calendar.set(Calendar.DAY_OF_MONTH, day);
            mDialog.setMaxDate(calendar);
        }
        return this;
    }

    public void show() {
        if (mContext != null && mFragmentManager != null) {


            DialogFactory.showDialog(mDialog, mFragmentManager, new Bundle(), DialogFactory.DATE_PICKER_DIALOG_NAME);
        }
    }


    private boolean isDateValid(int year, int month, int day) {
        return isYearValid(year) && isMonthValid(month)
                && isDayValid(year, month, day);
    }

    private boolean isYearValid(int year) {
        return year >= 1949 && year < 2100;
    }

    private boolean isMonthValid(int month) {
        return month >= 1 && month <= 12;
    }

    private boolean isDayValid(int year, int month, int day) {
        return day >= 1 && day <= getDay(year, month);
    }

    private int getDay(int year, @IntRange(from = 1, to = 12) int month) {
        if (month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 2) {
            return isLeapYear(year) ? 29 : 28;
        } else {
            return 30;
        }

    }

    private static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;

    }

    private static class DefaultDateRangerLimiter implements DateRangeLimiter {
        private int minYear;
        private int minMonth;
        private int minDay;
        private int maxYear;
        private int maxMonth;
        private int maxDay;


        public DefaultDateRangerLimiter() {
            minYear = 1949;
            minMonth = 1;
            minDay = 1;
            maxYear = 2050;
            maxMonth = 12;
            maxDay = 31;

        }

        public void setMinDate(int year, int month, int day) {
            minYear = year;
            minMonth = month;
            minDay = day;
        }

        public void setMaxDate(int year, int month, int day) {
            maxYear = year;
            maxMonth = month;
            maxDay = day;
        }

        @Override
        public int getMinYear() {
            return minYear;
        }

        @Override
        public int getMaxYear() {
            return maxYear;
        }

        @NonNull
        @Override
        public Calendar getStartDate() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, minYear);
            calendar.set(Calendar.MONTH, minMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, minDay);
            return calendar;
        }

        @NonNull
        @Override
        public Calendar getEndDate() {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, maxYear);
            calendar.set(Calendar.MONTH, maxMonth - 1);
            calendar.set(Calendar.DAY_OF_MONTH, maxDay);
            return calendar;
        }

        @Override
        public boolean isOutOfRange(int year, int month, int day) {
            if (year > maxYear || year < minYear) {
                return true;
            } else if (year == maxYear) {
                if (month < maxMonth) {
                    return false;
                } else if (month == maxMonth) {
                    return day <= maxDay;
                } else {
                    return false;
                }
            } else if (year == minYear) {
                if (month > minMonth) {
                    return false;
                } else if (month == minMonth) {
                    return day > minDay;
                } else {
                    return false;
                }
            }
            return false;
        }

        @NonNull
        @Override
        public Calendar setToNearestDate(@NonNull Calendar day) {
            return day;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.minYear);
            dest.writeInt(this.minMonth);
            dest.writeInt(this.minDay);
            dest.writeInt(this.maxYear);
            dest.writeInt(this.maxMonth);
            dest.writeInt(this.maxDay);
        }


        protected DefaultDateRangerLimiter(Parcel in) {
            this.minYear = in.readInt();
            this.minMonth = in.readInt();
            this.minDay = in.readInt();
            this.maxYear = in.readInt();
            this.maxMonth = in.readInt();
            this.maxDay = in.readInt();
        }

        public static final Creator<DefaultDateRangerLimiter> CREATOR = new Creator<DefaultDateRangerLimiter>() {
            @Override
            public DefaultDateRangerLimiter createFromParcel(Parcel source) {
                return new DefaultDateRangerLimiter(source);
            }

            @Override
            public DefaultDateRangerLimiter[] newArray(int size) {
                return new DefaultDateRangerLimiter[size];
            }
        };
    }

    public interface OnDatePickListener {
        void onPick(int year, int month, int day);
    }
}
