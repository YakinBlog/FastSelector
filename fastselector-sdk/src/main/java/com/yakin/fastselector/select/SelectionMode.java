package com.yakin.fastselector.select;

import com.yakin.fastselector.ChooseType;
import com.yakin.fastselector.ISelectionHandler;
import com.yakin.fastselector.SelectionFragment;
import com.yakin.fastselector.model.MediaModel;
import com.yakin.fastselector.utils.EventUtil;

import java.util.List;

public class SelectionMode {

    private SelectionFragment fragment;
    private SelectionConfig config;

    public SelectionMode(SelectionFragment fragment) {
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

    public void forResult(ISelectionHandler<List<MediaModel>> handler) {
        if (!EventUtil.isFastDoubleClick()) {
            fragment.startSelectionForResult(handler, config);
        }
    }
}
