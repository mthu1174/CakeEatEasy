package com.finalterm.cakeeateasy.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.finalterm.cakeeateasy.databinding.ItemBannerBinding;
import com.finalterm.cakeeateasy.models.BannerItem;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    private final List<BannerItem> bannerItems;

    public BannerAdapter(List<BannerItem> bannerItems) { this.bannerItems = bannerItems; }

    @NonNull @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerItem currentItem = bannerItems.get(position);
        holder.binding.imgBannerBackground.setImageResource(currentItem.getImageResId());
        holder.binding.txtBannerTitle.setText(currentItem.getTitle());
        holder.binding.btnBannerAction.setText(currentItem.getButtonText());
    }

    @Override
    public int getItemCount() { return bannerItems.size(); }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ItemBannerBinding binding;
        public BannerViewHolder(ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
