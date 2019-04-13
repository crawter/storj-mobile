package io.storj.mobile.dataprovider.buckets;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import io.storj.mobile.common.responses.ListResponse;
import io.storj.mobile.common.responses.Response;
import io.storj.mobile.common.responses.SingleResponse;
import io.storj.mobile.dataprovider.BaseRepository;
import io.storj.mobile.domain.buckets.Bucket;
import io.storj.mobile.domain.buckets.IBucketRepository;

public class BucketRepository extends BaseRepository implements IBucketRepository {
    private final String[] mColumns;

    public BucketRepository(SQLiteDatabase db) {
        super(db);

        mColumns = new String[] {
            BucketContract._ID,
            BucketContract._CREATED,
            BucketContract._NAME,
            BucketContract._DECRYPTED,
            BucketContract._STARRED,
            BucketContract._HASH,
        };
    }

    @Override
    public ListResponse<Bucket> getAll() {
        Cursor cursor = mDb.query(
                BucketContract.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null,
                null);

        List<Bucket> buckets = bucketsFromCursor(cursor);

        cursor.close();

        return new ListResponse<Bucket>(buckets, true, null);
    }

    @Override
    public ListResponse<Bucket> getAll(String orderByColumn, boolean isDesc) {

        String column = orderByColumn;

        if(orderByColumn == null || orderByColumn.isEmpty()) {
            column = BucketContract._NAME;
        }

        String orderBy = isDesc ? " ORDER BY " + column + " COLLATE NOCASE DESC;" : " ORDER BY " + column + " COLLATE NOCASE ASC;";

        String query = "SELECT * FROM " + BucketContract.TABLE_NAME + orderBy;

        Cursor cursor = mDb.rawQuery(query, null);

        List<Bucket> buckets = bucketsFromCursor(cursor);

        cursor.close();

        return new ListResponse<Bucket>(buckets, true, null);
    }

    @Override
    public SingleResponse<Bucket> get(String bucketId) {

        String[] selectionArgs = {
                bucketId
        };
        String orderBy = BucketContract._CREATED + " DESC";

        Cursor cursor = mDb.query(
                BucketContract.TABLE_NAME,
                mColumns,
                BucketContract._DEFAULT_WHERE_CLAUSE,
                selectionArgs,
                null, null, orderBy, null);

        Bucket model = bucketFromCursor(cursor);

        cursor.close();

        return new SingleResponse<Bucket>(model, model != null, null);
    }

    // TODO: use enum BucketColumns, check on validity
    @Override
    public SingleResponse<Bucket> get(String columnName, String columnValue) {
        String[] selectionArgs = {
                columnValue
        };

        Cursor cursor = mDb.query(
                BucketContract.TABLE_NAME,
                null,
                columnName + " = ?",
                selectionArgs,
                null, null, null, null);

        Bucket model = bucketFromCursor(cursor);

        cursor.close();

        return new SingleResponse<Bucket>(model, model != null, null);
    }

    @Override
    public Response insert(Bucket model) {
        if(model == null)
            return new Response(false, "Model is not valid!");

        ContentValues map = new ContentValues();

        map.put(BucketContract._ID, model.id);
        map.put(BucketContract._CREATED, model.created);
        map.put(BucketContract._NAME, model.name);
        map.put(BucketContract._HASH, model.hash);
        map.put(BucketContract._DECRYPTED, model.isDecrypted ? 1 : 0);
        map.put(BucketContract._STARRED, model.isStarred ? 1 : 0);

        return _executeInsert(BucketContract.TABLE_NAME, map);
    }

    @Override
    public Response delete(String bucketId) {
        if(bucketId == null || bucketId.isEmpty())
            return new Response(false, "Model id is not valid!");

        return _executeDelete(new String[] { bucketId }, BucketContract.TABLE_NAME, BucketContract._DEFAULT_WHERE_CLAUSE);
    }

    @Override
    public Response deleteAll() {
        return _deleteAll(BucketContract.TABLE_NAME);
    }

    @Override
    public Response update(Bucket model) {
        if(model == null)
            return new Response(false, "Model is not valid!");

    ContentValues map = new ContentValues();

        map.put(BucketContract._CREATED, model.created);
        map.put(BucketContract._NAME, model.name);
        map.put(BucketContract._HASH, model.hash);
        map.put(BucketContract._DECRYPTED, model.isDecrypted);
        map.put(BucketContract._DECRYPTED, model.isStarred);

        return _executeUpdate(BucketContract.TABLE_NAME, model.id, null,null, map);
    }

    @Override
    public Response createTable() {
        try {
            mDb.execSQL(BucketContract.createTable());

            return new Response(true, null);
        } catch (SQLException ex) {
            return new Response(true, "BucketRepository createTable() " + ex.getMessage());
        }
    }

    private List<Bucket> bucketsFromCursor(Cursor cursor) {
        List<Bucket> result = new ArrayList<Bucket>();

        if (cursor.moveToFirst()){
            do {
                result.add(readFromCursor(cursor));
            } while (cursor.moveToNext());
        }

        return result;
    }

    private Bucket bucketFromCursor(Cursor cursor) {
        Bucket model = null;

        if (cursor.moveToFirst()) {
            model = readFromCursor(cursor);
        }

        return model;
    }

    private Bucket readFromCursor(Cursor cursor) {
        Bucket result = new Bucket();

        for(int i = 0; i < mColumns.length; i++) {
            switch (mColumns[i]) {
                case BucketContract._CREATED:
                    result.created = cursor.getString(i);
                    break;
                case BucketContract._NAME:
                    result.name = cursor.getString(i);
                    break;
                case BucketContract._ID:
                    result.id = cursor.getString(i);
                    break;
                case BucketContract._DECRYPTED:
                    result.isDecrypted = cursor.getInt(i) == 1;
                    break;
                case BucketContract._STARRED:
                    result.isStarred = cursor.getInt(i) == 1;
                    break;
                case BucketContract._HASH:
                    result.hash = cursor.getLong(i);
                    break;
            }
        }

        return result;
    }
}
