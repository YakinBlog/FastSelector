package com.yakin.fastselector;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.yakin.fastselector.crop.CropMode;
import com.yakin.fastselector.select.SelectionMode;

import java.io.File;

public class Selector implements ISelector {

    public static boolean isPrintLog;

    private ISelector selector;

    private Selector(Activity activity) {
        this.selector = new SelectorImpl(activity);
    }

    public static ISelector get(Activity activity) {
        return new Selector(activity);
    }

    public static ISelector get(Fragment fragment) {
        return new Selector(fragment.getActivity());
    }

    @Override
    public SelectionMode openGallery(ChooseType chooseType) {
        return selector.openGallery(chooseType);
    }

    @Override
    public CropMode openCrop(String filePath) {
        return selector.openCrop(filePath);
    }

    @Override
    public CropMode openCrop(File file) {
        return selector.openCrop(file);
    }

    @Override
    public CropMode openCrop(Uri fileUri) {
        return selector.openCrop(fileUri);
    }
}
