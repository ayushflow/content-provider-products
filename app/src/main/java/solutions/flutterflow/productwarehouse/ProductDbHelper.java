package solutions.flutterflow.productwarehouse;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.UUID;

public class ProductDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "ProductDatabase.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProductContract.ProductEntry.TABLE_NAME + " (" +
                    ProductContract.ProductEntry.COLUMN_NAME_ID + " TEXT PRIMARY KEY NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_NAME_NAME + " TEXT NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_NAME_CATEGORY + " TEXT NOT NULL," +
                    ProductContract.ProductEntry.COLUMN_NAME_INVENTORY + " INTEGER NOT NULL)";

    public ProductDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is just a cache for online data, so its upgrade policy is
        // to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + ProductContract.ProductEntry.TABLE_NAME);
        onCreate(db);
    }

    public void insertSampleData(SQLiteDatabase db) {
        // Sample categories and base inventory values for variation
        String[] categories = {"Electronics", "Home & Garden", "Health & Beauty", "Toys & Hobbies"};
        int[] baseInventories = {50, 60, 70, 80};

        for (int i = 1; i <= 30; i++) {
            ContentValues values = new ContentValues();

            // Generate a UUID for each product's ID
            String uuid = UUID.randomUUID().toString();
            values.put(ProductContract.ProductEntry.COLUMN_NAME_ID, uuid);

            // Construct a product name with an index
            String productName = "Product " + i;
            values.put(ProductContract.ProductEntry.COLUMN_NAME_NAME, productName);

            // Assign a category from the predefined list, cycling through the array
            String category = categories[i % categories.length];
            values.put(ProductContract.ProductEntry.COLUMN_NAME_CATEGORY, category);

            // Assign an inventory number, varying it slightly
            int inventory = baseInventories[i % baseInventories.length] + (i * 2); // Just for variation
            values.put(ProductContract.ProductEntry.COLUMN_NAME_INVENTORY, inventory);

            // Insert the product into the database
            db.insert(ProductContract.ProductEntry.TABLE_NAME, null, values);
        }
    }

}


