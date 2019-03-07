package com.yakin.fastselector.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.yakin.fastselector.ChooseType;
import com.yakin.fastselector.utils.EventUtil;

import java.io.File;

public class CropMode {

    private Activity activity;
    private Fragment fragment;

    private CropConfig config;

    public CropMode(Activity activity, Fragment fragment) {
        this.activity = activity;
        this.fragment = fragment;
        this.config = new CropConfig();
    }

    public CropMode setOriginalFilePath(String originalFilePath) {
        config.originalFilePath = originalFilePath;
        return this;
    }

    public CropMode setTargetFilePath(String targetFilePath) {
        config.targetFilePath = targetFilePath;
        return this;
    }

    public CropMode setOriginalFile(File originalFile) {
        config.originalFile = originalFile;
        return this;
    }

    public CropMode setTargetFile(File targetFile) {
        config.targetFile = targetFile;
        return this;
    }

    public CropMode setOriginalUri(Uri originalUri) {
        config.originalUri = originalUri;
        return this;
    }

    public CropMode setTargetUri(Uri targetUri) {
        config.targetUri = targetUri;
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

    public void forResult(int requestCode) {
        if (!EventUtil.isFastDoubleClick()) {
            if(config.originalFilePath != null) {
                config.originalFile = new File(config.originalFilePath);
            }
            if(config.originalFile != null) {
                // 第二个参数必须要和AndroidManifest.xml里provider的authorities一致
                config.originalUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", config.originalFile);
            }
            if(config.targetFilePath != null) {
                config.targetFile = new File(config.targetFilePath);
            }
            if(config.targetFile != null) {
                config.targetUri = Uri.fromFile(config.targetFile);
            }
            if(config.originalUri == null || config.targetUri == null) {
                throw new NullPointerException("originalUri == null or targetUri == null");
            }
            Intent intent = new Intent("com.android.camera.action.CROP");
            // >=7.x 对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.setDataAndType(config.originalUri, ChooseType.IMAGE.getMimeType());
            intent.putExtra("crop", true);

            intent.putExtra("aspectX", config.aspectX);
            intent.putExtra("aspectY", config.aspectY);
            intent.putExtra("outputX", config.outputWidth);
            intent.putExtra("outputY", config.outputHeight);
            intent.putExtra("scale", config.scale);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, config.targetUri);
            intent.putExtra("outputFormat", config.outputFormat);
            intent.putExtra("noFaceDetection", true);
            intent.putExtra("return-data", false);
            if (fragment != null) {
                fragment.startActivityForResult(intent, requestCode);
            } else {
                activity.startActivityForResult(intent, requestCode);
            }
        }
    }
}
