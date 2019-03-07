package com.yakin.fastselector.crop;

import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;

import java.io.File;

public class CropConfig {

    String originalFilePath;
    String targetFilePath;

    File originalFile;
    File targetFile;

    Uri originalUri;
    Uri targetUri;

    int aspectX = 1; // 横向比例
    int aspectY = 1; // 纵向比例
    int outputWidth = 1000; // 输出的宽
    int outputHeight = 1000; // 输出的高
    boolean scale = true; // 是否支持比例缩放

    CompressFormat outputFormat = CompressFormat.PNG; // 输出格式
}
