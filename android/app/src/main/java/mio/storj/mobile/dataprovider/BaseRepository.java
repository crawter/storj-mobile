package mio.storj.mobile.dataprovider;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import mio.storj.mobile.common.responses.Response;


public abstract class BaseRepository {
    protected SQLiteDatabase mDb;

    public BaseRepository(SQLiteDatabase db) {
        mDb = db;
    }

    protected Response _executeInsert(String tableName, ContentValues valuesMap)  {

        try {
            mDb.insertOrThrow(tableName, null, valuesMap);
        } catch(SQLException error) {
            return new Response(false, error.getMessage());
        }

        return new Response(true, null);
    }

    protected Response _executeDelete(String[] ids, String tableName, String whereClause) {
        boolean isSuccess;

        try {
            isSuccess = mDb.delete(tableName, whereClause, ids) > 0;
        } catch(SQLException error) {
            return new Response(false, error.getMessage());
        }

        return new Response(isSuccess, null);
    }

    protected Response _deleteAll(String tableName) {
        try {
            mDb.delete(tableName, null, null);
        } catch(SQLException error) {
            return new Response(false, error.getMessage());
        }

        return new Response(true, null);
    }

    protected Response _deleteAll(String tableName, String whereClause, String[] params) {
        int result;

        try {
            result = mDb.delete(tableName, whereClause, params);
        } catch(SQLException error) {
            return new Response(false, error.getMessage());
        }

        return new Response(true, null);
    }

    protected Response _executeUpdate(String tableName, String id, String[] columnNames, String[] columnValues, String[] columnsToUpdate, String [] updatedValues) {
        boolean isSuccess;

        ContentValues map = _getContentMap(columnsToUpdate, updatedValues);

        String whereClause = _getWhereClause(columnNames, columnValues, id);

        try {
            mDb.update(tableName, map, whereClause, new String[] { id });
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }
        return new Response(true, null);
    }

    protected Response _executeUpdate(String tableName, String id, String[] columnNames, String[] columnValues, ContentValues map) {
        boolean isSuccess;
        int result;
        String whereClause = _getWhereClause(columnNames, columnValues, id);

        try {
            result = mDb.update(tableName, map, whereClause, new String[] { id });

            if(result == 0) {
                return new Response(false, "No entries found!", 535);
            }
        } catch (Exception e) {
            return new Response(false, e.getMessage());
        }

        return new Response(true, null);
    }

    protected static String _getWhereClause(String[] columnNames, String[] columnValues, String id) {
        boolean columnNamesAreValid = columnNames != null && columnNames.length != 0;
        boolean columnValuesAreValid = columnValues != null && columnValues.length != 0;

        if (!(columnNamesAreValid && columnValuesAreValid))
            return BaseContract._DEFAULT_WHERE_CLAUSE;

        int length = columnNames.length < columnValues.length
                ? columnNames.length : columnValues.length;

        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < length; i++) {
            sb.append(columnNames[i]);
            sb.append("=");
            sb.append(columnValues[i]);
        }

        return sb.toString();
    }

    private static ContentValues _getContentMap(String[] columnNames, String[] columnValues) {
        ContentValues map = new ContentValues();

        boolean columnNamesAreValid = columnNames != null && columnNames.length != 0;
        boolean columnValuesAreValid = columnValues != null && columnValues.length != 0;

        if (!(columnNamesAreValid && columnValuesAreValid))
            return map;

        int length = columnNames.length < columnValues.length
                ? columnNames.length : columnValues.length;

        for(int i = 0; i < length; i++) {
            map.put(columnNames[i], columnValues[i]);
        }

        return map;
    }
}
