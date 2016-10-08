package com.example.naruto.az_media_tool.Extras;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Naruto on 7/17/2016.
 */

public class Ent_item implements Parcelable {
    private String filepath;
    private String filename;
    private Bitmap BitmapThumbnail;
    private String Duration;
    private int type;
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getBitmapThumbnail() {
        return BitmapThumbnail;
    }

    public void setBitmapThumbnail(Bitmap bitmapThumbnail) {
        BitmapThumbnail = bitmapThumbnail;
    }

    public String getDuration() {
        return Duration;
    }

    public void setDuration(String duration) {
        Duration = duration;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath;
    }

    public Ent_item() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.filepath);
        dest.writeString(this.filename);
        dest.writeParcelable(this.BitmapThumbnail, flags);
        dest.writeString(this.Duration);
        dest.writeInt(this.type);
        dest.writeSerializable(this.file);
    }

    protected Ent_item(Parcel in) {
        this.filepath = in.readString();
        this.filename = in.readString();
        this.BitmapThumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.Duration = in.readString();
        this.type = in.readInt();
        this.file = (File) in.readSerializable();
    }

    public static final Creator<Ent_item> CREATOR = new Creator<Ent_item>() {
        @Override
        public Ent_item createFromParcel(Parcel source) {
            return new Ent_item(source);
        }

        @Override
        public Ent_item[] newArray(int size) {
            return new Ent_item[size];
        }
    };
}
