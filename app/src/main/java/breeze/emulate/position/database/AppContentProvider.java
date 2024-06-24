package breeze.emulate.position.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AppContentProvider extends ContentProvider {

    private static final int TYPE_COORDINATE = 1;
    private static final int TYPE_SETTINGS = 2;
    private static final UriMatcher uriMatcher;
    private static AppDatabaseOpenHelper openhelper;
    private static SQLiteDatabase sqLiteDatabase;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("breeze.emulate.position.provider","coordinate",TYPE_COORDINATE);
        uriMatcher.addURI("breeze.emulate.position.provider","app_settings",TYPE_SETTINGS);
    }

    @Override
    public boolean onCreate() {
        if (openhelper == null) {
            openhelper = new AppDatabaseOpenHelper(getContext());
        }
        if (sqLiteDatabase == null) {
            sqLiteDatabase = openhelper.getReadableDatabase();
        }
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (uriMatcher.match(uri) == TYPE_COORDINATE) {
            return sqLiteDatabase.query("table_coordinate", projection, selection, selectionArgs, null, null, sortOrder);
        } else if (uriMatcher.match(uri) == TYPE_SETTINGS) {
            return sqLiteDatabase.query("app_settings", projection, selection, selectionArgs, null, null, sortOrder);
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
