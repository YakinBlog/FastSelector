package com.yakin.fastselector.utils;

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
}
