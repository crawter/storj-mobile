package mio.storj.mobile.dataprovider.files;

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
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.domain.files.IFileRepository;
import mio.storj.mobile.common.responses.ListResponse;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.BaseRepository;
import mio.storj.mobile.domain.files.File;
import mio.storj.mobile.domain.files.IFileRepository;

public class FileRepository extends BaseRepository implements IFileRepository {
    private String[] _columns = new String[] {
            FileContract._ID,
            FileContract._NAME,
            FileContract._MIMETYPE,
            FileContract._INDEX,
            FileContract._HMAC,
            FileContract._ERASURE,
            FileContract._CREATED,
            FileContract._DECRYPTED,
            FileContract._STARRED,
            FileContract._SIZE,
            FileContract._SYNCED,
            FileContract._DOWNLOAD_STATE,
            FileContract._FILE_HANDLE,
            FileContract._FILE_URI,
            FileContract.FILE_FK,
            FileContract._FILE_THUMBNAIL
    };

    public FileRepository(SQLiteDatabase db) {
        super(db);
    }

    @Override
    public ListResponse<File> getAll() {
        Cursor cursor = mDb.query(FileContract.TABLE_NAME, null, null, null, null, null, null, null);

        List<File> result = filesFromCursor(cursor);

        cursor.close();

        return new ListResponse<File>(result, true, null);
    }

    @Override
    public ListResponse<File> getAll(String orderByColumn, boolean isDesc) {
        String column = orderByColumn;

        if(orderByColumn == null || orderByColumn.isEmpty()) {
            column = FileContract._NAME;
        }

        String orderBy = isDesc ? column + " DESC" : orderByColumn + " ASC";

        Cursor cursor = mDb.query(FileContract.TABLE_NAME, null, null, null, null, null, orderBy, null);

        List<File> result = filesFromCursor(cursor);

        cursor.close();

        return new ListResponse<File>(result, true, null);
    }

    @Override
    public ListResponse<File> getAll(String bucketId) {
        Cursor cursor = mDb.query(FileContract.TABLE_NAME,
                null,
                FileContract.FILE_FK + " = ?",
                new String[] { bucketId },
                null,
                null,
                null,
                null);

        List<File> result = filesFromCursor(cursor);

        cursor.close();

        return new ListResponse<File>(result, true, null);
    }

    @Override
    public ListResponse<File> getAll(String bucketId, String orderByColumn, boolean isDesc) {
        String column = orderByColumn;

        if(orderByColumn == null || orderByColumn.isEmpty()) {
            column = FileContract._NAME;
        }

        String orderBy = isDesc ? " ORDER BY " + column + " COLLATE NOCASE DESC;" : " ORDER BY " + column + " COLLATE NOCASE ASC;";

        String query = "SELECT * FROM " + FileContract.TABLE_NAME + " WHERE " + FileContract.FILE_FK + " = ?" + orderBy;

        Cursor cursor = mDb.rawQuery(query, new String[] { bucketId  });

        List<File> result = filesFromCursor(cursor);

        cursor.close();

        return new ListResponse<File>(result, true, null);
    }

    @Override
    public SingleResponse<File> get(String fileId) {
        String orderBy = FileContract._CREATED + " DESC";

        Cursor cursor = mDb.query(
                FileContract.TABLE_NAME,
                null,
                FileContract._ID + " = ?",
                new String[] { fileId },
                null, null, orderBy, null);

        File model = fileFromCursor(cursor);

        cursor.close();

        return new SingleResponse<File>(model, model != null, null);
    }

    @Override
    public SingleResponse<File> get(String param, String selection, String bucketId) {
        String orderBy = FileContract._CREATED + " DESC";

        Cursor cursor = mDb.query(
                FileContract.TABLE_NAME,
                null,
                selection + " = ? and " + FileContract.FILE_FK + " = ?",
                new String[] { param, bucketId },
                null, null, orderBy, null);

        File model = fileFromCursor(cursor);

        cursor.close();

        return new SingleResponse<File>(model, model != null, null);
    }

