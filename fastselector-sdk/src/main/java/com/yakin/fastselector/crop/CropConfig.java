package com.yakin.fastselector.crop;

import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;

import java.io.File;

public class CropConfig {

    public String originalFilePath;
    public File originalFile;
    public Uri originalUri;

    public String saveDirectory;

    public int aspectX = 1; // 横向比例
    public int aspectY = 1; // 纵向比例
    public int outputWidth = 1000; // 输出的宽
    public int outputHeight = 1000; // 输出的高
    public boolean scale = true; // 是否支持比例缩放

    public CompressFormat outputFormat = CompressFormat.PNG; // 输出格式

    CropConfig() { }
}
