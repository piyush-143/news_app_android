package com.example.newsapp.ui.home;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.newsapp.R;
import com.example.newsapp.models.Article;
import com.example.newsapp.utils.AppConstants;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        // Handle Back Button Click
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Retrieve object
        Article article = (Article) getIntent().getSerializableExtra("article");

        ImageView imgHeader = findViewById(R.id.imgHeader);
        TextView tvSource = findViewById(R.id.tvSource); // Added
        TextView tvDate = findViewById(R.id.tvDate);     // Added
        TextView tvTitle = findViewById(R.id.tvTitle);
        TextView tvDesc = findViewById(R.id.tvDesc);
        TextView tvContent = findViewById(R.id.tvContent);
        Button btnReadMore = findViewById(R.id.btnReadMore);

        if (article != null) {
            Glide.with(this)
                    .load(article.image)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imgHeader);

            tvSource.setText(article.source != null ? article.source.name : "");
            tvDate.setText(AppConstants.formatDate(article.publishedAt));
            tvTitle.setText(article.title);
            tvDesc.setText(article.description);
            tvContent.setText(article.getCleanContent());

            btnReadMore.setOnClickListener(v -> {
                // Use WebViewActivity for internal browser experience
                Intent intent = new Intent(DetailActivity.this, WebViewActivity.class);
                intent.putExtra("url", article.url);
                intent.putExtra("title", article.title);
                startActivity(intent);
            });
        }
    }
}