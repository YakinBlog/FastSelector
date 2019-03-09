package com.yakin.fastselector;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.yakin.fastselector.create.CreationConfig;
import com.yakin.fastselector.crop.CropConfig;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.select.SelectionConfig;

import java.util.List;

public class SelectionFragment extends Fragment {

    private final int REQUEST_SELECT = 101;
    private final int REQUEST_CROP = 102;
    private final int REQUEST_TAKE_IMAGE = 103;
    private final int REQUEST_TAKE_VIDEO = 104;
    private final int REQUEST_TAKE_AUDIO = 105;

    private ISelectionHandler handler;
    private Uri saveFileUri;

    private SelectionInternalOperation operation;

    public SelectionFragment() {
        operation = new SelectionInternalOperation(this);
    }

    public void startSelectionForResult(ISelectionHandler<List<MediaModel>> handler, SelectionConfig config) {
        this.handler = handler;
        operation.startSelection(REQUEST_SELECT, config);
    }

    public void startCropForResult(ISelectionHandler<MediaModel> handler, CropConfig config) {
        this.handler = handler;
        saveFileUri = operation.startCrop(REQUEST_CROP, config);
    }

    public void startCreationForResult(ISelectionHandler<MediaModel> handler, CreationConfig config) {
        this.handler = handler;
        Intent intent = new Intent();
        if(config.chooseType == ChooseType.IMAGE) {
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        } else if(config.chooseType == ChooseType.VIDEO) {
            intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        } else if(config.chooseType == ChooseType.AUDIO) {
            intent.setAction(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        }
        // 检测对应程序是否存在
        if(intent.resolveActivity(getActivity().getPackageManager()) == null) {
            if(handler != null) {
                handler.onSelectionResult(Activity.RESULT_CANCELED, null);
            }
            return;
        }
        if(config.chooseType == ChooseType.IMAGE) {
            saveFileUri = operation.startTakeImage(REQUEST_TAKE_IMAGE, config);
        } else if(config.chooseType == ChooseType.VIDEO) {
            saveFileUri = operation.startTakeVideo(REQUEST_TAKE_VIDEO, config);
        } else if(config.chooseType == ChooseType.AUDIO) {
            operation.startTakeAudio(REQUEST_TAKE_AUDIO, config);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(handler != null) {
            if (resultCode == Activity.RESULT_OK) {
                switch (requestCode) {
                    case REQUEST_SELECT:
                        List<MediaModel> list = operation.queryResultFromIntent(data);
                        handler.onSelectionResult(resultCode, list);
                        break;
                    case REQUEST_CROP:
                    case REQUEST_TAKE_IMAGE:
                    case REQUEST_TAKE_VIDEO:
                        MediaModel media = operation.queryResultFromUri(saveFileUri);
                        handler.onSelectionResult(resultCode, media);
                        break;
                    case REQUEST_TAKE_AUDIO:
                        MediaModel audio = operation.queryResultFromUri(data.getData());
                        handler.onSelectionResult(resultCode, audio);
                        break;
                }
            } else {
                handler.onSelectionResult(resultCode, null);
            }
            saveFileUri = null;
        }
    }
}
