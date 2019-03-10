package com.yakin.fastselector;

import android.app.Activity;
import android.app.FragmentManager;
import android.net.Uri;

import com.yakin.fastselector.create.CreationMode;
import com.yakin.fastselector.crop.CropMode;
import com.yakin.fastselector.select.SelectionMode;

import java.io.File;

class SelectorImpl implements ISelector {

    private String FRAGMENT_TAG = "FastSelector";

    private SelectionFragment fragment;

    public SelectorImpl(Activity activity) {
        fragment = findRTPFragment(activity);
        boolean needNewInstance = fragment == null;
        if (needNewInstance) {
            fragment = new SelectionFragment();
            FragmentManager fragmentMgr = activity.getFragmentManager();
            fragmentMgr.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)
                    .commitAllowingStateLoss();
            fragmentMgr.executePendingTransactions();
        }
    }

    private SelectionFragment findRTPFragment(Activity activity) {
        return (SelectionFragment) activity.getFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }

    @Override
    public SelectionMode openGallery(ChooseType chooseType) {
        return new SelectionMode(fragment)
                .setChooseType(chooseType);
    }

    @Override
    public CropMode openCrop(String filePath) {
        return new CropMode(fragment)
                .setOriginalFilePath(filePath);
    }

    @Override
    public CropMode openCrop(File file) {
        return new CropMode(fragment)
                .setOriginalFile(file);
    }

    @Override
    public CropMode openCrop(Uri fileUri) {
        return new CropMode(fragment)
                .setOriginalUri(fileUri);
    }

    @Override
    public CreationMode openCreate(ChooseType chooseType) {
        return new CreationMode(fragment)
                .setChooseType(chooseType);
    }
}
