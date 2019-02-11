package io.storj.mobile.dataprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import io.storj.mobile.common.responses.Response;
import io.storj.mobile.dataprovider.buckets.BucketContract;
import io.storj.mobile.dataprovider.files.FileContract;
import io.storj.mobile.dataprovider.settings.SettingsContract;
import io.storj.mobile.dataprovider.syncqueue.SyncQueueRepository;
import io.storj.mobile.dataprovider.buckets.BucketRepository;
import io.storj.mobile.dataprovider.files.FileRepository;
import io.storj.mobile.dataprovider.settings.SettingsRepository;
import io.storj.mobile.dataprovider.uploading.UploadingFileContract;
import io.storj.mobile.dataprovider.uploading.UploadingFilesRepository;
import io.storj.mobile.domain.files.IFileRepository;
import io.storj.mobile.domain.buckets.IBucketRepository;
import io.storj.mobile.domain.IDatabase;
import io.storj.mobile.domain.settings.ISettingsRepository;
import io.storj.mobile.domain.syncqueue.ISyncQueueRepository;
import io.storj.mobile.domain.uploading.IUploadingFilesRepository;
import io.storj.mobile.storjlibmodule.dataprovider.contracts.SynchronizationQueueContract;

public class Database extends SQLiteOpenHelper implements IDatabase  {
    private IBucketRepository _buckets;
    private IFileRepository _files;
    private ISettingsRepository _settings;
    private ISyncQueueRepository _syncQueue;
    private IUploadingFilesRepository _uploadingFiles;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "storj.new.db";

    public Database(Context context, SQLiteDatabase.CursorFactory factory) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public IBucketRepository buckets() {
        if (_buckets == null) {
            _buckets = new BucketRepository(getWritableDatabase());
        }

        return _buckets;
    }

    @Override
    public IFileRepository files() {
        if (_files == null) {
            _files = new FileRepository(getWritableDatabase());
        }

        return _files;
    }

    @Override
    public IUploadingFilesRepository uploadingFiles() {
        if (_uploadingFiles == null) {
            _uploadingFiles = new UploadingFilesRepository(getWritableDatabase());
        }

        return _uploadingFiles;
    }

    @Override
    public ISyncQueueRepository syncQueueEntries() {
        if (_syncQueue == null) {
            _syncQueue = new SyncQueueRepository(getWritableDatabase());
        }

        return _syncQueue;
    }

    @Override
    public ISettingsRepository settings() {
        if (_settings == null) {
            _settings = new SettingsRepository(getWritableDatabase());
        }

        return _settings;
    }

    @Override
    public Response createTables() {
        buckets().createTable();
        files().createTable();
        settings().createTable();
        syncQueueEntries().createTable();

        return new Response(true, null);
    }

    @Override
    public Response dropTables() {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + BucketContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + FileContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UploadingFileContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SettingsContract.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + SynchronizationQueueContract.TABLE_NAME);

        return null;
    }

    public void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public void commitTransaction() {
        getWritableDatabase().setTransactionSuccessful();
    }

    public void rollbackTransaction() {
        getWritableDatabase().endTransaction();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTables();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropTables();
        createTables();
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }
}
