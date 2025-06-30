package com.finalterm.cakeeateasy.connectors;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.finalterm.cakeeateasy.models.Customer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Ngoc_test_login_signup.sqlite";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG = "DatabaseHelper";

    // Table name
    public static final String TABLE_CUSTOMER = "CUSTOMER";

    // Column names
    public static final String COLUMN_CUSTOMER_ID = "CustomerID";
    public static final String COLUMN_CUSTOMER_USERNAME = "CustomerUsername";
    public static final String COLUMN_CUSTOMER_PASSWORD = "CustomerPassword";
    public static final String COLUMN_CUSTOMER_EMAIL = "CustomerEmail";
    public static final String COLUMN_CUSTOMER_PHONE = "CustomerPhoneNumber";
    public static final String COLUMN_CUSTOMER_DOB = "CustomerDOB";

    // --- ORDERS TABLE ---
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_INVOICE_ID = "invoice_id";
    public static final String COLUMN_ORDER_CUSTOMER_NAME = "customer_name";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    public static final String COLUMN_ORDER_STATUS = "status";

    // --- ADDRESSES TABLE ---
    public static final String TABLE_ADDRESSES = "addresses";
    public static final String COLUMN_ADDRESS_ID = "id";
    public static final String COLUMN_ADDRESS_NAME = "name";
    public static final String COLUMN_ADDRESS_PHONE = "phone";
    public static final String COLUMN_ADDRESS_LINE = "address_line";
    public static final String COLUMN_ADDRESS_IS_DEFAULT = "is_default";

    private final Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

        // Ensure the database exists and is up to date.
        if (!checkDatabase()) {
            copyDatabase();
        }
        // Getting a readable or writable database will trigger onCreate or onUpgrade if necessary.
        getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method is only called if the database file did not exist and was just created.
        // In our case, we copy it from assets, so this is more of a fallback.
        // The table creation SQL from onUpgrade should be here too in a real scenario
        // where the app doesn't ship with a pre-made DB.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            String CREATE_ADDRESSES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_ADDRESSES + " ("
                    + COLUMN_ADDRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_ADDRESS_NAME + " TEXT NOT NULL,"
                    + COLUMN_ADDRESS_PHONE + " TEXT NOT NULL,"
                    + COLUMN_ADDRESS_LINE + " TEXT NOT NULL,"
                    + COLUMN_ADDRESS_IS_DEFAULT + " INTEGER NOT NULL DEFAULT 0"
                    + ");";
            db.execSQL(CREATE_ADDRESSES_TABLE);
        }
    }

    private void copyDatabase() {
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();

            File dbFile = new File(outFileName);
            if (!dbFile.getParentFile().exists()) {
                dbFile.getParentFile().mkdirs();
            }

            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            Log.d(TAG, "Database copied successfully");
        } catch (IOException e) {
            Log.e(TAG, "Error copying database: " + e.getMessage());
            // It's a good practice to throw a runtime exception here to crash the app,
            // as the app cannot function without the database.
            throw new RuntimeException("Error copying database", e);
        }
    }

    private boolean checkDatabase() {
        return context.getDatabasePath(DATABASE_NAME).exists();
    }

    /**
     * Check if username and password match in database
     * @param username the username to check
     * @param password the password to check
     * @return true if credentials are valid, false otherwise
     */
    public boolean checkUserCredentials(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CUSTOMER_ID};
        String selection = COLUMN_CUSTOMER_USERNAME + " = ? AND " + COLUMN_CUSTOMER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        Cursor cursor = db.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        // db.close(); // The helper manages the connection
        return count > 0;
    }

    /**
     * Get customer information by username
     * @param username the username to search for
     * @return Customer object or null if not found
     */
    public Customer getCustomerByUsername(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Customer customer = null;
        Cursor cursor = db.query(TABLE_CUSTOMER, null, COLUMN_CUSTOMER_USERNAME + " = ?", new String[]{username}, null, null, null);
        if (cursor.moveToFirst()) {
            customer = new Customer();
            customer.setCustomerID(cursor.getInt(cursor.getColumnIndex(COLUMN_CUSTOMER_ID)));
            customer.setCustomerUsername(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_USERNAME)));
            customer.setCustomerPassword(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_PASSWORD)));
            customer.setCustomerEmail(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_EMAIL)));
            customer.setCustomerPhoneNumber(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_PHONE)));
            customer.setCustomerDOB(cursor.getString(cursor.getColumnIndex(COLUMN_CUSTOMER_DOB)));
        }
        cursor.close();
        return customer;
    }

    /**
     * Check if username already exists
     * @param username the username to check
     * @return true if username exists, false otherwise
     */
    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_CUSTOMER_ID};
        String selection = COLUMN_CUSTOMER_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    /**
     * Add new customer to database
     * @param customer the customer object to add
     * @return the ID of the newly inserted customer, or -1 if failed
     */
    public long addCustomer(Customer customer) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_USERNAME, customer.getCustomerUsername());
        values.put(COLUMN_CUSTOMER_PASSWORD, customer.getCustomerPassword());
        values.put(COLUMN_CUSTOMER_EMAIL, customer.getCustomerEmail());
        values.put(COLUMN_CUSTOMER_PHONE, customer.getCustomerPhoneNumber());
        values.put(COLUMN_CUSTOMER_DOB, customer.getCustomerDOB());
        long id = db.insert(TABLE_CUSTOMER, null, values);
        return id;
    }

    /**
     * Insert a new order and return the invoice_id
     */
    public long insertOrder(String customerName, String orderDate, double totalAmount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_CUSTOMER_NAME, customerName);
        values.put(COLUMN_ORDER_DATE, orderDate);
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_ORDER_STATUS, status);
        // Ensure orders table exists. This is a failsafe.
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + " (invoice_id INTEGER PRIMARY KEY AUTOINCREMENT, customer_name TEXT, order_date TEXT, total_amount REAL, status TEXT)");
        long invoiceId = db.insert(TABLE_ORDERS, null, values);
        return invoiceId;
    }

    public long addAddress(com.finalterm.cakeeateasy.models.Address address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS_NAME, address.getName());
        values.put(COLUMN_ADDRESS_PHONE, address.getPhone());
        values.put(COLUMN_ADDRESS_LINE, address.getAddressLine());
        values.put(COLUMN_ADDRESS_IS_DEFAULT, address.isDefault() ? 1 : 0);
        return db.insert(TABLE_ADDRESSES, null, values);
    }

    public int updateAddress(com.finalterm.cakeeateasy.models.Address address) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ADDRESS_NAME, address.getName());
        values.put(COLUMN_ADDRESS_PHONE, address.getPhone());
        values.put(COLUMN_ADDRESS_LINE, address.getAddressLine());
        values.put(COLUMN_ADDRESS_IS_DEFAULT, address.isDefault() ? 1 : 0);
        return db.update(TABLE_ADDRESSES, values, COLUMN_ADDRESS_ID + " = ?", new String[]{String.valueOf(address.getId())});
    }

    public void deleteAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ADDRESSES, COLUMN_ADDRESS_ID + " = ?", new String[]{String.valueOf(addressId)});
    }

    public java.util.ArrayList<com.finalterm.cakeeateasy.models.Address> getAllAddresses() {
        java.util.ArrayList<com.finalterm.cakeeateasy.models.Address> addresses = new java.util.ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ADDRESSES, null, null, null, null, null, COLUMN_ADDRESS_IS_DEFAULT + " DESC");

        if (cursor.moveToFirst()) {
            do {
                com.finalterm.cakeeateasy.models.Address address = new com.finalterm.cakeeateasy.models.Address();
                address.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ADDRESS_ID)));
                address.setName(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS_NAME)));
                address.setPhone(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS_PHONE)));
                address.setAddressLine(cursor.getString(cursor.getColumnIndex(COLUMN_ADDRESS_LINE)));
                address.setDefault(cursor.getInt(cursor.getColumnIndex(COLUMN_ADDRESS_IS_DEFAULT)) == 1);
                addresses.add(address);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return addresses;
    }

    public void setDefaultAddress(int addressId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues unsetValue = new ContentValues();
            unsetValue.put(COLUMN_ADDRESS_IS_DEFAULT, 0);
            db.update(TABLE_ADDRESSES, unsetValue, null, null);

            ContentValues setValue = new ContentValues();
            setValue.put(COLUMN_ADDRESS_IS_DEFAULT, 1);
            db.update(TABLE_ADDRESSES, setValue, COLUMN_ADDRESS_ID + " = ?", new String[]{String.valueOf(addressId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }
} 