package com.epgis.base.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MapItem implements Parcelable {

    private String name;        // 名称
    private String label;       // 标签 AndroidManifest设置
    private String description; // 描述 AndroidManifest设置
    private String category;    // 分类

    public MapItem(String name, String label, String description, String category) {
        this.name = name;
        this.label = label;
        this.description = description;
        this.category = category;
    }

    private MapItem(Parcel in) {
        name = in.readString();
        label = in.readString();
        description = in.readString();
        category = in.readString();
    }

    public String getName() {
        return name;
    }

    public String getSimpleName() {
        String[] split = name.split("\\.");
        return split[split.length - 1];
    }

    public String getLabel() {
        return label != null ? label : getSimpleName();
    }

    public String getDescription() {
        return description != null ? description : "-";
    }

    public String getCategory() {
        return category;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(label);
        out.writeString(description);
        out.writeString(category);
    }

    public static final Creator<MapItem> CREATOR = new Creator<MapItem>() {

        public MapItem createFromParcel(Parcel in) {
            return new MapItem(in);
        }

        public MapItem[] newArray(int size) {
            return new MapItem[size];
        }
    };
}
