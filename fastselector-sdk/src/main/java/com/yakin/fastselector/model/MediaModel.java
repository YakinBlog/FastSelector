package com.yakin.fastselector.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaModel implements Parcelable {

    private String path;
    private String mimeType;
    private int width;
    private int height;
    private long size;
    private long duration;

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.mimeType);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeLong(this.size);
    }

    public static final Parcelable.Creator<MediaModel> CREATOR = new Parcelable.Creator<MediaModel>() {
        @Override
        public MediaModel createFromParcel(Parcel source) {
            MediaModel media = new MediaModel();
            media.path = source.readString();
            media.mimeType = source.readString();
            media.width = source.readInt();
            media.height = source.readInt();
            media.size = source.readLong();
            return media;
        }

        @Override
        public MediaModel[] newArray(int size) {
            return new MediaModel[size];
        }
    };
}
