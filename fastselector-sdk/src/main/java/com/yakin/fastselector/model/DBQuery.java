package com.yakin.fastselector.model;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.yakin.fastselector.utils.UriUtil;

public class DBQuery {

    private Context context;
    private ContentResolver contentResolver;

    public static DBQuery get(Context context) {
        return new DBQuery(context);
    }

    private DBQuery(Context context) {
        this.context = context;
        this.contentResolver = context.getContentResolver();
    }

    public MediaModel query(Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if(isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (UriUtil.isExternalStorageDocument(uri)) {  // ExternalStorageProvider
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    MediaModel media = new MediaModel();
                    media.setPath(Environment.getExternalStorageDirectory() + "/" + split[1]);
                    return media;
                }
            } else if (UriUtil.isDownloadsDocument(uri)) {  // DownloadsProvider
                String docId = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));

                return query(contentUri, null, null);
            } else if (UriUtil.isMediaDocument(uri)) { // MediaProvider
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = "_id=?";
                String[] selectionArgs = new String[]{ split[1] };

                return query(contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) { // MediaStore (and general)
            return query(uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) { // File
            MediaModel media = new MediaModel();
            media.setPath(uri.getPath());
            return media;
        }
        return null;
    }

    public MediaModel query(Uri uri, String selection, String[] selectionArgs) {
        String[] columns = { MediaStore.MediaColumns.DATA,
                MediaStore.MediaColumns.SIZE,
                MediaStore.MediaColumns.MIME_TYPE,
                MediaStore.MediaColumns.WIDTH,
                MediaStore.MediaColumns.HEIGHT
        };
        Cursor cursor = contentResolver.query(uri, columns, selection, selectionArgs, null);
        cursor.moveToFirst();
        MediaModel media = new MediaModel();
        int pathIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
        if(pathIndex > -1) {
            media.setPath(cursor.getString(pathIndex));
        }
        int sizeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.SIZE);
        if(sizeIndex > -1) {
            media.setSize(cursor.getLong(sizeIndex));
        }
        int mimeTypeIndex = cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE);
        if(mimeTypeIndex > -1) {
            media.setMimeType(cursor.getString(mimeTypeIndex));
        }
        int widthIndex = cursor.getColumnIndex(MediaStore.MediaColumns.WIDTH);
        if(widthIndex > -1) {
            media.setWidth(cursor.getInt(widthIndex));
        }
        int heightIndex = cursor.getColumnIndex(MediaStore.MediaColumns.HEIGHT);
        if(heightIndex > -1) {
            media.setHeight(cursor.getInt(heightIndex));
        }
        cursor.close();
        media.setDuration(getDuration(media.getPath()));
        return media;
    }

    private long getDuration(String path) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            return Long.parseLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        } catch (Exception e) { }
        return 0;
    }
}
