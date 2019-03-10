package com.yakin.fastselector;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.yakin.fastselector.create.CreationConfig;
import com.yakin.fastselector.crop.CropConfig;
import com.yakin.fastselector.model.DBQuery;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.select.SelectionConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

class SelectionInternalOperation {

    private Fragment fragment;
    private String rootDir;

    public SelectionInternalOperation(Fragment fragment) {
        this.fragment = fragment;
        rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public void startSelection(int requestCode, SelectionConfig config) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(config.chooseType.getMimeType());
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, config.multiple);
        fragment.startActivityForResult(intent, requestCode);
    }

    public Uri startCrop(int requestCode, CropConfig config) {
        if(config.saveDirectory == null) {
            config.saveDirectory = rootDir;
        }
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(System.currentTimeMillis());
        if(config.outputFormat == Bitmap.CompressFormat.WEBP) {
            fileBuilder.append(".webp");
        } else if(config.outputFormat == Bitmap.CompressFormat.PNG) {
            fileBuilder.append(".png");
        } else {
            fileBuilder.append(".jpg");
        }
        File file = new File(config.saveDirectory, fileBuilder.toString());
        Uri saveFileUri = Uri.fromFile(file);

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
        intent.putExtra(MediaStore.EXTRA_OUTPUT, saveFileUri);
        intent.putExtra("outputFormat", config.outputFormat);
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        fragment.startActivityForResult(intent, requestCode);
        return saveFileUri;
    }

    private Uri getUriFromFile(File file) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            // 第二个参数必须要和AndroidManifest.xml里provider的authorities一致
            Activity activity = fragment.getActivity();
            return FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);
        }
        return Uri.fromFile(file);
    }

    public Uri startTakeImage(int requestCode, CreationConfig config, ISelectionHandler handler) {
        if(config.saveDirectory == null) {
            config.saveDirectory = rootDir;
        }
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(System.currentTimeMillis());
        fileBuilder.append(".png");

        File file = new File(config.saveDirectory, fileBuilder.toString());
        Uri outputFileUri = getUriFromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Activity activity = fragment.getActivity();
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            // >=7.x 对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            fragment.startActivityForResult(intent, requestCode);
        } else if(handler != null) {
            handler.onSelectionResult(Activity.RESULT_CANCELED, null);
        }
        return Uri.fromFile(file);
    }

    public Uri startTakeVideo(int requestCode, CreationConfig config, ISelectionHandler handler) {
        if(config.saveDirectory == null) {
            config.saveDirectory = rootDir;
        }
        StringBuilder fileBuilder = new StringBuilder();
        fileBuilder.append(System.currentTimeMillis());
        fileBuilder.append(".mp4");

        File file = new File(config.saveDirectory, fileBuilder.toString());
        Uri outputFileUri = getUriFromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        Activity activity = fragment.getActivity();
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            if(config.videoMaxSecond > 0) {
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, config.videoMaxSecond);
            }
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, config.videoHighQuality ? 1 : 0);
            fragment.startActivityForResult(intent, requestCode);
        } else if(handler != null) {
            handler.onSelectionResult(Activity.RESULT_CANCELED, null);
        }
        return Uri.fromFile(file);
    }

    public void startTakeAudio(int requestCode, CreationConfig config, ISelectionHandler handler) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        Activity activity = fragment.getActivity();
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if(handler != null) {
            handler.onSelectionResult(Activity.RESULT_CANCELED, null);
        }
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
        return DBQuery.get(fragment.getActivity()).query(uri);
    }
}
