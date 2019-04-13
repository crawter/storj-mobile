package mio.storj.mobile.dataprovider.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.BaseRepository;
import mio.storj.mobile.domain.settings.ISettingsRepository;
import mio.storj.mobile.domain.settings.Settings;
import mio.storj.mobile.common.responses.Response;
import mio.storj.mobile.common.responses.SingleResponse;
import mio.storj.mobile.dataprovider.BaseRepository;
import mio.storj.mobile.domain.settings.ISettingsRepository;
import mio.storj.mobile.domain.settings.Settings;

public class SettingsRepository extends BaseRepository implements ISettingsRepository {
    private final String[] mColumns;

    public SettingsRepository(SQLiteDatabase db) {
        super(db);
        mColumns = new String[] {
            SettingsContract._ID,
            SettingsContract._FIRST_SIGN_IN,
            SettingsContract._SYNC_STATUS,
            SettingsContract._SYNC_SETTINGS,
            SettingsContract._LAST_SYNC
        };
    }

    @Override
    public SingleResponse<Settings> get(String id) {
        if(id == null || id.isEmpty()) {
            return new SingleResponse<Settings>(null, false, "id param is not valid");
        }

        Cursor cursor = mDb.query(SettingsContract.TABLE_NAME,
                null,
                SettingsContract._ID + " = ?",
                new String[] { id },
                null,
                null,
                null);

        Settings model = settingFromCursor(cursor);

        cursor.close();

        return new SingleResponse<Settings>(model, model != null, null);
    }

    @Override
    public Response update(Settings settings) {
        ContentValues map = new ContentValues();

        map.put(SettingsContract._FIRST_SIGN_IN, settings.isFirstSignIn());
        map.put(SettingsContract._SYNC_STATUS, settings.isSyncStatus());
        map.put(SettingsContract._SYNC_SETTINGS, settings.getSyncSettings());
        map.put(SettingsContract._LAST_SYNC, settings.getLastSync());

        return _executeUpdate(SettingsContract.TABLE_NAME, settings.getId(), null, null, map);
    }

    @Override
    public Response insert(Settings settings) {
        ContentValues map = new ContentValues();

        map.put(SettingsContract._ID, settings.getId());
        map.put(SettingsContract._FIRST_SIGN_IN, settings.isFirstSignIn());
        map.put(SettingsContract._SYNC_STATUS, settings.isSyncStatus());
        map.put(SettingsContract._SYNC_SETTINGS, settings.getSyncSettings());
        map.put(SettingsContract._LAST_SYNC, settings.getLastSync());

        return _executeInsert(SettingsContract.TABLE_NAME, map);
    }

    @Override
    public Response createTable() {
        try {
            mDb.execSQL(SettingsContract.createTable());
            return new Response(true, null);
        } catch (SQLException ex) {
            return new Response(true, "SettingsRepository createTable() " + ex.getMessage());
        }
    }

    private Settings settingFromCursor(Cursor cursor) {
        Settings result = null;

        if (cursor.moveToFirst()){
            result = readFromCursor(cursor);
        }

        return result;
    }

    private Settings readFromCursor(Cursor cursor) {
        String lastSync = "", id = "";
        boolean syncStat = false, isFirst = false;
        int syncSettings = 0;

        for(int i = 0; i < mColumns.length; i++) {
            switch (mColumns[i]) {
                case SettingsContract._ID:
                    id = cursor.getString(i);
                case SettingsContract._LAST_SYNC:
                    lastSync = cursor.getString(i);
                    break;
                case SettingsContract._SYNC_STATUS:
                    syncStat = cursor.getInt(i) == 1;
                case SettingsContract._FIRST_SIGN_IN:
                    isFirst = cursor.getInt(i) == 1;
                case SettingsContract._SYNC_SETTINGS:
                    syncSettings = cursor.getInt(i);
                    break;
            }
        }

        return new Settings(id, isFirst, syncStat, syncSettings, lastSync);
    }
}
