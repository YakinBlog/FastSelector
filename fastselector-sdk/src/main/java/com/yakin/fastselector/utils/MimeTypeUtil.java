package com.yakin.fastselector.utils;

import android.webkit.MimeTypeMap;

public class MimeTypeUtil {

    public static boolean isImage(String mimeType) {
        return mimeType.toLowerCase().startsWith("image");
    }

    public static boolean isVideo(String mimeType) {
        return mimeType.toLowerCase().startsWith("video");
    }

    public static boolean isAudio(String mimeType) {
        return mimeType.toLowerCase().startsWith("audio");
    }

    public static String getMimeType(String filePath) {
        String ext = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }
}
