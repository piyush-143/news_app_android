package com.example.newsapp.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

// Source Model
public class Source implements Serializable {
    @SerializedName("name")
    public String name;
    
    @SerializedName("url")
    public String url;
}
