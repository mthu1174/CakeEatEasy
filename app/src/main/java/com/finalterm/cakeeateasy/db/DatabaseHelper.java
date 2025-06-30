package com.finalterm.cakeeateasy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Category; // Đảm bảo import đúng model

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cakeeateasy.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";

    // --- Bảng CATEGORIES ---
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_CAT_ID = "id";
    public static final String COLUMN_CAT_NAME = "name";
    public static final String COLUMN_CAT_IMAGE_RES_ID = "image_res_id";

    // Câu lệnh SQL để tạo bảng categories
    private static final String SQL_CREATE_TABLE_CATEGORIES = "CREATE TABLE " + TABLE_CATEGORIES + " (" +
            COLUMN_CAT_ID + " TEXT PRIMARY KEY," +
            COLUMN_CAT_NAME + " TEXT NOT NULL," +
            COLUMN_CAT_IMAGE_RES_ID + " INTEGER NOT NULL)";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Hàm này chỉ chạy MỘT LẦN DUY NHẤT khi database được tạo lần đầu.
        Log.d(TAG, "Creating database for the first time.");
        db.execSQL(SQL_CREATE_TABLE_CATEGORIES);
        Log.d(TAG, "Table 'categories' created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logic để nâng cấp database khi bạn thay đổi cấu trúc bảng ở phiên bản mới.
        // Cách đơn giản nhất là xóa bảng cũ và tạo lại.
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(db);
    }

    /**
     * Thêm một category mới vào database.
     * @param category Đối tượng Category cần thêm.
     */
    public void addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CAT_ID, category.getId());
        values.put(COLUMN_CAT_NAME, category.getName());
        values.put(COLUMN_CAT_IMAGE_RES_ID, category.getImageResId());

        long result = db.insert(TABLE_CATEGORIES, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert category: " + category.getName());
        } else {
            Log.d(TAG, "Successfully inserted category: " + category.getName());
        }
        db.close(); // Luôn đóng kết nối sau khi hoàn thành.
    }

    /**
     * Lấy tất cả các category từ database.
     * @return một danh sách các đối tượng Category.
     */
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORIES;

        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor là đối tượng để duyệt qua kết quả của câu query.
        Cursor cursor = db.rawQuery(selectQuery, null);

        // Duyệt qua tất cả các dòng và thêm vào danh sách.
        if (cursor.moveToFirst()) {
            do {
                // Lấy dữ liệu từ các cột của dòng hiện tại
                String id = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CAT_NAME));
                int imageResId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CAT_IMAGE_RES_ID));

                // Tạo đối tượng Category từ dữ liệu và thêm vào list
                Category category = new Category(id, name, imageResId);
                categoryList.add(category);
            } while (cursor.moveToNext());
        }

        // Đóng các tài nguyên để tránh rò rỉ bộ nhớ
        cursor.close();
        db.close();

        Log.d(TAG, "Fetched " + categoryList.size() + " categories from database.");
        return categoryList;
    }

    /**
     * Đếm số lượng category trong database.
     * Hữu ích để kiểm tra xem database có trống không.
     * @return số lượng category.
     */
    public int getCategoriesCount() {
        String countQuery = "SELECT * FROM " + TABLE_CATEGORIES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }
}