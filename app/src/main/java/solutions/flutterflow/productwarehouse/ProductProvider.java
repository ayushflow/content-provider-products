package solutions.flutterflow.productwarehouse;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class ProductProvider extends ContentProvider {
    public static final String AUTHORITY = "solutions.flutterflow.productwarehouse.products";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + ProductContract.ProductEntry.TABLE_NAME);
    private static final int PRODUCTS = 100;
    private static final int PRODUCT_ID = 101;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, ProductContract.ProductEntry.TABLE_NAME, PRODUCTS);
        uriMatcher.addURI(AUTHORITY, ProductContract.ProductEntry.TABLE_NAME + "/*", PRODUCT_ID);
    }

    private ProductDbHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new ProductDbHelper(getContext());
        Log.i("PRODUCT_PROVIDER", "onCreate: " + CONTENT_URI);
        return true;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = uriMatcher.match(uri);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (match) {
            case PRODUCTS:
                long id = db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e("ProductProvider", "Failed to insert row for " + uri);
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // Notice how the selection is being built for a single item query
                selection = ProductContract.ProductEntry.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[] { uri.getLastPathSegment() };
                cursor = db.query(ProductContract.ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case PRODUCTS:
                rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsUpdated = db.update(ProductContract.ProductEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case PRODUCTS:
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                selection = ProductContract.ProductEntry.COLUMN_NAME_ID + "=?";
                selectionArgs = new String[]{uri.getLastPathSegment()};
                rowsDeleted = db.delete(ProductContract.ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ProductContract.ProductEntry.TABLE_NAME;
            case PRODUCT_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + ProductContract.ProductEntry.TABLE_NAME;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