    @Override
    public Response insert(File model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(FileContract._ID, model.fileId);
        map.put(FileContract._CREATED, model.created);
        map.put(FileContract._DECRYPTED, model.isDecrypted);
        map.put(FileContract._ERASURE, model.erasure);
        map.put(FileContract._HMAC, model.hmac);
        map.put(FileContract._INDEX, model.index);
        map.put(FileContract._MIMETYPE, model.mimeType);
        map.put(FileContract._STARRED, model.isStarred);
        map.put(FileContract._SYNCED, model.isSynced);
        map.put(FileContract._DOWNLOAD_STATE, model.downloadState);
        map.put(FileContract._FILE_HANDLE, model.fileHandle);
        map.put(FileContract._FILE_URI, model.fileUri);
        map.put(FileContract._SIZE, model.size);
        map.put(FileContract.FILE_FK, model.bucketId);
        map.put(FileContract._NAME, model.name);
        map.put(FileContract._FILE_THUMBNAIL, model.thumbnail);

        return _executeInsert(FileContract.TABLE_NAME, map);
    }

    @Override
    public Response delete(String fileId) {
        if(fileId == null || fileId.isEmpty())
            return new Response(false, "Model id is not valid!");

        return _executeDelete(new String[] { fileId }, FileContract.TABLE_NAME, FileContract._DEFAULT_WHERE_CLAUSE);
    }

    @Override
    public Response deleteAll(String bucketId) {
        return _deleteAll(FileContract.TABLE_NAME, "bucketId = ?", new String[]{bucketId});
    }

    @Override
    public Response update(File model) {
        if(model == null) {
            return new Response(false, "Model is not valid!");
        }

        ContentValues map = new ContentValues();

        map.put(FileContract._CREATED, model.created);
        map.put(FileContract._DECRYPTED, model.isDecrypted);
        map.put(FileContract._ERASURE, model.erasure);
        map.put(FileContract._HMAC, model.hmac);
        map.put(FileContract._INDEX, model.index);
        map.put(FileContract._MIMETYPE, model.mimeType);
        map.put(FileContract._SIZE, model.size);
        map.put(FileContract.FILE_FK, model.bucketId);
        map.put(FileContract._NAME, model.name);
        map.put(FileContract._STARRED, model.isStarred);
        map.put(FileContract._DOWNLOAD_STATE, model.downloadState);
        map.put(FileContract._FILE_HANDLE, model.fileHandle);
        map.put(FileContract._FILE_URI, model.fileUri);
        map.put(FileContract._FILE_THUMBNAIL, model.thumbnail);

        return _executeUpdate(FileContract.TABLE_NAME, model.fileId, null,null, map);
    }

    @Override
    public Response createTable() {
        try {
            mDb.execSQL(FileContract.createTable());

            return new Response(true, null);
        } catch (SQLException ex) {
            return new Response(true, "FileRepository createTable() " + ex.getMessage());
        }
    }

    private List<File> filesFromCursor(Cursor cursor) {
        List<File> result = new ArrayList();

        if (cursor.moveToFirst()){
            do {
                result.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return result;
    }

    private File fileFromCursor(Cursor cursor) {
        File model = null;

        if (cursor.moveToFirst()) {
            model = readFromCursor(cursor);
        }

        return model;
    }

    private File readFromCursor(Cursor cursor) {
        File result = new File();

        for(int i = 0; i < _columns.length; i++) {
            switch(_columns[i]) {
                case FileContract._CREATED :
                    result.created = cursor.getString(i);
                    break;
                case FileContract._NAME :
                    result.name = cursor.getString(i);
                    break;
                case FileContract._ID :
                    result.fileId = cursor.getString(i);
                    break;
                case FileContract._ERASURE:
                    result.erasure = cursor.getString(i);
                    break;
                case FileContract._HMAC:
                    result.hmac = cursor.getString(i);
                    break;
                case FileContract._INDEX:
                    result.index = cursor.getString(i);
                    break;
                case FileContract._MIMETYPE:
                    result.mimeType = cursor.getString(i);
                    break;
                case FileContract.FILE_FK:
                    result.bucketId = cursor.getString(i);
                    break;
                case FileContract._FILE_URI:
                    result.fileUri = cursor.getString(i);
                    break;
                case FileContract._FILE_THUMBNAIL:
                    result.thumbnail = cursor.getString(i);
                    break;
                case FileContract._DECRYPTED :
                    result.isDecrypted = cursor.getInt(i) == 1;
                case FileContract._STARRED :
                    result.isStarred = cursor.getInt(i) == 1;
                case FileContract._SYNCED :
                    result.isSynced = cursor.getInt(i) == 1;
                    break;
                case FileContract._DOWNLOAD_STATE:
                    result.downloadState = cursor.getInt(i);
                    break;
                case FileContract._SIZE :
                    result.size = cursor.getLong(i);
                case FileContract._FILE_HANDLE:
                    result.fileHandle = cursor.getLong(i);
                    break;
            }
        }

        return result;
    }
}
