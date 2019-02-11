package io.storj.mobile.dataprovider.buckets;

import io.storj.mobile.dataprovider.BaseContract;

public final class BucketContract extends BaseContract {
    public final static String TABLE_NAME = "buckets";

    public final static String _NAME = "name";
    public final static String _CREATED = "created";
    public final static String _DECRYPTED = "is_decrypted";
    public final static String _HASH = "hashcode";
    public final static String _STARRED = "is_starred";

    public static String createTable() {
        return String.format("CREATE TABLE IF NOT EXISTS %s (" +
                "%s TEXT PRIMARY KEY NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s TEXT NOT NULL, " +
                "%s INTEGER, " +
                "%s INTEGER, " +
                "%s TEXT NOT NULL)",
                 TABLE_NAME, _ID, _CREATED, _NAME, _DECRYPTED, _STARRED, _HASH);
    }
}
