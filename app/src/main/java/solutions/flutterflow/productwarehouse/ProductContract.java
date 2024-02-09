package solutions.flutterflow.productwarehouse;

import android.provider.BaseColumns;

public final class ProductContract {
    private ProductContract() {}

    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_NAME_ID = "id"; // Updated to use a String ID
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_CATEGORY = "category";
        public static final String COLUMN_NAME_INVENTORY = "inventory";
    }
}


