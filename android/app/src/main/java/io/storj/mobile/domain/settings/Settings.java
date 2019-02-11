package io.storj.mobile.domain.settings;

// Settings is a domain model that describes settings entity
public class Settings {
    private String _id;
    private boolean _isFirstSignIn;
    private boolean _syncStatus;
    private int _syncSettings;
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
