package org.luyinbros.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.ArrayList;

public class FragmentIntent {
    private Fragment from;
    private String toFragmentClsName;
    private Bundle mExtra = new Bundle();

    public FragmentIntent setClassName(Fragment from, Class toFragmentCls) {
        this.from = from;
        this.toFragmentClsName = toFragmentCls.getName();
        return this;
    }

    public FragmentIntent setClassName(Fragment from, String toFragmentClsName) {
        this.from = from;
        this.toFragmentClsName = toFragmentClsName;
        return this;
    }

    public FragmentIntent withBoolean(String key, boolean value) {
        mExtra.putBoolean(key, value);
        return this;
    }

    public FragmentIntent withByte(String key, byte value) {
        mExtra.putByte(key, value);
        return this;
    }

    public FragmentIntent withChar(String key, char value) {
        mExtra.putChar(key, value);
        return this;
    }

    public FragmentIntent withShort(String key, short value) {
        mExtra.putShort(key, value);
        return this;
    }

    public FragmentIntent withInt(String key, int value) {
        mExtra.putInt(key, value);
        return this;
    }

    public FragmentIntent withLong(String key, long value) {
        mExtra.putLong(key, value);
        return this;
    }

    public FragmentIntent withFloat(String key, float value) {
        mExtra.putFloat(key, value);
        return this;
    }

    public FragmentIntent withDouble(String key, double value) {
        mExtra.putDouble(key, value);
        return this;
    }

    public FragmentIntent withString(String key, String value) {

        mExtra.putString(key, value);
        return this;
    }

    public FragmentIntent withCharSequence(String key, CharSequence value) {

        mExtra.putCharSequence(key, value);
        return this;
    }

    public FragmentIntent withParcelable(String key, Parcelable value) {

        mExtra.putParcelable(key, value);
        return this;
    }

    public FragmentIntent withParcelableArray(String key, Parcelable[] value) {
        mExtra.putParcelableArray(key, value);
        return this;
    }

    public FragmentIntent withParcelableArrayList(String key, ArrayList<? extends Parcelable> value) {
        mExtra.putParcelableArrayList(key, value);
        return this;
    }


    public FragmentIntent withStringArrayList(String key, ArrayList<String> value) {
        mExtra.putStringArrayList(key, value);
        return this;
    }

    public FragmentIntent withCharSequenceArrayList(String key, ArrayList<CharSequence> value) {
        mExtra.putCharSequenceArrayList(key, value);
        return this;
    }

    public FragmentIntent withSerializable(String key, Serializable value) {
        mExtra.putSerializable(key, value);
        return this;
    }

    public FragmentIntent withBooleanArray(String key, boolean[] value) {
        mExtra.putBooleanArray(key, value);
        return this;
    }

    public FragmentIntent withByteArray(String key, byte[] value) {
        mExtra.putByteArray(key, value);
        return this;
    }

    public FragmentIntent withCharArray(String key, char[] value) {
        mExtra.putCharArray(key, value);
        return this;
    }

    public FragmentIntent withShortArray(String key, short[] value) {
        mExtra.putShortArray(key, value);
        return this;
    }

    public FragmentIntent withIntArray(String key, int[] value) {
        mExtra.putIntArray(key, value);
        return this;
    }

    public FragmentIntent withLongArray(String key, long[] value) {
        mExtra.putLongArray(key, value);
        return this;
    }

    public FragmentIntent withFloatArray(String key, float[] value) {
        mExtra.putFloatArray(key, value);
        return this;
    }

    public FragmentIntent withDoubleArray(String key, double[] value) {
        mExtra.putDoubleArray(key, value);
        return this;
    }

    public FragmentIntent withStringArray(String key, String[] value) {
        mExtra.putStringArray(key, value);
        return this;
    }

    public FragmentIntent withCharSequence(String key, CharSequence[] value) {
        mExtra.putCharSequenceArray(key, value);
        return this;
    }

    public FragmentIntent withBundle(String key, Bundle value) {
        mExtra.putBundle(key, value);
        return this;
    }

    public FragmentIntent withAllBundle(Bundle bundle) {
        if (bundle != null) {
            mExtra.putAll(bundle);
        }
        return this;
    }

    public FragmentIntent withNewBundle(Bundle bundle) {
        if (bundle != null) {
            mExtra = bundle;
        }
        return this;
    }

}
