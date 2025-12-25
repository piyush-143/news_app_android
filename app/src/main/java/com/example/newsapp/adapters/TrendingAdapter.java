package com.example.newsapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.models.Article;
import com.example.newsapp.utils.AppConstants;

import java.util.List;

public class TrendingAdapter extends RecyclerView.Adapter<TrendingAdapter.ViewHolder> {

    private final Context context;
    private final List<Article> articles;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public TrendingAdapter(Context context, List<Article> articles, OnItemClickListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trending, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Article article = articles.get(position);

        // Display Ranking Index (1-based)
        holder.tvIndex.setText("#" + (position + 1));

        holder.tvTitle.setText(article.title);
        holder.tvSource.setText(article.source.name);
        holder.tvDate.setText(AppConstants.formatDate(article.publishedAt));

        Glide.with(context)
                .load(article.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgThumbnail);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvTitle, tvSource, tvDate;
        ImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSource = itemView.findViewById(R.id.tvSource);
            tvDate = itemView.findViewById(R.id.tvDate);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}