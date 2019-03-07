package com.yakin.fastselector;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.yakin.fastselector.utils.EventUtil;

public class SelectionMode {

    private Activity activity;
    private Fragment fragment;

    private SelectionConfig config;

    SelectionMode(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
        this.config = new SelectionConfig();
    }

    public SelectionMode setChooseType(ChooseType chooseType) {
        config.chooseType = chooseType;
        return this;
    }

    public SelectionMode setMultiple(boolean multiple) {
        config.multiple = multiple;
        return this;
    }

    public void forResult(int requestCode) {
        if (!EventUtil.isFastDoubleClick()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(config.chooseType.getMimeType());
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, config.multiple);
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }
}
