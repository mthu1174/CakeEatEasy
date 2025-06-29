package com.finalterm.cakeeateasy.connectors;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finalterm.cakeeateasy.models.ProductDetails;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProductDetailsConnector extends SQLiteOpenHelper {
    private static final String DB_NAME = "Ngoc_test_login_signup.sqlite";
    private static final String DB_PATH_SUFFIX = "/databases/";
    private static final int DB_VERSION = 1;
    private Context context;
    private SQLiteDatabase database;

    public ProductDetailsConnector(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        copyDatabaseIfNeeded();
    }

    private void copyDatabaseIfNeeded() {
        String dbPath = context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DB_NAME;
        try {
            java.io.File dbDir = new java.io.File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!dbDir.exists()) dbDir.mkdir();
            InputStream is = context.getAssets().open(DB_NAME);
            OutputStream os = new FileOutputStream(dbPath, false); // always overwrite
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
            os.flush();
            os.close();
            is.close();
        } catch (IOException e) {
            Log.e("DB_COPY", "Error copying database", e);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need, pre-populated
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need
    }

    public ProductDetails getProductDetailsById(int productId) {
        ProductDetails product = null;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM ProductDetails WHERE ProductID = ?", new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                product = new ProductDetails(
                        cursor.getInt(cursor.getColumnIndexOrThrow("ProductID")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProductNameTitle")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProductCategory")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProductName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ProductDescription")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("ProductCurrentPrice")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("ProductOldPrice")),
                        cursor.getDouble(cursor.getColumnIndexOrThrow("DiscountPercent")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("QuantityPicker")),
                        cursor.getString(cursor.getColumnIndexOrThrow("SizeContent")),
                        cursor.getString(cursor.getColumnIndexOrThrow("UsageInstruction")),
                        cursor.getString(cursor.getColumnIndexOrThrow("IncludedAccessories")),
                        cursor.getString(cursor.getColumnIndexOrThrow("TitleReview")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ReviewContent")),
                        cursor.getString(cursor.getColumnIndexOrThrow("CustomerName")),
                        cursor.getString(cursor.getColumnIndexOrThrow("TitleReview2")),
                        cursor.getString(cursor.getColumnIndexOrThrow("ReviewContent2")),
                        cursor.getString(cursor.getColumnIndexOrThrow("CustomerName2"))
                );
                cursor.close();
            }
        } catch (SQLException e) {
            Log.e("DB_ERROR", "Error fetching product details", e);
        } finally {
            if (db != null) db.close();
        }
        return product;
    }
}
