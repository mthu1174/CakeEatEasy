package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);

        holder.txtUserName.setText(review.getUserName());
        holder.txtComment.setText(review.getComment());
        holder.txtDate.setText(review.getDate());
        holder.ratingBar.setRating(review.getRating());

        Glide.with(context)
                .load(review.getUserAvatarUrl())
                .placeholder(R.drawable.ic_avatar) // Ảnh mặc định
                .error(R.drawable.ic_avatar)       // Ảnh khi lỗi
                .circleCrop() // Bo tròn ảnh
                .into(holder.imgAvatar);
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    // Hàm để cập nhật dữ liệu cho adapter
    public void updateReviews(List<Review> newReviews) {
        this.reviewList.clear();
        this.reviewList.addAll(newReviews);
        notifyDataSetChanged();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtUserName, txtComment, txtDate;
        RatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.img_review_avatar);
            txtUserName = itemView.findViewById(R.id.txt_review_user_name);
            txtComment = itemView.findViewById(R.id.txt_review_comment);
            txtDate = itemView.findViewById(R.id.txt_review_date);
            ratingBar = itemView.findViewById(R.id.rating_bar_review);
        }
    }
}