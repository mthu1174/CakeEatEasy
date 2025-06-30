package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.AddressAdapter;
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Address;

import java.util.ArrayList;

public class AddressActivity extends AppCompatActivity implements AddressAdapter.OnAddressActionsListener {

    private RecyclerView recyclerView;
    private AddressAdapter addressAdapter;
    private ArrayList<Address> addressList;
    private DatabaseHelper dbHelper;
    private ImageView btnBack;
    private LinearLayout btnAddNewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        dbHelper = new DatabaseHelper(this);

        btnBack = findViewById(R.id.btn_back);
        btnAddNewAddress = findViewById(R.id.btn_add_new_address);
        recyclerView = findViewById(R.id.rv_addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadAddresses();

        btnBack.setOnClickListener(v -> {
            // Find the default address to return it
            Address defaultAddress = null;
            for (Address address : addressList) {
                if (address.isDefault()) {
                    defaultAddress = address;
                    break;
                }
            }

            Intent resultIntent = new Intent();
            if (defaultAddress != null) {
                resultIntent.putExtra("selected_address", defaultAddress);
                setResult(RESULT_OK, resultIntent);
            } else {
                setResult(RESULT_CANCELED, resultIntent);
            }
            finish();
        });
        btnAddNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAddresses();
    }

    private void loadAddresses() {
        addressList = dbHelper.getAllAddresses();
        addressAdapter = new AddressAdapter(this, addressList, this);
        recyclerView.setAdapter(addressAdapter);
    }

    @Override
    public void onEditClick(Address address) {
        Intent intent = new Intent(this, EditAddressActivity.class);
        intent.putExtra("address_to_edit", address);
        startActivity(intent);
    }

    @Override
    public void onSetDefaultClick(Address address) {
        dbHelper.setDefaultAddress(address.getId());
        // Update the list to reflect the change for the result
        for(Address adr : addressList) {
            adr.setDefault(adr.getId() == address.getId());
        }
    }
}