package com.yakin.fastselector.crop;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.content.FileProvider;

import com.yakin.fastselector.ISelectionHandler;
import com.yakin.fastselector.SelectionFragment;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.utils.EventUtil;

import java.io.File;

public class CropMode {

    private SelectionFragment fragment;
    private CropConfig config;

    public CropMode(SelectionFragment fragment) {
        this.fragment = fragment;
        this.config = new CropConfig();
    }

    public CropMode setOriginalFilePath(String originalFilePath) {
        config.originalFilePath = originalFilePath;
        return this;
    }

    public CropMode setSaveDirectory(String saveDirectory) {
        config.saveDirectory = saveDirectory;
        return this;
    }

    public CropMode setOriginalFile(File originalFile) {
        config.originalFile = originalFile;
        return this;
    }

    public CropMode setOriginalUri(Uri originalUri) {
        config.originalUri = originalUri;
        return this;
    }

    public CropMode setAspectX(int aspectX) {
        config.aspectX = aspectX;
        return this;
    }

    public CropMode setAspectY(int aspectY) {
        config.aspectY = aspectY;
        return this;
    }

    public CropMode setOutputWidth(int outputWidth) {
        config.outputWidth = outputWidth;
        return this;
    }

    public CropMode setOutputHeight(int outputHeight) {
        config.outputHeight = outputHeight;
        return this;
    }

    public CropMode setOutputFormat(Bitmap.CompressFormat outputFormat) {
        config.outputFormat = outputFormat;
        return this;
    }

    public CropMode setScale(boolean scale) {
        config.scale = scale;
        return this;
    }

    public void forResult(ISelectionHandler<MediaModel> handler) {
        if (!EventUtil.isFastDoubleClick()) {
            if(config.originalFilePath != null) {
                config.originalFile = new File(config.originalFilePath);
            }
            if(config.originalFile != null) {
                // 第二个参数必须要和AndroidManifest.xml里provider的authorities一致
                Activity activity = fragment.getActivity();
                config.originalUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", config.originalFile);
            }
            if(config.originalUri == null) {
                throw new NullPointerException("original == null");
            }
            fragment.startCropForResult(handler, config);
        }
    }
}
