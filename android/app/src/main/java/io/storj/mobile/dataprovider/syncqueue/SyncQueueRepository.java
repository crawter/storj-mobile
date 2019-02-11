package io.storj.mobile.dataprovider.syncqueue;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.dataprovider.BaseRepository;
import io.storj.mobile.domain.syncqueue.ISyncQueueRepository;
import io.storj.mobile.domain.syncqueue.SyncQueueEntry;
import io.storj.mobile.domain.syncqueue.SyncStateEnum;

public class SyncQueueRepository extends BaseRepository implements ISyncQueueRepository {
    private String[] mColumns;

    public SyncQueueRepository(SQLiteDatabase db) {
        super(db);
        mColumns = new String[] {
            SyncQueueContract._ID,
            SyncQueueContract._FILE_NAME,
            SyncQueueContract._LOCAL_PATH,
            SyncQueueContract._STATUS,
            SyncQueueContract._ERROR_CODE,
            SyncQueueContract._SIZE,
            SyncQueueContract._COUNT,
            SyncQueueContract._CREATION_DATE,
            SyncQueueContract._BUCKET_ID,
            SyncQueueContract._FILE_HANDLE
        };
    }

    @Override
    public Response insert(SyncQueueEntry model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(SyncQueueContract._FILE_NAME, model.getFileName());
        map.put(SyncQueueContract._LOCAL_PATH, model.getLocalPath());
        map.put(SyncQueueContract._BUCKET_ID, model.getBucketId());

        return _executeInsert(SyncQueueContract.TABLE_NAME, map);
    }

    @Override
    public Response update(SyncQueueEntry model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(SyncQueueContract._FILE_NAME, model.getFileName());
        map.put(SyncQueueContract._LOCAL_PATH, model.getLocalPath());
        map.put(SyncQueueContract._STATUS, model.getStatus());
        map.put(SyncQueueContract._ERROR_CODE, model.getErrorCode());
        map.put(SyncQueueContract._SIZE, model.getSize());
        map.put(SyncQueueContract._COUNT, model.getCount());
        map.put(SyncQueueContract._BUCKET_ID, model.getBucketId());

        if(model.getFileHandle() != 0) {
            map.put(SyncQueueContract._FILE_HANDLE, model.getFileHandle());
        }

        return _executeUpdate(SyncQueueContract.TABLE_NAME, String.valueOf(model.getId()), null,null, map);
    }

    @Override
    public ListResponse<SyncQueueEntry> getAll() {
        List<SyncQueueEntry> result = new ArrayList();

        Cursor cursor = mDb.query(SyncQueueContract.TABLE_NAME, null, null, null, null, null, null, null);

        for(SyncQueueEntry dbo : getEntriesFromCursor(cursor)) {
            result.add(dbo);
        }

        cursor.close();

        return new ListResponse<SyncQueueEntry>(result, true, null);
    }

    @Override
    public SyncQueueEntry get(int id) {
        Cursor cursor = mDb.query(SyncQueueContract.TABLE_NAME,
                null,
                SyncQueueContract._ID + " = ?",
                new String[] { String.valueOf(id) },
                null, null, null);

        SyncQueueEntry sqe = getEntryFromCursor(cursor);

        cursor.close();

        return sqe;
    }

    @Override
    public SyncQueueEntry get(String localPath, String bucketId) {
        Cursor cursor = mDb.query(SyncQueueContract.TABLE_NAME,
                null,
                SyncQueueContract._LOCAL_PATH + " = ? AND " + SyncQueueContract._BUCKET_ID + " = ?",
                new String[] { localPath, bucketId },
                null, null, null);

        SyncQueueEntry sqe = getEntryFromCursor(cursor);
        cursor.close();

        return sqe;
    }

    @Override
    public int getActiveCount() {
        Cursor cursor = mDb.query(SyncQueueContract.TABLE_NAME,
                null,
                SyncQueueContract._STATUS + " = ? OR " + SyncQueueContract._STATUS + " = ?",
                new String[] {
                    String.valueOf(SyncStateEnum.QUEUED.getValue()),
                    String.valueOf(SyncStateEnum.PROCESSING.getValue())
                },
                null, null, null);

        return cursor.getCount();
    }

    @Override
    public Response createTable() {
        try {
            mDb.execSQL(SyncQueueContract.createTable());
            return new Response(true, null);
        } catch (SQLException ex) {
            return new Response(true, "SyncQueueRepository createTable() " + ex.getMessage());
        }
    }

    private List<SyncQueueEntry> getEntriesFromCursor(Cursor cursor) {
        List<SyncQueueEntry> result = new ArrayList();

        if (cursor.moveToFirst()){
            do {
                result.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return result;
    }

    private SyncQueueEntry getEntryFromCursor(Cursor cursor) {
        SyncQueueEntry dbo = null;

        if (cursor.moveToFirst()){
            dbo = readFromCursor(cursor);
        }

        return dbo;
    }

    private SyncQueueEntry readFromCursor(Cursor cursor) {
        int id = 0, status = 0, errCode = 0, count = 0;
        long size = 0, fHandle = 0;
        String name = "", buckId = "", path = "", created = "";

        for(int i = 0; i < mColumns.length; i++) {
            switch (mColumns[i]) {
                case SyncQueueContract._ID:
                    id = cursor.getInt(i);
                    break;
                case SyncQueueContract._STATUS:
                    status = cursor.getInt(i);
                    break;
                case SyncQueueContract._ERROR_CODE:
                    errCode = cursor.getInt(i);
                    break;
                case SyncQueueContract._COUNT:
                    count = cursor.getInt(i);
                    break;
                case SyncQueueContract._SIZE:
                    size = cursor.getLong(i);
                    break;
                case SyncQueueContract._FILE_HANDLE:
                    fHandle = cursor.getLong(i);
                    break;
                case SyncQueueContract._FILE_NAME:
                    name = cursor.getString(i);
                    break;
                case SyncQueueContract._BUCKET_ID:
                    buckId = cursor.getString(i);
                    break;
                case SyncQueueContract._LOCAL_PATH:
                    path = cursor.getString(i);
                    break;
                case SyncQueueContract._CREATION_DATE:
                    created = cursor.getString(i);
                    break;
            }
        }

        return new SyncQueueEntry(id, name, path, status, errCode, size, count, created, buckId, fHandle);
    }
}
