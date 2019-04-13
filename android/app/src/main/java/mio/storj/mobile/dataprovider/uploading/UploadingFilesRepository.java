package mio.storj.mobile.dataprovider.uploading;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.BaseRepository;
import mio.storj.mobile.domain.uploading.IUploadingFilesRepository;
import mio.storj.mobile.domain.uploading.UploadingFile;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.BaseRepository;
import mio.storj.mobile.domain.uploading.IUploadingFilesRepository;
import mio.storj.mobile.domain.uploading.UploadingFile;

public class UploadingFilesRepository extends BaseRepository implements IUploadingFilesRepository {
    private String[] mColumns = {
        UploadingFileContract._ID,
        UploadingFileContract._NAME,
        UploadingFileContract._URI,
        UploadingFileContract._PROGRESS,
        UploadingFileContract._SIZE,
        UploadingFileContract._UPLOADED,
        UploadingFileContract._BUCKET_ID
    };

    public UploadingFilesRepository(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public ListResponse<UploadingFile> getAll() {
        Cursor cursor = mDb.query(UploadingFileContract.TABLE_NAME, null, null, null, null, null, null, null);

        List<UploadingFile> result = uploadingFilesFromCursor(cursor);

        cursor.close();

        return new ListResponse<UploadingFile>(result, true, null);
    }

    @Override
    public SingleResponse<UploadingFile> get(String id) {
        Cursor cursor = mDb.query(UploadingFileContract.TABLE_NAME,
                null,
                UploadingFileContract._ID + " = ?",
                new String[] { id },
                null, null, null);

        UploadingFile uf = uploadingFileFromCursor(cursor);

        cursor.close();

        return new SingleResponse<UploadingFile>(uf, uf != null, null);
    }

    @Override
    public Response insert(UploadingFile model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(UploadingFileContract._ID, model.getFileHandle());
        map.put(UploadingFileContract._PROGRESS, model.getProgress());
        map.put(UploadingFileContract._SIZE, model.getSize());
        map.put(UploadingFileContract._UPLOADED, model.getUploaded());
        map.put(UploadingFileContract._NAME, model.getName());
        map.put(UploadingFileContract._URI, model.getUri());
        map.put(UploadingFileContract._BUCKET_ID, model.getBucketId());

        return _executeInsert(UploadingFileContract.TABLE_NAME, map);
    }

    @Override
    public Response update(UploadingFile model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(UploadingFileContract._PROGRESS, model.getProgress());
        map.put(UploadingFileContract._SIZE, model.getSize());
        map.put(UploadingFileContract._UPLOADED, model.getUploaded());

        return _executeUpdate(UploadingFileContract.TABLE_NAME, String.valueOf(model.getFileHandle()), null,null, map);
    }

    @Override
    public Response delete(long fileHandle) {
        if(fileHandle == 0) {
            return new Response(false, "id is not valid!");
        }

        return _executeDelete(
                new String[] { String.valueOf(fileHandle) },
                UploadingFileContract.TABLE_NAME,
                UploadingFileContract._DEFAULT_WHERE_CLAUSE);
    }

    @Override
    public Response deleteAll() {
        return _deleteAll(UploadingFileContract.TABLE_NAME);
    }

    @Override
    public Response createTable() {
        try {
            mDb.execSQL(UploadingFileContract.createTable());
            return new Response(true, null);
        } catch (SQLException ex) {
            return new Response(true, "UploadingFilesRepository createTable() " + ex.getMessage());
        }
    }

    private List<UploadingFile> uploadingFilesFromCursor(Cursor cursor) {
        List<UploadingFile> result = new ArrayList();

        if (cursor.moveToFirst()){
            do {
                result.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return result;
    }

    private UploadingFile uploadingFileFromCursor(Cursor cursor) {
        UploadingFile model = null;

        if (cursor.moveToFirst()){
            model = readFromCursor(cursor);
        }

        return model;
    }

    private UploadingFile readFromCursor(Cursor cursor) {
        String name = "", uri = "", bucketId = "";
        long fileHandle = 0, size = 0, uploaded = 0;
        double progress = 0;

        for(int i = 0; i < mColumns.length; i++) {
            switch (mColumns[i]) {
                case UploadingFileContract._NAME:
                    name = cursor.getString(i);
                    break;
                case UploadingFileContract._URI:
                    uri = cursor.getString(i);
                    break;
                case UploadingFileContract._BUCKET_ID:
                    bucketId = cursor.getString(i);
                    break;
                case UploadingFileContract._SIZE:
                    size = cursor.getLong(i);
                    break;
                case UploadingFileContract._ID:
                    fileHandle = cursor.getLong(i);
                    break;
                case UploadingFileContract._UPLOADED:
                    uploaded = cursor.getLong(i);
                    break;
                case UploadingFileContract._PROGRESS:
                    progress = cursor.getDouble(i);
                    break;
            }
        }

        return new UploadingFile(fileHandle, progress, size, uploaded, name, uri, bucketId);
    }
}
