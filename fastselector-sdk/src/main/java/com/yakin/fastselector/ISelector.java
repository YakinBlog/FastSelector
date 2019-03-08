package com.yakin.fastselector;

import android.net.Uri;

import com.yakin.fastselector.crop.CropMode;
import com.yakin.fastselector.select.SelectionMode;

import java.io.File;

public interface ISelector {

    SelectionMode openGallery(ChooseType chooseType);

    CropMode openCrop(String filePath);

    CropMode openCrop(File file);

    CropMode openCrop(Uri fileUri);
}
