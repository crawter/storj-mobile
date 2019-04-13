package mio.storj.mobile.storjlibmodule.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// FilePathModel is used in FilePickerModule
public class FilePathModel {
    @Expose
    @SerializedName("name")
    private String mName;
    @Expose
    @SerializedName("path")
    private String mPath;

    public FilePathModel(String name, String path) {
        mName = name;
        mPath = path;
    }
}
