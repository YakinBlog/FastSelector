package com.yakin.fastselector;

public enum ChooseType {

    ALL("image/*;video/*;audio/*"),
    IMAGE("image/*"),
    VIDEO("video/*"),
    AUDIO("audio/*");

    private final String mimeType;

    ChooseType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }
}
