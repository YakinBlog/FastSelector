package com.yakin.fastselector;

import android.app.Activity;
import android.app.Fragment;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.yakin.fastselector.crop.CropConfig;
import com.yakin.fastselector.model.DBQuery;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.select.SelectionConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SelectionFragment extends Fragment {

    private final int REQUEST_SELECT = 101;
    private final int REQUEST_CROP = 102;

    private ISelectionHandler handler;
    private String rootDir;
    private Uri saveFileUri;

    public SelectionFragment() {
        rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public void startSelectionForResult(ISelectionHandler<List<MediaModel>> handler, SelectionConfig config) {
        this.handler = handler;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(config.chooseType.getMimeType());
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, config.multiple);
        startActivityForResult(intent, REQUEST_SELECT);
    }

    public void startCropForResult(ISelectionHandler<MediaModel> handler, CropConfig config) {
        this.handler = handler;
        if(config.targetFilePath != null) {
            config.targetFile = new File(config.targetFilePath);
        }
        if(config.targetFile != null) {
            config.targetUri = Uri.fromFile(config.targetFile);
        }
        if(config.targetUri == null) {
            StringBuilder fileBuilder = new StringBuilder();
            fileBuilder.append(System.currentTimeMillis());
            if(config.outputFormat == Bitmap.CompressFormat.WEBP) {
                fileBuilder.append(".webp");
            } else if(config.outputFormat == Bitmap.CompressFormat.PNG) {
                fileBuilder.append(".png");
            } else {
                fileBuilder.append(".jpg");
            }
            config.targetFile = new File(rootDir, fileBuilder.toString());
            config.targetFilePath = config.targetFile.getAbsolutePath();
            config.targetUri = Uri.fromFile(config.targetFile);
        }
        saveFileUri = config.targetUri;
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
        startActivityForResult(intent, REQUEST_CROP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(handler != null) {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_SELECT:
                        List<MediaModel> list = queryResultFromIntent(data);
                        handler.onSelectionResult(resultCode, list);
                        break;
                    case REQUEST_CROP:
                        MediaModel media = queryResultFromUri(saveFileUri);
                        handler.onSelectionResult(resultCode, media);
                        break;
                }
            } else {
                handler.onSelectionResult(resultCode, null);
            }
        }
    }

    private List<MediaModel> queryResultFromIntent(Intent intent) {
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

    private MediaModel queryResultFromUri(Uri uri) {
        return DBQuery.get(getActivity()).query(uri);
    }
}
