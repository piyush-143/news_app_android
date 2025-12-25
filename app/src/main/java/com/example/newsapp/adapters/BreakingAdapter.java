package com.example.newsapp.adapters;

import android.annotation.SuppressLint;
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

import java.util.List;

public class BreakingAdapter extends RecyclerView.Adapter<BreakingAdapter.ViewHolder> {

    private final Context context;
    private final List<Article> articles;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Article article);
    }

    public BreakingAdapter(Context context, List<Article> articles, OnItemClickListener listener) {
        this.context = context;
        this.articles = articles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_breaking, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Flutter logic reversed the list: newsProvider.breakingNews!.articles.length - 1 - index
        // We replicate that here to match the Flutter behavior exactly.
        int reversedIndex = articles.size() - 1 - position;
        Article article = articles.get(reversedIndex);

        holder.tvTitle.setText(article.title);

        String sourceName = article.source != null ? article.source.name : "";
        holder.tvDesc.setText("Detailed coverage regarding " + sourceName.toUpperCase() + "...");

        Glide.with(context)
                .load(article.image)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgBreaking);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(article));
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageView imgBreaking;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            imgBreaking = itemView.findViewById(R.id.imgBreaking);
        }
    }
}