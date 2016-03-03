package com.example.velkapp.db;

import android.provider.BaseColumns;

/**
 * Created by juusee on 01/03/16.
 */
public class AccountContract {
    public static final String DB_NAME = "com.example.velkapp.db.accounts";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "accounts";

    public class Columns {
        public static final String NAME = "name";
        public static final String ACCOUNT = "account";
        public static final String _ID = BaseColumns._ID;
    }
}
