package com.example.newsapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

// Main Response Wrapper
public class NewsResponse {
    @SerializedName("totalArticles")
    public int totalArticles;
    
    @SerializedName("articles")
    public List<Article> articles;
}
