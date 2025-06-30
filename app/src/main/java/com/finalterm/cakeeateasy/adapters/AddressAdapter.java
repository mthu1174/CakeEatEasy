package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private final Context context;
    private final List<Address> addressList;
    private final OnAddressActionsListener listener;
    private int selectedPosition = -1;

    public interface OnAddressActionsListener {
        void onEditClick(Address address);
        void onSetDefaultClick(Address address);
    }

    public AddressAdapter(Context context, List<Address> addressList, OnAddressActionsListener listener) {
        this.context = context;
        this.addressList = addressList;
        this.listener = listener;
        for (int i = 0; i < addressList.size(); i++) {
            if (addressList.get(i).isDefault()) {
                selectedPosition = i;
                break;
            }
        }
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.name.setText(address.getName());
        holder.phone.setText(address.getPhone());
        holder.addressLine.setText(address.getAddressLine());
        holder.defaultTag.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);
        holder.radioButton.setChecked(position == selectedPosition);

        holder.edit.setOnClickListener(v -> {
            try {
                if (listener != null) {
                    listener.onEditClick(address);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        holder.radioButton.setOnClickListener(v -> {
            if (position != selectedPosition) {
                if (selectedPosition != -1) {
                    addressList.get(selectedPosition).setDefault(false);
                    notifyItemChanged(selectedPosition);
                }
                selectedPosition = position;
                address.setDefault(true);
                notifyItemChanged(selectedPosition);
                listener.onSetDefaultClick(address);
            }
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView name, phone, addressLine, defaultTag, edit;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.rb_address);
            name = itemView.findViewById(R.id.tv_name);
            phone = itemView.findViewById(R.id.tv_phone);
            addressLine = itemView.findViewById(R.id.tv_address_line);
            defaultTag = itemView.findViewById(R.id.tv_default_tag);

            edit = itemView.findViewById(R.id.tv_edit);
        }
    }
} 