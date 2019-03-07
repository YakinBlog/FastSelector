package com.yakin.fastselector;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.yakin.fastselector.crop.CropMode;
import com.yakin.fastselector.model.DBQuery;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.select.SelectionMode;

import java.util.ArrayList;
import java.util.List;

public class Selector {

    public static boolean isPrintLog;

    private Activity activity;
    private Fragment fragment;

    private Selector(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
    }

    public static Selector get(Activity activity) {
        return new Selector(activity, null);
    }

    public static Selector get(Fragment fragment) {
        return new Selector(fragment.getActivity(), null);
    }

    public SelectionMode openGallery(ChooseType chooseType) {
        return new SelectionMode(activity, fragment)
                .setChooseType(chooseType);
    }

    public CropMode openCrop() {
        return new CropMode(activity, fragment);
    }

    public List<MediaModel> queryResultFromIntent(Intent intent) {
        List<MediaModel> list = new ArrayList<>();
        if(intent.getData() != null) {
            list.add(queryResultFromUri(intent.getData()));
        }
        if(intent.getClipData() != null) {
            ClipData clipData = intent.getClipData();
            for (int i = 0; i < clipData.getItemCount(); i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                list.add(queryResultFromUri(uri));
            }
        }
        return list;
    }

    public MediaModel queryResultFromUri(Uri uri) {
        return DBQuery.get(activity).query(uri);
    }
}
