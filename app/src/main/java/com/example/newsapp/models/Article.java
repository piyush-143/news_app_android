package com.example.newsapp.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

// Article Model
public class Article implements Serializable {
    @SerializedName("title")
    public String title;
    
    @SerializedName("description")
    public String description;
    
    @SerializedName("content")
    public String content;
    
    @SerializedName("url")
    public String url;
    
    @SerializedName("image")
    public String image;
    
    @SerializedName("publishedAt")
    public String publishedAt;
    
    @SerializedName("source")
    public Source source;

    public String getCleanContent() {
        if (content == null) return "";
        // Regex to remove [ +1234 chars ]
        return content.replaceAll("\\s*\\[.*chars?\\]$", "");
    }
}

