package mio.storj.mobile.dataprovider;

import android.provider.BaseColumns;

public abstract class BaseContract implements BaseColumns {
    public static final String _DEFAULT_WHERE_CLAUSE = _ID + " = ?";
}
