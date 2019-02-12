package io.storj.mobile.domain.settings;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

// Settings is a domain model that describes settings entity
public class Settings {
    @Expose
    @SerializedName("id")
    private String _id;
    @Expose
    @SerializedName("isFirstSignIn")
    private boolean _isFirstSignIn;
    @Expose
    @SerializedName("syncStatus")
    private boolean _syncStatus;
    @Expose
    @SerializedName("syncSettings")
    private int _syncSettings;
    @Expose
    @SerializedName("lastSync")
    private String _lastSync;

    public Settings(String id) {
        _id = id;
    }

    // Settings - is a constructor for settings entity
    public Settings(String id, boolean isFirstSignIn, boolean syncStatus, int syncSettings, String lastSync) {
        _id = id;
        _isFirstSignIn = isFirstSignIn;
        _syncStatus = syncStatus;
        _syncSettings = syncSettings;
        _lastSync = lastSync;
    }

    public String getId() {
        return _id;
    }
    public boolean isFirstSignIn() {
        return _isFirstSignIn;
    }
    public boolean isSyncStatus() {
        return _syncStatus;
    }
    public int getSyncSettings() {
        return _syncSettings;
    }
    public String getLastSync() {
        return _lastSync;
    }

    public void setSyncSettings(int syncSettings) {
        _syncSettings = syncSettings;
    }
    public void setFirstSignIn(boolean isFirstSignIn) {
        _isFirstSignIn = isFirstSignIn;
    }
}
