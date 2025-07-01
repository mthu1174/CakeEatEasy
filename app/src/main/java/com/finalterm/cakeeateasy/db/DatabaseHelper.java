package com.finalterm.cakeeateasy.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import com.finalterm.cakeeateasy.models.Notification;
import com.finalterm.cakeeateasy.utils.Utils;

import androidx.annotation.Nullable;

import com.finalterm.cakeeateasy.models.CartItem;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.models.Customer;
import com.finalterm.cakeeateasy.models.Order;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.models.Review;
import com.finalterm.cakeeateasy.models.Voucher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private static final String DATABASE_NAME = "Client_Database.db";
    private static final int DATABASE_VERSION = 2; // Tăng version nếu bạn thay đổi cấu trúc bảng sau này
    private static String DATABASE_PATH = ""; // Sẽ được khởi tạo trong constructor

    // --- Tên Bảng ---
    public static final String TABLE_CART = "Cart";
    public static final String TABLE_CATEGORY = "Category";
    public static final String TABLE_CUSTOMER = "Customer";
    public static final String TABLE_CUSTOMER_ORDER = "CustomerOrder";
    public static final String TABLE_NOTIFICATION = "Notification";
    public static final String TABLE_ORDER_ITEM = "OrderItem";
    public static final String TABLE_PAYMENT = "Payment";
    public static final String TABLE_PRODUCT = "Product";
    public static final String TABLE_REVIEW = "Review";
    public static final String TABLE_SHIPMENT = "Shipment";
    public static final String TABLE_WISHLIST = "Wishlist";
    public static final String TABLE_VOUCHER = "Voucher";

    // --- Hằng số Cột (giữ nguyên như cũ) ---
    public static final String COL_CUSTOMER_ID = "customer_id";
    public static final String COL_CUSTOMER_NAME = "name";
    public static final String COL_CUSTOMER_EMAIL = "email";
    public static final String COL_CUSTOMER_PASSWORD = "password";
    public static final String COL_CUSTOMER_PHONE = "phone";
    public static final String COL_CUSTOMER_ADDRESS = "address";
    public static final String COL_CUSTOMER_DOB = "dob";
    public static final String COL_CUSTOMER_USERNAME = "username";
    public static final String COL_CUSTOMER_CART_ID = "cart_id";
    public static final String COL_CATEGORY_ID = "category_id";
    public static final String COL_CATEGORY_NAME = "name";
    public static final String COL_CATEGORY_IMAGE = "image";
    public static final String COL_PRODUCT_ID = "product_id";
    public static final String COL_PRODUCT_NAME = "name";
    public static final String COL_PRODUCT_DESC = "description";
    public static final String COL_PRODUCT_PRICE = "price";
    public static final String COL_PRODUCT_ORIGINAL_PRICE = "original_price";
    public static final String COL_PRODUCT_STOCK = "stock";
    public static final String COL_PRODUCT_CATEGORY_ID = "category_id";
    public static final String COL_PRODUCT_IMAGE_URL = "image_url";
    public static final String COL_ORDER_ITEM_ID = "order_item_id";
    public static final String COL_ORDER_ITEM_QUANTITY = "quantity";
    public static final String COL_ORDER_ITEM_PRICE = "price";
    public static final String COL_ORDER_ITEM_PRODUCT_ID = "product_id";
    public static final String COL_ORDER_ITEM_CART_ID = "cart_id";
    public static final String COL_ORDER_ITEM_ORDER_ID = "order_id";

    // --- BIẾN ĐỂ THỰC HIỆN LOGIC COPY DATABASE ---
    private final Context context;
    private boolean mDidCopy = false; // Cờ để kiểm tra DB đã được copy chưa

    /**
     * Constructor đã được sửa đổi để kiểm tra và copy database từ assets
     */
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // Lấy đường dẫn tới database của ứng dụng
        DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getPath();
        Log.d(TAG, "Database path: " + DATABASE_PATH);

        // Kiểm tra và copy DB ngay trong constructor
        try {
            createDatabase();
        } catch (IOException e) {
            throw new Error("Unable to create database", e);
        }
    }

    /**
     * Tạo database nếu nó chưa tồn tại bằng cách copy từ assets.
     */
    public void createDatabase() throws IOException {
        boolean dbExist = checkDatabase();

        if (dbExist) {
            Log.d(TAG, "Database already exists. No need to copy.");
            // Không làm gì cả - database đã tồn tại
        } else {
            // Database chưa tồn tại, tiến hành copy từ assets
            this.getReadableDatabase(); // Gọi hàm này để tạo ra một db rỗng trong đường dẫn mặc định
            this.close(); // Đóng nó lại để có thể ghi đè file
            try {
                copyDataBaseFromAssets();
                mDidCopy = true; // Bật cờ đã copy
                Log.d(TAG, "Database copied successfully from assets.");
            } catch (IOException e) {
                throw new Error("Error copying database from assets", e);
            }
        }
    }

    /**
     * Kiểm tra xem file database đã tồn tại trong thư mục data của app chưa.
     * @return true nếu tồn tại, false nếu không.
     */
    private boolean checkDatabase() {
        File dbFile = new File(DATABASE_PATH);
        return dbFile.exists();
    }

    /**
     * Sao chép file database từ thư mục /assets vào thư mục data của ứng dụng.
     */
    private void copyDataBaseFromAssets() throws IOException {
        // Mở file database trong assets như một luồng nhập
        InputStream myInput = context.getAssets().open(DATABASE_NAME);

        // Đường dẫn đến file database sẽ được tạo
        String outFileName = DATABASE_PATH;

        // Mở file database rỗng như một luồng xuất
        OutputStream myOutput = new FileOutputStream(outFileName);

        // Chuyển bytes từ myInput vào myOutput
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        // Đóng các luồng
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Hàm này sẽ chỉ được gọi bởi SQLiteOpenHelper nếu database chưa được tạo.
     * HOẶC nó sẽ được gọi thủ công từ onOpen() sau khi chúng ta copy xong file từ assets.
     *
     * SỬA ĐỔI QUAN TRỌNG: Dùng "CREATE TABLE IF NOT EXISTS" cho tất cả các bảng.
     * Điều này đảm bảo an toàn khi gọi hàm này trên một database đã có sẵn một vài bảng.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called. Creating tables IF THEY DON'T EXIST...");

        final String SQL_CREATE_TABLE_CUSTOMER = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER + " ("
                + COL_CUSTOMER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_CUSTOMER_NAME + " TEXT NOT NULL, "
                + COL_CUSTOMER_USERNAME + " TEXT NOT NULL UNIQUE, " // THÊM DÒNG NÀY
                + COL_CUSTOMER_EMAIL + " TEXT NOT NULL UNIQUE, "
                + COL_CUSTOMER_PASSWORD + " TEXT NOT NULL, "
                + COL_CUSTOMER_PHONE + " TEXT, "
                + COL_CUSTOMER_ADDRESS + " TEXT, "
                + COL_CUSTOMER_DOB + " TEXT);";
        final String SQL_CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY + " (" + COL_CATEGORY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_CATEGORY_NAME + " TEXT NOT NULL, " + COL_CATEGORY_IMAGE + " TEXT);";
        final String SQL_CREATE_TABLE_PRODUCT = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCT + " (" + COL_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PRODUCT_NAME + " TEXT NOT NULL, " + COL_PRODUCT_DESC + " TEXT, " + COL_PRODUCT_PRICE + " REAL NOT NULL, " + COL_PRODUCT_ORIGINAL_PRICE + " REAL DEFAULT 0, " + COL_PRODUCT_STOCK + " INTEGER NOT NULL, " + COL_PRODUCT_CATEGORY_ID + " INTEGER, " + COL_PRODUCT_IMAGE_URL + " TEXT, FOREIGN KEY(" + COL_PRODUCT_CATEGORY_ID + ") REFERENCES " + TABLE_CATEGORY + "(" + COL_CATEGORY_ID + ") ON DELETE SET NULL);";
        final String SQL_CREATE_TABLE_CART = "CREATE TABLE IF NOT EXISTS " + TABLE_CART + " (cart_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL UNIQUE, FOREIGN KEY(customer_id) REFERENCES " + TABLE_CUSTOMER + "(customer_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_CUSTOMER_ORDER = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMER_ORDER + " (order_id TEXT PRIMARY KEY, customer_id INTEGER NOT NULL, order_date TEXT DEFAULT CURRENT_TIMESTAMP, total REAL NOT NULL, status TEXT NOT NULL, FOREIGN KEY(customer_id) REFERENCES " + TABLE_CUSTOMER + "(customer_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_ORDER_ITEM = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER_ITEM + " (" + COL_ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, " + COL_ORDER_ITEM_PRICE + " REAL NOT NULL, " + COL_ORDER_ITEM_PRODUCT_ID + " INTEGER NOT NULL, " + COL_ORDER_ITEM_CART_ID + " INTEGER, " + COL_ORDER_ITEM_ORDER_ID + " INTEGER, FOREIGN KEY(" + COL_ORDER_ITEM_CART_ID + ") REFERENCES " + TABLE_CART + "(cart_id) ON DELETE CASCADE, FOREIGN KEY(" + COL_ORDER_ITEM_ORDER_ID + ") REFERENCES " + TABLE_CUSTOMER_ORDER + "(order_id) ON DELETE CASCADE, FOREIGN KEY(" + COL_ORDER_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_PAYMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENT + " (payment_id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL, amount REAL NOT NULL, payment_date TEXT DEFAULT CURRENT_TIMESTAMP, payment_method TEXT NOT NULL, FOREIGN KEY(order_id) REFERENCES " + TABLE_CUSTOMER_ORDER + "(order_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_REVIEW = "CREATE TABLE IF NOT EXISTS " + TABLE_REVIEW + " (review_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL, product_id INTEGER NOT NULL, rating INTEGER NOT NULL CHECK(rating >= 1 AND rating <= 5), comment TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP, title TEXT, FOREIGN KEY(customer_id) REFERENCES " + TABLE_CUSTOMER + "(customer_id) ON DELETE CASCADE, FOREIGN KEY(product_id) REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_SHIPMENT = "CREATE TABLE IF NOT EXISTS " + TABLE_SHIPMENT + " (shipment_id INTEGER PRIMARY KEY AUTOINCREMENT, order_id INTEGER NOT NULL, shipment_date TEXT, tracking_number TEXT, status TEXT, FOREIGN KEY(order_id) REFERENCES " + TABLE_CUSTOMER_ORDER + "(order_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_WISHLIST = "CREATE TABLE IF NOT EXISTS " + TABLE_WISHLIST + " (wishlist_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL, product_id INTEGER NOT NULL, FOREIGN KEY(customer_id) REFERENCES " + TABLE_CUSTOMER + "(customer_id) ON DELETE CASCADE, FOREIGN KEY(product_id) REFERENCES " + TABLE_PRODUCT + "(product_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + " (notification_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_id INTEGER NOT NULL, message TEXT NOT NULL, created_at TEXT DEFAULT CURRENT_TIMESTAMP, is_read INTEGER DEFAULT 0, order_id INTEGER, FOREIGN KEY(customer_id) REFERENCES " + TABLE_CUSTOMER + "(customer_id) ON DELETE CASCADE, FOREIGN KEY(order_id) REFERENCES " + TABLE_CUSTOMER_ORDER + "(order_id) ON DELETE CASCADE);";
        final String SQL_CREATE_TABLE_VOUCHER = "CREATE TABLE IF NOT EXISTS " + TABLE_VOUCHER + " (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, description TEXT, code TEXT UNIQUE, discount_amount INTEGER, title_color_res INTEGER, bg_color_res INTEGER, icon_res INTEGER);";

        db.execSQL(SQL_CREATE_TABLE_CUSTOMER);
        db.execSQL(SQL_CREATE_TABLE_CATEGORY);
        db.execSQL(SQL_CREATE_TABLE_PRODUCT);
        db.execSQL(SQL_CREATE_TABLE_CART);
        db.execSQL(SQL_CREATE_TABLE_CUSTOMER_ORDER);
        db.execSQL(SQL_CREATE_TABLE_ORDER_ITEM);
        db.execSQL(SQL_CREATE_TABLE_PAYMENT);
        db.execSQL(SQL_CREATE_TABLE_REVIEW);
        db.execSQL(SQL_CREATE_TABLE_SHIPMENT);
        db.execSQL(SQL_CREATE_TABLE_WISHLIST);
        db.execSQL(SQL_CREATE_TABLE_NOTIFICATION);
        db.execSQL(SQL_CREATE_TABLE_VOUCHER); // Thêm bảng Voucher

        Log.d(TAG, "All CREATE TABLE IF NOT EXISTS statements executed.");
    }

    /**
     * Phương thức này được gọi mỗi khi mở database.
     * Chúng ta sẽ dùng nó để gọi onCreate() nếu database vừa được copy xong.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (mDidCopy) {
            // Nếu database vừa được copy, có khả năng cấu trúc của nó cũ hơn code hiện tại
            // (ví dụ: thiếu bảng Voucher).
            // Gọi onCreate() để nó chạy các lệnh "CREATE TABLE IF NOT EXISTS",
            // đảm bảo tất cả các bảng cần thiết đều tồn tại.
            Log.d(TAG, "Database was just copied. Calling onCreate() manually to create missing tables.");
            onCreate(db);
            mDidCopy = false; // Reset lại cờ, chỉ chạy một lần duy nhất
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Logic này sẽ xóa toàn bộ dữ liệu cũ và tạo lại.
        // Đây là cách đơn giản nhất.
        // Với logic copy từ assets, onUpgrade ít khi được gọi trên thiết bị người dùng mới,
        // nhưng nó vẫn cần thiết cho các bản cập nhật sau này.
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data.");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOUCHER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WISHLIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHIPMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PAYMENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER);
        onCreate(db);
    }

    // ===================================================================
    // CÁC HÀM CRUD CỦA BẠN (GIỮ NGUYÊN, KHÔNG CẦN THAY ĐỔI)
    // Dưới đây là toàn bộ code các hàm khác của bạn, tui chỉ dán lại thôi.
    // ===================================================================

    // --- CUSTOMER ---
    public long addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CUSTOMER_NAME, customer.getName());
            values.put(COL_CUSTOMER_EMAIL, customer.getEmail());
            values.put(COL_CUSTOMER_PASSWORD, customer.getPassword());
            values.put(COL_CUSTOMER_PHONE, customer.getPhone());
            values.put(COL_CUSTOMER_ADDRESS, customer.getAddress());
            values.put(COL_CUSTOMER_DOB, customer.getDob());
            newRowId = db.insertOrThrow(TABLE_CUSTOMER, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding customer: " + e.getMessage());
        } finally {
        }
        return newRowId;
    }
    public int getUserIdByCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        int userId = -1;

        String[] columns = {COL_CUSTOMER_ID}; // Nên dùng hằng số cột luôn cho nhất quán
        String selection = COL_CUSTOMER_USERNAME + " = ? AND " + COL_CUSTOMER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        // Sửa lại dòng này để dùng hằng số tên bảng
        Cursor cursor = db.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CUSTOMER_ID));
            }
            cursor.close();
        }
        // Không cần đóng db ở đây nếu bạn muốn hàm này được tái sử dụng

        return userId;
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            String selection = COL_CUSTOMER_EMAIL + " = ?";
            String[] selectionArgs = {email};
            cursor = db.query(TABLE_CUSTOMER, new String[]{COL_CUSTOMER_ID}, selection, selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                exists = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking if email exists", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return exists;
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean exists = false;
        try {
            // Câu truy vấn chỉ cần tìm trong cột username
            // COL_CUSTOMER_USERNAME là hằng số bạn đã định nghĩa, ví dụ: "username"
            String selection = COL_CUSTOMER_USERNAME + " = ?";
            String[] selectionArgs = { username };

            cursor = db.query(
                    TABLE_CUSTOMER,             // Tên bảng
                    new String[]{COL_CUSTOMER_ID}, // Chỉ cần lấy 1 cột bất kỳ để đếm
                    selection,                  // Mệnh đề WHERE
                    selectionArgs,              // Giá trị cho WHERE
                    null,
                    null,
                    null
            );

            // Nếu cursor trả về nhiều hơn 0 dòng, nghĩa là username đã tồn tại
            if (cursor != null && cursor.getCount() > 0) {
                exists = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi kiểm tra username tồn tại", e);
        } finally {
            // Luôn đóng cursor và database để tránh rò rỉ bộ nhớ
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
    public int getCustomersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CUSTOMER, null);
            if (cursor != null) count = cursor.getCount();
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    // --- CATEGORY ---
    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_CATEGORY_NAME, category.getName());
            values.put(COL_CATEGORY_IMAGE, category.getImage());
            newRowId = db.insertOrThrow(TABLE_CATEGORY, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding category: " + e.getMessage());
        } finally {
        }
        return newRowId;
    }
    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_CATEGORY, null, null, null, null, null, COL_CATEGORY_NAME + " ASC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_CATEGORY_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_NAME));
                    String image = cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY_IMAGE));
                    categoryList.add(new Category(id, name, image));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all categories: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return categoryList;
    }
    public int getCategoriesCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY, null);
            if (cursor != null) count = cursor.getCount();
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    // --- PRODUCT ---
    public long addProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put(COL_PRODUCT_NAME, product.getName());
            values.put(COL_PRODUCT_DESC, product.getDescription());
            values.put(COL_PRODUCT_PRICE, product.getPrice());
            values.put(COL_PRODUCT_ORIGINAL_PRICE, product.getOriginalPrice());
            values.put(COL_PRODUCT_STOCK, product.getStock());
            values.put(COL_PRODUCT_CATEGORY_ID, product.getCategoryId());
            values.put(COL_PRODUCT_IMAGE_URL, product.getImageUrl());
            newRowId = db.insertOrThrow(TABLE_PRODUCT, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding product", e);
        } finally {
        }
        return newRowId;
    }

    /**
     * Lấy TẤT CẢ các sản phẩm từ database, đồng thời JOIN để lấy tên category.
     * @return Danh sách tất cả đối tượng Product.
     */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        final String GET_ALL_PRODUCTS_QUERY = "SELECT p.*, c.name as category_name FROM " +
                TABLE_PRODUCT + " p LEFT JOIN " + TABLE_CATEGORY + " c ON p.category_id = c.category_id";

        try {
            cursor = db.rawQuery(GET_ALL_PRODUCTS_QUERY, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
                    double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_ORIGINAL_PRICE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_STOCK));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY_ID));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                    Product product = new Product(id, name, desc, price, originalPrice, stock, categoryId, imageUrl, categoryName);
                    product.setCategoryName(categoryName);
                    productList.add(product);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy tất cả sản phẩm", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return productList;
    }
    public List<Review> getReviewsByProductId(int productId) {
        List<Review> reviewList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_REVIEW, null, "product_id = ?", new String[]{String.valueOf(productId)}, null, null, "created_at DESC");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Cần triển khai logic đọc dữ liệu từ cursor và tạo đối tượng Review
                    // Ví dụ: int reviewId = cursor.getInt(cursor.getColumnIndexOrThrow("review_id")); ...
                    // reviewList.add(new Review(...));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting reviews by product id", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return reviewList;
    }
    public int getProductsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCT, null);
            if (cursor != null) count = cursor.getCount();
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }

    public Product getProductById(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Product product = null;

        final String GET_PRODUCT_BY_ID_QUERY = "SELECT p.*, c.name as category_name FROM " +
                TABLE_PRODUCT + " p LEFT JOIN " + TABLE_CATEGORY + " c ON p.category_id = c.category_id " +
                "WHERE p.product_id = ?";

        try {
            cursor = db.rawQuery(GET_PRODUCT_BY_ID_QUERY, new String[]{String.valueOf(productId)});
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
                double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_ORIGINAL_PRICE));
                int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_STOCK));
                int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY_ID));
                String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));
                String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                product = new Product(id, name, desc, price, originalPrice, stock, categoryId, imageUrl, categoryName);
                product.setCategoryName(categoryName);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy sản phẩm theo ID", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return product;
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        final String GET_PRODUCTS_QUERY = "SELECT p.*, c.name as category_name FROM " +
                TABLE_PRODUCT + " p LEFT JOIN " + TABLE_CATEGORY + " c ON p.category_id = c.category_id " +
                "WHERE p.category_id = ?";

        try {
            cursor = db.rawQuery(GET_PRODUCTS_QUERY, new String[]{String.valueOf(categoryId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {

                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
                    double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_ORIGINAL_PRICE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_STOCK));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                    Product product = new Product(id, name, desc, price, originalPrice, stock, categoryId, imageUrl, categoryName);
                    product.setCategoryName(categoryName);
                    productList.add(product);

                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return productList;
    }

    // --- CART & ORDER ---
    private int getOrCreateCartId(int customerId) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        int cartId = -1;
        try {
            cursor = db.query(TABLE_CART, new String[]{"cart_id"}, "customer_id = ?", new String[]{String.valueOf(customerId)}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                cartId = cursor.getInt(cursor.getColumnIndexOrThrow("cart_id"));
            } else {
                ContentValues values = new ContentValues();
                values.put("customer_id", customerId);
                cartId = (int) db.insertOrThrow(TABLE_CART, null, values);
                Log.d(TAG, "Created new cart with ID: " + cartId + " for customer: " + customerId);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting or creating cart", e);
        } finally {
            if (cursor != null) cursor.close();
            // Không đóng db ở đây vì nó có thể được gọi bởi hàm khác đang mở db
        }
        return cartId;
    }

    // Trong DatabaseHelper.java

    // Thêm một tham số `price` vào hàm
    public void addToCart(int customerId, int productId, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Bước 1: Lấy cartId của người dùng
        int cartId = -1;
        Cursor cartCursor = null;
        try {
            cartCursor = db.query(TABLE_CART, new String[]{"cart_id"}, "customer_id = ?", new String[]{String.valueOf(customerId)}, null, null, null);
            if (cartCursor != null && cartCursor.moveToFirst()) {
                cartId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("cart_id"));
            } else {
                // Nếu chưa có giỏ hàng, tạo mới
                ContentValues values = new ContentValues();
                values.put("customer_id", customerId);
                cartId = (int) db.insert(TABLE_CART, null, values);
            }
        } finally {
            if (cartCursor != null) cartCursor.close();
        }

        if (cartId == -1) {
            Log.e(TAG, "Không thể lấy hoặc tạo giỏ hàng.");
            return;
        }

        // Bước 2: Kiểm tra xem sản phẩm đã có trong giỏ hàng chưa
        Cursor itemCursor = null;
        try {
            String selection = "cart_id = ? AND product_id = ?";
            String[] selectionArgs = {String.valueOf(cartId), String.valueOf(productId)};
            itemCursor = db.query(TABLE_ORDER_ITEM, new String[]{"order_item_id", "quantity"}, selection, selectionArgs, null, null, null);

            if (itemCursor != null && itemCursor.moveToFirst()) {
                // Nếu đã có, cập nhật số lượng
                int existingQuantity = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("quantity"));
                int orderItemId = itemCursor.getInt(itemCursor.getColumnIndexOrThrow("order_item_id"));
                int newQuantity = existingQuantity + quantity;

                ContentValues updateValues = new ContentValues();
                updateValues.put("quantity", newQuantity);
                db.update(TABLE_ORDER_ITEM, updateValues, "order_item_id = ?", new String[]{String.valueOf(orderItemId)});
            } else {
                // Nếu chưa có, thêm mới
                ContentValues insertValues = new ContentValues();
                insertValues.put("cart_id", cartId);
                insertValues.put("product_id", productId);
                insertValues.put("quantity", quantity);
                insertValues.put("price", price); // Sử dụng giá được truyền vào
                db.insert(TABLE_ORDER_ITEM, null, insertValues);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error adding to cart", e);
        } finally {
            if (itemCursor != null) itemCursor.close();
            // KHÔNG ĐÓNG DB Ở ĐÂY
        }
    }

    public List<CartItem> getCartItemsByCustomerId(int customerId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        int cartId = -1;
        // Lấy cartId trong cùng transaction
        Cursor cartCursor = null;
        try {
            cartCursor = db.query(TABLE_CART, new String[]{"cart_id"}, "customer_id = ?", new String[]{String.valueOf(customerId)}, null, null, null);
            if (cartCursor != null && cartCursor.moveToFirst()) {
                cartId = cartCursor.getInt(cartCursor.getColumnIndexOrThrow("cart_id"));
            }
        } finally {
            if (cartCursor != null) cartCursor.close();
        }

        if (cartId == -1) {
            return cartItems;
        }

        Cursor cursor = null;
        final String GET_CART_ITEMS_QUERY = "SELECT oi.*, p." + COL_PRODUCT_NAME + ", p." + COL_PRODUCT_IMAGE_URL + " FROM " + TABLE_ORDER_ITEM + " oi JOIN " + TABLE_PRODUCT + " p ON oi." + COL_ORDER_ITEM_PRODUCT_ID + " = p." + COL_PRODUCT_ID + " WHERE oi." + COL_ORDER_ITEM_CART_ID + " = ?";
        try {
            cursor = db.rawQuery(GET_CART_ITEMS_QUERY, new String[]{String.valueOf(cartId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int orderItemId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_ID));
                    int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_PRICE));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ORDER_ITEM_QUANTITY));
                    cartItems.add(new CartItem(orderItemId, productId, name, price, imageUrl, quantity));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting cart items", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return cartItems;
    }

    public void updateCartItemQuantity(int orderItemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(COL_ORDER_ITEM_QUANTITY, newQuantity);
            db.update(TABLE_ORDER_ITEM, values, COL_ORDER_ITEM_ID + " = ?", new String[]{String.valueOf(orderItemId)});
        } catch (Exception e) {
            Log.e(TAG, "Error updating cart item quantity", e);
        } finally {
        }
    }

    public void removeCartItem(int orderItemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(TABLE_ORDER_ITEM, COL_ORDER_ITEM_ID + " = ?", new String[]{String.valueOf(orderItemId)});
        } catch (Exception e) {
            Log.e(TAG, "Error removing cart item", e);
        } finally {
        }
    }

    public String placeOrderFromCartItems(int customerId, List<CartItem> itemsToCheckout, double totalAmount) {
        SQLiteDatabase db = this.getWritableDatabase();
        String newOrderId = Utils.generateRandomOrderId(5);
        Log.d("ORDER_FLOW", "Chuẩn bị tạo đơn hàng mới. ID: " + newOrderId + " cho CustomerID: " + customerId);

        // 1. Sinh mã Order ID ngẫu nhiên trước khi thực hiện transaction

        db.beginTransaction();
        try {
            // 2. Dùng mã Order ID vừa tạo để chèn vào CSDL
            ContentValues orderValues = new ContentValues();
            orderValues.put("order_id", newOrderId); // <-- Dùng mã mới
            orderValues.put("customer_id", customerId);
            orderValues.put("total", totalAmount);
            orderValues.put("status", "Ongoing"); // Đặt trạng thái ban đầu là Ongoing

            db.insertOrThrow(TABLE_CUSTOMER_ORDER, null, orderValues);

            // 3. Cập nhật các OrderItem để trỏ đến mã Order ID mới
            for (CartItem item : itemsToCheckout) {
                ContentValues itemUpdateValues = new ContentValues();
                itemUpdateValues.put("order_id", newOrderId); // <-- Dùng mã mới
                itemUpdateValues.putNull(COL_ORDER_ITEM_CART_ID); // Xóa khỏi giỏ hàng
                db.update(TABLE_ORDER_ITEM, itemUpdateValues, COL_ORDER_ITEM_ID + " = ?", new String[]{String.valueOf(item.getOrderItemId())});
            }

            db.setTransactionSuccessful();
            Log.d(TAG, "Order placed successfully with ID: " + newOrderId);

        } catch (Exception e) {
            Log.e("ORDER_FLOW", "LỖI KHI ĐẶT HÀNG: ", e); // In ra cả exception
            newOrderId = null;

        } finally {
            db.endTransaction();
        }
        return newOrderId; // 4. Trả về mã Order ID dạng String
    }


    // === THAY ĐỔI QUAN TRỌNG: Hàm getOrdersByStatus ===
    // Sửa lại để đọc order_id dạng String và tạo đối tượng Order tương ứng.
    public List<Order> getOrdersByStatus(int customerId, List<String> statuses) {
        List<Order> orderList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String placeholders = TextUtils.join(",", Collections.nCopies(statuses.size(), "?"));

            final String GET_ORDERS_QUERY =
                    "SELECT " +
                            "o.order_id, o.order_date, o.total, o.status, " +
                            "(SELECT p.name FROM OrderItem oi JOIN Product p ON oi.product_id = p.product_id WHERE oi.order_id = o.order_id LIMIT 1) AS first_product_name, " +
                            "(SELECT p.image_url FROM OrderItem oi JOIN Product p ON oi.product_id = p.product_id WHERE oi.order_id = o.order_id LIMIT 1) AS first_product_image " +
                            "FROM " + TABLE_CUSTOMER_ORDER + " o " +
                            "WHERE o.customer_id = ? AND o.status IN (" + placeholders + ") " +
                            "ORDER BY o.order_date DESC";

            List<String> argsList = new ArrayList<>();
            argsList.add(String.valueOf(customerId));
            argsList.addAll(statuses);
            String[] selectionArgs = argsList.toArray(new String[0]);

            cursor = db.rawQuery(GET_ORDERS_QUERY, selectionArgs);

            if (cursor != null && cursor.moveToFirst()) {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                do {
                    // Đọc order_id từ CSDL dưới dạng String
                    String orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));
                    String date = cursor.getString(cursor.getColumnIndexOrThrow("order_date"));
                    String productName = cursor.getString(cursor.getColumnIndexOrThrow("first_product_name"));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("first_product_image"));
                    double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
                    String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));

                    // Tạo đối tượng Order với orderId là String
                    orderList.add(new Order(orderId, date, productName, imageUrl, currencyFormat.format(total), status));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting orders by status", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return orderList;
    }

    // --- WISHLIST (FAVOURITE) ---
    public void addToWishlist(int customerId, int productId) {
        if (isFavourite(customerId, productId)) {
            Log.d(TAG, "Product " + productId + " is already in wishlist for customer " + customerId);
            return;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("customer_id", customerId);
            values.put("product_id", productId);
            db.insert(TABLE_WISHLIST, null, values);
            Log.d(TAG, "Added product " + productId + " to wishlist for customer " + customerId);
        } catch (Exception e) {
            Log.e(TAG, "Error adding to wishlist", e);
        } finally {
        }
    }
    public boolean removeFromWishlist(int customerId, int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String whereClause = "customer_id = ? AND product_id = ?";
            String[] whereArgs = { String.valueOf(customerId), String.valueOf(productId) };
            db.delete(TABLE_WISHLIST, whereClause, whereArgs);
            Log.d(TAG, "Removed product " + productId + " from wishlist for customer " + customerId);
        } catch (Exception e) {
            Log.e(TAG, "Error removing from wishlist", e);
        } finally {
        }
        return false;
    }
    public boolean isFavourite(int customerId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean isFav = false;
        try {
            String whereClause = "customer_id = ? AND product_id = ?";
            String[] whereArgs = { String.valueOf(customerId), String.valueOf(productId) };
            cursor = db.query(TABLE_WISHLIST, null, whereClause, whereArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                isFav = true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking favourite status", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return isFav;
    }
    public List<Product> getFavouriteProducts(int customerId) {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        final String GET_FAVOURITES_QUERY = "SELECT p.*, c.name AS category_name FROM " + TABLE_PRODUCT + " p " +
                "JOIN " + TABLE_WISHLIST + " w ON p.product_id = w.product_id " +
                "LEFT JOIN " + TABLE_CATEGORY + " c ON p.category_id = c.category_id " +
                "WHERE w.customer_id = ?";

        try {
            cursor = db.rawQuery(GET_FAVOURITES_QUERY, new String[]{String.valueOf(customerId)});
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    // Dùng hàm tiện ích để tạo đối tượng Product từ Cursor
                    Product product = createProductFromCursor(cursor);

                    // === SỬA LỖI 1: THÊM SẢN PHẨM VÀO DANH SÁCH ===
                    productList.add(product);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting favourite products", e);
        } finally {
            if (cursor != null) cursor.close();
        }

        Log.d("DB_GET_FAV_DEBUG", "Hàm getFavouriteProducts trả về " + productList.size() + " sản phẩm cho customerId " + customerId);
        return productList;
    }

    /**
     * Hàm tiện ích để tạo đối tượng Product từ một Cursor.
     * Tránh lặp code.
     */
    private Product createProductFromCursor(Cursor cursor) {
        // Đổi tên biến 'id' thành 'productId' cho khớp với model
        int productId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
        double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_ORIGINAL_PRICE));
        int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_STOCK));
        int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY_ID));
        String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));

        // Lấy tên category từ kết quả JOIN
        int categoryNameColIndex = cursor.getColumnIndex("category_name");
        String categoryName = "Không xác định"; // Giá trị mặc định
        if (categoryNameColIndex != -1 && !cursor.isNull(categoryNameColIndex)) {
            categoryName = cursor.getString(categoryNameColIndex);
        }

        // Gọi đúng constructor đầy đủ nhất của Product
        return new Product(productId, name, desc, price, originalPrice, stock, categoryId, imageUrl, categoryName);
    }
    public List<Product> getPromoProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        // === CÂU TRUY VẤN MỚI ===
        // 1. SELECT p.*, c.name as category_name: Lấy tất cả cột từ bảng Product (p) và cột name từ Category (c), đổi tên thành category_name
        // 2. JOIN ... ON ...: Kết nối 2 bảng qua category_id
        // 3. WHERE p.original_price > p.price AND p.original_price > 0: Chỉ lấy sản phẩm có giá gốc lớn hơn giá bán (tức là có giảm giá)
        final String GET_PROMO_PRODUCTS_QUERY = "SELECT p.*, c.name as category_name FROM " +
                TABLE_PRODUCT + " p LEFT JOIN " + TABLE_CATEGORY + " c ON p.category_id = c.category_id " +
                "WHERE p.original_price > p.price AND p.original_price > 0";

        try {
            cursor = db.rawQuery(GET_PROMO_PRODUCTS_QUERY, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_DESC));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE));
                    double originalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_ORIGINAL_PRICE));
                    int stock = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_STOCK));
                    int categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_PRODUCT_CATEGORY_ID));
                    String imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE_URL));
                    // Lấy tên category từ kết quả JOIN
                    String categoryName = cursor.getString(cursor.getColumnIndexOrThrow("category_name"));

                    // Tạo đối tượng Product với thông tin mới
                    Product product = new Product(id, name, desc, price, originalPrice, stock, categoryId, imageUrl, categoryName);
                    product.setCategoryName(categoryName); // Set tên category cho sản phẩm
                    productList.add(product);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy sản phẩm khuyến mãi", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return productList;
    }

    // --- VOUCHER ---
    public long addVoucher(Voucher voucher) {
        SQLiteDatabase db = this.getWritableDatabase();
        long newRowId = -1;
        try {
            ContentValues values = new ContentValues();
            values.put("title", voucher.getTitle());
            values.put("description", voucher.getDescription());
            values.put("title_color_res", voucher.getTitleAndIconColorRes());
            values.put("bg_color_res", voucher.getIconBackgroundColorRes());
            values.put("icon_res", voucher.getIconRes());
            values.put("code", voucher.getVoucherCode());
            values.put("discount_amount", voucher.getDiscountAmount());
            newRowId = db.insertOrThrow(TABLE_VOUCHER, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding voucher", e);
        } finally {
        }
        return newRowId;
    }
    public List<Voucher> getAllVouchers() {
        List<Voucher> voucherList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_VOUCHER, null, null, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
                    String desc = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                    int titleColor = cursor.getInt(cursor.getColumnIndexOrThrow("title_color_res"));
                    int bgColor = cursor.getInt(cursor.getColumnIndexOrThrow("bg_color_res"));
                    int iconRes = cursor.getInt(cursor.getColumnIndexOrThrow("icon_res"));
                    String code = cursor.getString(cursor.getColumnIndexOrThrow("code"));
                    int amount = cursor.getInt(cursor.getColumnIndexOrThrow("discount_amount"));

                    voucherList.add(new Voucher(title, desc, titleColor, bgColor, iconRes, code, amount));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all vouchers", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return voucherList;
    }
    public int getVouchersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        int count = 0;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_VOUCHER, null);
            if (cursor != null) count = cursor.getCount();
        } catch (Exception e) {
            Log.e(TAG, "Error counting vouchers", e);
        } finally {
            if (cursor != null) cursor.close();
        }
        return count;
    }
    // Dán khối code này vào trong class DatabaseHelper

