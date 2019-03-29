package org.luyinbros.update;

import android.os.Parcel;

import org.luyinbros.update.core.AppUpdateInfo;


public class DefaultAppUpdateInfo implements AppUpdateInfo {
    private String apkUrl;
    private String lastUpdate;


    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.apkUrl);
        dest.writeString(this.lastUpdate);
    }

    public DefaultAppUpdateInfo() {
    }

    protected DefaultAppUpdateInfo(Parcel in) {
        this.apkUrl = in.readString();
        this.lastUpdate = in.readString();
    }

    public static final Creator<DefaultAppUpdateInfo> CREATOR = new Creator<DefaultAppUpdateInfo>() {
        @Override
        public DefaultAppUpdateInfo createFromParcel(Parcel source) {
            return new DefaultAppUpdateInfo(source);
        }

        @Override
        public DefaultAppUpdateInfo[] newArray(int size) {
            return new DefaultAppUpdateInfo[size];
        }
    };
}
