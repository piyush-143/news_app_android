package com.example.newsapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.adapters.BreakingAdapter;
import com.example.newsapp.models.Article;
import com.example.newsapp.models.NewsResponse;
import com.example.newsapp.network.NewsApiClient;
import com.example.newsapp.utils.AppConstants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BreakingFragment extends Fragment {

    RecyclerView recyclerView;
    BreakingAdapter adapter;

    // State Views
    ProgressBar progressBar;
    View contentView;
    LinearLayout errorView;
    TextView tvError;
    Button btnRetry;

    List<Article> articles = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_breaking, container, false);

        initViews(view);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BreakingAdapter(getContext(), articles, article -> {
            Intent intent = new Intent(getContext(), DetailActivity.class);
            intent.putExtra("article", article);
            startActivity(intent);
        });
        recyclerView.setAdapter(adapter);

        btnRetry.setOnClickListener(v -> fetchBreakingNews());

        fetchBreakingNews();

        return view;
    }

    private void initViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        contentView = view.findViewById(R.id.contentView);
        recyclerView = view.findViewById(R.id.recycler_breaking);
        errorView = view.findViewById(R.id.errorView);
        tvError = view.findViewById(R.id.tvError);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    private void fetchBreakingNews() {
        showLoadingState();

        // Using "search" endpoint for specific query "breaking"
        NewsApiClient.getInterface().getSearchNews(
                "breaking", "en", "in", AppConstants.getApiKey()
        ).enqueue(new Callback<NewsResponse>() {
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().articles != null) {
                    articles.clear();
                    articles.addAll(response.body().articles);
                    if (articles.isEmpty()) {
                        showErrorState("No breaking news found.");
                    } else {
                        adapter.notifyDataSetChanged();
                        showContentState();
                    }
                } else {
                    // Show detailed error explanation based on status code
                    showErrorState(getErrorMessage(response.code()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                showErrorState("Network Error\nPlease check your internet connection.");
            }
        });
    }

    // --- Helpers ---

    private void showLoadingState() {
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }

    private void showContentState() {
        progressBar.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        contentView.setVisibility(View.VISIBLE);
    }

    private void showErrorState(String message) {
        progressBar.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        tvError.setText(message);
    }

    private String getErrorMessage(int statusCode) {
        switch (statusCode) {
            case 400: return "Bad Request (400)\nThe server could not understand the request.";
            case 401: return "Unauthorized (401)\nInvalid API Key. Please check credentials.";
            case 403: return "Forbidden (403)\nAccess denied or API limit reached.";
            case 404: return "Not Found (404)\nThe requested resource was not found.";
            case 429: return "Limit Reached (429)\nToo many requests. Please wait a moment.";
            case 500: return "Server Error (500)\nSomething went wrong on our end.";
            case 502: return "Bad Gateway (502)\nInvalid response from upstream.";
            case 503: return "Service Unavailable (503)\nThe server is overloaded.";
            default: return "Unexpected Error (" + statusCode + ")\nPlease try again later.";
        }
    }
}