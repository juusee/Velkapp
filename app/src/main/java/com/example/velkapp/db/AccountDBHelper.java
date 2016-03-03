package com.example.velkapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.example.velkapp.R;

/**
 * Created by juusee on 01/03/16.
 */
public class AccountDBHelper extends SQLiteOpenHelper {

    private final String TAG = "AccountDBHelper";

    private static AccountDBHelper instance;

    private String DEFAULT_SORT_ORDER = " ASC";

    private AccountDBHelper(Context context) {
        super(context, AccountContract.DB_NAME, null, AccountContract.DB_VERSION);
    }

    public static synchronized  AccountDBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new AccountDBHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sqlQuery =
                String.format("CREATE TABLE %s (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "%s TEXT, %s TEXT)",
                        AccountContract.TABLE,
                        AccountContract.Columns.ACCOUNT,
                        AccountContract.Columns.NAME);
        Log.d("AccountDBHelper", "Query to form table: " + sqlQuery);
        sqLiteDatabase.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + AccountContract.TABLE);
        onCreate(sqLiteDatabase);
    }

    public void insert(String name, String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(AccountContract.Columns.NAME, name);
        values.put(AccountContract.Columns.ACCOUNT, account);
        db.insert(AccountContract.TABLE, null, values);
    }

    public void delete(String account) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(
                AccountContract.TABLE,
                AccountContract.Columns.ACCOUNT + " LIKE ?",
                new String[]{account}
        );
    }

    public void update(String oldAccount, String newName, String newAccount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.clear();
        values.put(AccountContract.Columns.NAME, newName);
        values.put(AccountContract.Columns.ACCOUNT, newAccount);
        db.update(
                AccountContract.TABLE,
                values,
                AccountContract.Columns.ACCOUNT + " LIKE ?",
                new String[]{oldAccount}
        );
    }

    public boolean accountInDB(String account) {
        SQLiteDatabase db = this.getReadableDatabase();
        /*String sql = String.format("SELECT 1 FROM %s WHERE %s = %s",
                AccountContract.TABLE,
                AccountContract.Columns.ACCOUNT,
                account);
        */Cursor cursor = db.query(AccountContract.TABLE,
                new String[] { AccountContract.Columns.ACCOUNT },
                AccountContract.Columns.ACCOUNT + " = ?",
                new String[] { account },
                null, null, null);
        if (cursor.moveToFirst())
            return true;
        return false;
    }

    public Cursor list() {
        SQLiteDatabase sqlDB = this.getReadableDatabase();
        Cursor cursor = sqlDB.query(
                AccountContract.TABLE,
                new String[]{AccountContract.Columns._ID,
                        AccountContract.Columns.NAME,
                        AccountContract.Columns.ACCOUNT},
                null, null, null, null,
                AccountContract.Columns.NAME + DEFAULT_SORT_ORDER);
        return cursor;
    }
}
