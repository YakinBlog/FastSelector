package com.yakin.fastselector.create;

import com.yakin.fastselector.ChooseType;
import com.yakin.fastselector.ISelectionHandler;
import com.yakin.fastselector.SelectionFragment;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.utils.EventUtil;

public class CreationMode {

    private SelectionFragment fragment;
    private CreationConfig config;

    public CreationMode(SelectionFragment fragment) {
        this.fragment = fragment;
        this.config = new CreationConfig();
    }

    public CreationMode setSaveDirectory(String saveDirectory) {
        config.saveDirectory = saveDirectory;
        return this;
    }

    public CreationMode setVideoMaxSecond(int maxSecond) {
        config.videoMaxSecond = maxSecond;
        return this;
    }

    public CreationMode setVideoHighQuality(boolean highQuality) {
        config.videoHighQuality = highQuality;
        return this;
    }

    public CreationMode setChooseType(ChooseType chooseType) {
        config.chooseType = chooseType;
        return this;
    }

    public void forResult(ISelectionHandler<MediaModel> handler) {
        if (!EventUtil.isFastDoubleClick()) {
            fragment.startCreationForResult(handler, config);
        }
    }
}
