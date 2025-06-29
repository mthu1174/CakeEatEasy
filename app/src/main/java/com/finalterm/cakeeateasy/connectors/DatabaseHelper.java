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
    private static final int DATABASE_VERSION = 1;
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

    private Context context;
    private SQLiteDatabase database;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // This method won't be called since we're copying from assets
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades if needed
    }

    /**
     * Copy database from assets folder
     */
    public void copyDatabase() {
        try {
            InputStream inputStream = context.getAssets().open(DATABASE_NAME);
            String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();
            
            // Create the database directory if it doesn't exist
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
        }
    }

    /**
     * Open database
     */
    public void openDatabase() {
        String dbPath = context.getDatabasePath(DATABASE_NAME).getPath();
        if (database != null && database.isOpen()) {
            return;
        }
        database = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
     * Close database
     */
    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    /**
     * Check if database exists
     */
    public boolean checkDatabase() {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        return dbFile.exists();
    }

    /**
     * Check if username and password match in database
     * @param username the username to check
     * @param password the password to check
     * @return true if credentials are valid, false otherwise
     */
    public boolean checkUserCredentials(String username, String password) {
        openDatabase();
        
        String[] columns = {COLUMN_CUSTOMER_ID};
        String selection = COLUMN_CUSTOMER_USERNAME + " = ? AND " + COLUMN_CUSTOMER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};
        
        Cursor cursor = database.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        
        return count > 0;
    }

    /**
     * Get customer information by username
     * @param username the username to search for
     * @return Customer object or null if not found
     */
    public Customer getCustomerByUsername(String username) {
        openDatabase();
        
        String[] columns = {
                COLUMN_CUSTOMER_ID,
                COLUMN_CUSTOMER_USERNAME,
                COLUMN_CUSTOMER_PASSWORD,
                COLUMN_CUSTOMER_EMAIL,
                COLUMN_CUSTOMER_PHONE,
                COLUMN_CUSTOMER_DOB
        };
        String selection = COLUMN_CUSTOMER_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = database.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);
        
        Customer customer = null;
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
        openDatabase();
        
        String[] columns = {COLUMN_CUSTOMER_ID};
        String selection = COLUMN_CUSTOMER_USERNAME + " = ?";
        String[] selectionArgs = {username};
        
        Cursor cursor = database.query(TABLE_CUSTOMER, columns, selection, selectionArgs, null, null, null);
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
        openDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_USERNAME, customer.getCustomerUsername());
        values.put(COLUMN_CUSTOMER_PASSWORD, customer.getCustomerPassword());
        values.put(COLUMN_CUSTOMER_EMAIL, customer.getCustomerEmail());
        values.put(COLUMN_CUSTOMER_PHONE, customer.getCustomerPhoneNumber());
        values.put(COLUMN_CUSTOMER_DOB, customer.getCustomerDOB());
        
        return database.insert(TABLE_CUSTOMER, null, values);
    }
} 