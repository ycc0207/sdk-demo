package com.epgis.offline.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yangsimin on 2018/4/12.
 */

public class FragmentStackEntity implements Parcelable {

    public static final int REQUEST_CODE_INVALID = -1;

    public boolean isSticky = false;
    public int requestCode = REQUEST_CODE_INVALID;
    @ResultCode
    int resultCode = Activity.RESULT_CANCELED;
    public Bundle result = null;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isSticky ? (byte) 1 : (byte) 0);
        dest.writeInt(this.requestCode);
        dest.writeInt(this.resultCode);
        dest.writeBundle(this.result);
    }

    public FragmentStackEntity() {
    }

    protected FragmentStackEntity(Parcel in) {
        this.isSticky = in.readByte() != 0;
        this.requestCode = in.readInt();
        this.resultCode = in.readInt();
        this.result = in.readBundle();
    }

    public static final Creator<FragmentStackEntity> CREATOR = new Creator<FragmentStackEntity>() {
        @Override
        public FragmentStackEntity createFromParcel(Parcel source) {
            return new FragmentStackEntity(source);
        }

        @Override
        public FragmentStackEntity[] newArray(int size) {
            return new FragmentStackEntity[size];
        }
    };
}