// ===================================================================
// == CÁC HÀM CRUD CHO NOTIFICATION
// ===================================================================

    /**
     * Lấy tất cả thông báo của một khách hàng, sắp xếp theo ngày tạo mới nhất.
     * @param customerId ID của khách hàng.
     * @return Danh sách các đối tượng Notification.
     */
    public List<Notification> getNotificationsByCustomerId(int customerId) {
        List<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    TABLE_NOTIFICATION,
                    null, // Lấy tất cả các cột
                    "customer_id = ?",
                    new String[]{String.valueOf(customerId)},
                    null,
                    null,
                    "created_at DESC" // Sắp xếp mới nhất lên đầu
            );

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int notifId = cursor.getInt(cursor.getColumnIndexOrThrow("notification_id"));
                    int custId = cursor.getInt(cursor.getColumnIndexOrThrow("customer_id"));
                    String message = cursor.getString(cursor.getColumnIndexOrThrow("message"));
                    String createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
                    // is_read trong CSDL là INTEGER (0 hoặc 1), cần chuyển sang boolean
                    boolean isRead = cursor.getInt(cursor.getColumnIndexOrThrow("is_read")) == 1;
                    // order_id trong CSDL là TEXT (mã ngẫu nhiên), có thể null
                    String orderId = cursor.getString(cursor.getColumnIndexOrThrow("order_id"));

                    // Tạo đối tượng Notification và thêm vào danh sách
                    notificationList.add(new Notification(notifId, custId, message, createdAt, isRead, orderId));

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi lấy thông báo theo customer id", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return notificationList;
    }

    /**
     * Đánh dấu một thông báo là đã đọc.
     *
     * @param notificationId ID của thông báo cần cập nhật.
     * @return
     */
    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("is_read", 1); // 1 = đã đọc

            db.update(TABLE_NOTIFICATION, values, "notification_id = ?", new String[]{String.valueOf(notificationId)});
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đánh dấu thông báo đã đọc", e);
        } finally {
        }
        return false;
    }
    // Thêm hàm này vào lớp DatabaseHelper.java của bạn
    public boolean checkAndUpdatePassword(int userId, String oldPassword, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Trước tiên, kiểm tra xem mật khẩu cũ có đúng không
        String[] columns = {"password"};
        String selection = "id = ? AND password = ?";
        String[] selectionArgs = {String.valueOf(userId), oldPassword}; // Nên mã hóa mật khẩu ở đây
        Cursor cursor = db.query("users", columns, selection, selectionArgs, null, null, null);

        int cursorCount = cursor.getCount();
        cursor.close();

        if (cursorCount > 0) {
            // Mật khẩu cũ chính xác, tiến hành cập nhật mật khẩu mới
            ContentValues values = new ContentValues();
            values.put("password", newPassword); // Nên mã hóa mật khẩu mới
            String whereClause = "id = ?";
            String[] whereArgs = {String.valueOf(userId)};
            int rowsAffected = db.update("users", values, whereClause, whereArgs);
            return rowsAffected > 0; // Trả về true nếu cập nhật thành công
        } else {
            // Mật khẩu cũ không đúng
            return false;
        }
    }
    public String placeOrder(int userId, List<CartItem> items, double totalAmount, String address, String paymentMethod, String deliveryTime, String voucherCode, int shippingFee) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        String orderId = null;
        try {
            // 1. Tạo đơn hàng mới trong bảng 'orders'
            ContentValues orderValues = new ContentValues();
            orderId = "OD" + System.currentTimeMillis(); // Tạo mã đơn hàng duy nhất
            orderValues.put("id", orderId);
            orderValues.put("customer_id", userId);
            orderValues.put("total_amount", totalAmount);
            orderValues.put("status", "Pending"); // Trạng thái ban đầu
            orderValues.put("order_date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
            orderValues.put("shipping_address", address);
            orderValues.put("payment_method", paymentMethod);
            orderValues.put("delivery_time", deliveryTime);
            orderValues.put("voucher_code", voucherCode);
            orderValues.put("shipping_fee", shippingFee);

            long result = db.insert("orders", null, orderValues);
            if (result == -1) {
                throw new SQLException("Failed to insert into orders table");
            }

            // 2. Thêm các sản phẩm vào bảng 'order_items'
            for (CartItem item : items) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("order_id", orderId);
                itemValues.put("product_id", item.getProductId());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("price", item.getPrice()); // Giá tại thời điểm mua
                db.insert("order_items", null, itemValues);
            }

            // 3. Xóa các sản phẩm đã đặt hàng khỏi giỏ hàng (cart_items)
            for (CartItem item : items) {
                db.delete("cart_items", "id = ?", new String[]{String.valueOf(item.getOrderItemId())});
            }

            db.setTransactionSuccessful();
            return orderId;
        } catch (Exception e) {
            Log.e("DB_PLACE_ORDER", "Error placing order", e);
            return null;
        } finally {
            db.endTransaction();
        }
    }
    // Trong file DatabaseHelper.java

    public int getCartItemCount(int customerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        int itemCount = 0;

        // Câu truy vấn SQL ĐÚNG:
        // 1. SELECT SUM(oi.quantity): Tính tổng cột quantity từ bảng OrderItem (viết tắt là oi).
        // 2. FROM OrderItem oi: Bảng chính là OrderItem.
        // 3. JOIN Cart c ON oi.cart_id = c.cart_id: Kết nối với bảng Cart (viết tắt là c).
        // 4. WHERE c.customer_id = ?: Lọc theo customer_id từ bảng Cart.
        // 5. AND oi.order_id IS NULL: Rất quan trọng! Chỉ đếm những item chưa thuộc về đơn hàng nào (tức là vẫn còn trong giỏ hàng).
        final String GET_CART_COUNT_QUERY = "SELECT SUM(oi.quantity) FROM " +
                TABLE_ORDER_ITEM + " oi JOIN " + TABLE_CART + " c ON oi.cart_id = c.cart_id " +
                "WHERE c.customer_id = ? AND oi.order_id IS NULL";

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(GET_CART_COUNT_QUERY, new String[]{String.valueOf(customerId)});
            if (cursor != null && cursor.moveToFirst()) {
                // Lấy kết quả từ cột đầu tiên. Nếu giỏ hàng rỗng, SUM() có thể trả về NULL,
                // khi đó cursor.getInt(0) sẽ trả về 0, đúng với mong muốn.
                itemCount = cursor.getInt(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "Lỗi khi đếm số lượng sản phẩm trong giỏ hàng", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // Không cần đóng db ở đây, hãy để hệ thống tự quản lý
        }

        return itemCount;
    }


    // Hàm để lấy toàn bộ thông tin của một khách hàng dựa vào ID
    public Customer getCustomerById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("Customer", null, "customer_id = ?", new String[]{String.valueOf(userId)}, null, null, null);

        Customer customer = null;
        if (cursor != null && cursor.moveToFirst()) {
            customer = new Customer();
            customer.setCustomerId(cursor.getInt(cursor.getColumnIndexOrThrow("customer_id")));
            customer.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            customer.setEmail(cursor.getString(cursor.getColumnIndexOrThrow("email")));
            customer.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            customer.setPhone(cursor.getString(cursor.getColumnIndexOrThrow("phone")));
            customer.setAddress(cursor.getString(cursor.getColumnIndexOrThrow("address")));
            customer.setDob(cursor.getString(cursor.getColumnIndexOrThrow("dob"))); // dob lưu dạng TEXT
            cursor.close();
        }
        return customer;
    }

    // Hàm để cập nhật thông tin cá nhân của khách hàng
    public boolean updateUserProfile(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", customer.getName());
        values.put("phone", customer.getPhone());
        values.put("address", customer.getAddress());
        values.put("dob", customer.getDob());

        // Cập nhật dựa trên customer_id
        int rowsAffected = db.update("Customer", values, "customer_id = ?", new String[]{String.valueOf(customer.getCustomerId())});
        return rowsAffected > 0;
    }
    // Thêm hàm này vào file DatabaseHelper.java
    /**
     * Thêm một đánh giá mới vào cơ sở dữ liệu.
     * @return true nếu thêm thành công, ngược lại là false.
     */
    public boolean addReview(int userId, int productId, String orderId, float rating, String comment) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("customer_id", userId);
        values.put("product_id", productId);
        values.put("order_id", orderId);
        values.put("rating", rating);
        values.put("comment", comment);

        // Thêm ngày tạo review tự động
        String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        values.put("review_date", currentDate);

        long result = db.insert("reviews", null, values);

        // db.insert trả về -1 nếu có lỗi
        return result != -1;
    }


}