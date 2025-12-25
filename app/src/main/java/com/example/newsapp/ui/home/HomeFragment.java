package com.example.newsapp.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.newsapp.R;
import com.example.newsapp.adapters.CategoryAdapter;
import com.example.newsapp.adapters.FeaturedAdapter;
import com.example.newsapp.adapters.NewsAdapter;
import com.example.newsapp.models.Article;
import com.example.newsapp.models.NewsResponse;
import com.example.newsapp.network.NewsApiClient;

import com.example.newsapp.utils.AppConstants;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    // Featured
    View contentFeatured, loaderFeatured, errorFeatured;
    TextView tvErrorFeatured;
    Button btnRetryFeatured;
    TextView btnSeeAllFeatured;
    ViewPager2 viewPagerFeatured;
    DotsIndicator dotsIndicator;
    FeaturedAdapter featuredAdapter;
    List<Article> featuredArticles = new ArrayList<>();
    List<Article> fullFeaturedArticles = new ArrayList<>(); // Store full list

    // Recent
    TextView tvRecentHeader;
    TextView btnSeeAllRecent;
    RecyclerView recyclerRecent;
    View loaderRecent, errorRecent;
    TextView tvErrorRecent;
    Button btnRetryRecent;
    NewsAdapter recentAdapter;
    List<Article> recentArticles = new ArrayList<>();
    List<Article> fullRecentArticles = new ArrayList<>(); // Store full list

    // Nation
    LinearLayout layoutNation;
    TextView btnSeeAllNation;
    RecyclerView recyclerNation;
    View loaderNation, errorNation;
    TextView tvErrorNation;
    Button btnRetryNation;
    NewsAdapter nationAdapter;
    List<Article> nationArticles = new ArrayList<>();
    List<Article> fullNationArticles = new ArrayList<>(); // Store full list

    // World
    LinearLayout layoutWorld;
    TextView btnSeeAllWorld;
    RecyclerView recyclerWorld;
    View loaderWorld, errorWorld;
    TextView tvErrorWorld;
    Button btnRetryWorld;
    NewsAdapter worldAdapter;
    List<Article> worldArticles = new ArrayList<>();
    List<Article> fullWorldArticles = new ArrayList<>(); // Store full list

    // Categories
    RecyclerView recyclerCategories;
    CategoryAdapter categoryAdapter;
    List<String> categoryList = Arrays.asList("All", "Tech", "Health", "Science", "Gaming", "Business", "Entertainment", "Sports");

    // Auto-Scroll Logic
    private final Handler sliderHandler = new Handler(Looper.getMainLooper());
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            if (viewPagerFeatured.getAdapter() != null && !featuredArticles.isEmpty()) {
                int currentItem = viewPagerFeatured.getCurrentItem();
                int totalItems = featuredAdapter.getItemCount();
                int nextItem = (currentItem + 1) % totalItems;
                viewPagerFeatured.setCurrentItem(nextItem, true);
            }
            sliderHandler.postDelayed(this, 3000);
        }
    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupAdapters();

        // Initial Load
        fetchFeaturedNews();
        onCategorySelected("All"); // Loads Recent, Nation, World

        return view;
    }

    private void initViews(View view) {
        // Categories
        recyclerCategories = view.findViewById(R.id.recycler_categories);

        // Featured
        contentFeatured = view.findViewById(R.id.content_featured);
        loaderFeatured = view.findViewById(R.id.loader_featured);
        errorFeatured = view.findViewById(R.id.error_featured);
        tvErrorFeatured = view.findViewById(R.id.tv_error_featured);
        btnRetryFeatured = view.findViewById(R.id.btn_retry_featured);
        btnSeeAllFeatured = view.findViewById(R.id.btn_see_all_featured);
        viewPagerFeatured = view.findViewById(R.id.viewPagerFeatured);
        dotsIndicator = view.findViewById(R.id.dotsIndicator);

        // Recent
        tvRecentHeader = view.findViewById(R.id.tvRecentHeader);
        btnSeeAllRecent = view.findViewById(R.id.btn_see_all_recent);
        recyclerRecent = view.findViewById(R.id.recycler_recent);
        loaderRecent = view.findViewById(R.id.loader_recent);
        errorRecent = view.findViewById(R.id.error_recent);
        tvErrorRecent = view.findViewById(R.id.tv_error_recent);
        btnRetryRecent = view.findViewById(R.id.btn_retry_recent);

        // Nation
        layoutNation = view.findViewById(R.id.layoutNation);
        btnSeeAllNation = view.findViewById(R.id.btn_see_all_nation);
        recyclerNation = view.findViewById(R.id.recycler_nation);
        loaderNation = view.findViewById(R.id.loader_nation);
        errorNation = view.findViewById(R.id.error_nation);
        tvErrorNation = view.findViewById(R.id.tv_error_nation);
        btnRetryNation = view.findViewById(R.id.btn_retry_nation);

        // World
        layoutWorld = view.findViewById(R.id.layoutWorld);
        btnSeeAllWorld = view.findViewById(R.id.btn_see_all_world);
        recyclerWorld = view.findViewById(R.id.recycler_world);
        loaderWorld = view.findViewById(R.id.loader_world);
        errorWorld = view.findViewById(R.id.error_world);
        tvErrorWorld = view.findViewById(R.id.tv_error_world);
        btnRetryWorld = view.findViewById(R.id.btn_retry_world);
    }

    private void setupAdapters() {
        // Categories
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(getContext(), categoryList, this::onCategorySelected);
        recyclerCategories.setAdapter(categoryAdapter);

        // Featured
        featuredAdapter = new FeaturedAdapter(getContext(), featuredArticles, this::openDetail);
        viewPagerFeatured.setAdapter(featuredAdapter);
        dotsIndicator.setViewPager2(viewPagerFeatured);

        viewPagerFeatured.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 3000);
            }
        });

        // Lists
        recyclerRecent.setLayoutManager(new LinearLayoutManager(getContext()));
        recentAdapter = new NewsAdapter(getContext(), recentArticles, this::openDetail);
        recyclerRecent.setAdapter(recentAdapter);

        recyclerNation.setLayoutManager(new LinearLayoutManager(getContext()));
        nationAdapter = new NewsAdapter(getContext(), nationArticles, this::openDetail);
        recyclerNation.setAdapter(nationAdapter);

        recyclerWorld.setLayoutManager(new LinearLayoutManager(getContext()));
        worldAdapter = new NewsAdapter(getContext(), worldArticles, this::openDetail);
        recyclerWorld.setAdapter(worldAdapter);

        // Retry Listeners
        btnRetryFeatured.setOnClickListener(v -> fetchFeaturedNews());
        btnRetryRecent.setOnClickListener(v -> onCategorySelected(categoryAdapter.getSelectedCategory()));
        btnRetryNation.setOnClickListener(v -> fetchCategoryNews("nation", nationArticles, fullNationArticles, nationAdapter, recyclerNation, loaderNation, errorNation, tvErrorNation, btnRetryNation, btnSeeAllNation));
        btnRetryWorld.setOnClickListener(v -> fetchCategoryNews("world", worldArticles, fullWorldArticles, worldAdapter, recyclerWorld, loaderWorld, errorWorld, tvErrorWorld, btnRetryWorld, btnSeeAllWorld));

        // See All Listeners
        btnSeeAllFeatured.setOnClickListener(v -> openSeeAll("Featured News", fullFeaturedArticles));
        btnSeeAllRecent.setOnClickListener(v -> openSeeAll(tvRecentHeader.getText().toString(), fullRecentArticles));
        btnSeeAllNation.setOnClickListener(v -> openSeeAll("Nation News", fullNationArticles));
        btnSeeAllWorld.setOnClickListener(v -> openSeeAll("World News", fullWorldArticles));
    }

    private void openDetail(Article article) {
        Intent intent = new Intent(getContext(), DetailActivity.class);
        intent.putExtra("article", article);
        startActivity(intent);
    }

    private void openSeeAll(String title, List<Article> list) {
        Intent intent = new Intent(getContext(), SeeAllActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("articles", (Serializable) list);
        startActivity(intent);
    }

    @SuppressLint("SetTextI18n")
    private void onCategorySelected(String category) {
        if (category.equals("All")) {
            tvRecentHeader.setText("Recent News");
            layoutNation.setVisibility(View.VISIBLE);
            layoutWorld.setVisibility(View.VISIBLE);

            fetchCategoryNews("general", recentArticles, fullRecentArticles, recentAdapter, recyclerRecent, loaderRecent, errorRecent, tvErrorRecent, btnRetryRecent, btnSeeAllRecent);
            fetchCategoryNews("nation", nationArticles, fullNationArticles, nationAdapter, recyclerNation, loaderNation, errorNation, tvErrorNation, btnRetryNation, btnSeeAllNation);
            fetchCategoryNews("world", worldArticles, fullWorldArticles, worldAdapter, recyclerWorld, loaderWorld, errorWorld, tvErrorWorld, btnRetryWorld, btnSeeAllWorld);
        } else {
            tvRecentHeader.setText(category + " News");
            layoutNation.setVisibility(View.GONE);
            layoutWorld.setVisibility(View.GONE);

            String apiCategory = category.toLowerCase();
            if (category.equals("Gaming")) {
                fetchSearchNews(apiCategory, recentArticles, fullRecentArticles, recentAdapter, recyclerRecent, loaderRecent, errorRecent, tvErrorRecent, btnRetryRecent, btnSeeAllRecent);
            } else {
                fetchCategoryNews(apiCategory, recentArticles, fullRecentArticles, recentAdapter, recyclerRecent, loaderRecent, errorRecent, tvErrorRecent, btnRetryRecent, btnSeeAllRecent);
            }
        }
    }

    // --- Network Calls ---

    private void fetchFeaturedNews() {
        showLoadingState(contentFeatured, loaderFeatured, errorFeatured, btnSeeAllFeatured);
        NewsApiClient.getInterface().getSearchNews(
                "featured", "en", "in", AppConstants.getApiKey()
        ).enqueue(new Callback<NewsResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().articles != null) {
                    featuredArticles.clear();
                    fullFeaturedArticles.clear();
                    List<Article> all = response.body().articles;
                    fullFeaturedArticles.addAll(all); // Store Full List

                    if (!all.isEmpty()) {
                        featuredArticles.addAll(all.subList(0, Math.min(all.size(), 5)));
                        featuredAdapter.notifyDataSetChanged();
                        showContentState(contentFeatured, loaderFeatured, errorFeatured, btnSeeAllFeatured);
                        sliderHandler.postDelayed(sliderRunnable, 3000);
                    } else {
                        showErrorState("No featured news available", contentFeatured, loaderFeatured, errorFeatured, tvErrorFeatured, btnSeeAllFeatured);
                    }
                } else {
                    showErrorState(getErrorMessage(response.code()), contentFeatured, loaderFeatured, errorFeatured, tvErrorFeatured, btnSeeAllFeatured);
                }
            }
            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                showErrorState("Network Error\nPlease check your internet connection.", contentFeatured, loaderFeatured, errorFeatured, tvErrorFeatured, btnSeeAllFeatured);
            }
        });
    }

    private void fetchCategoryNews(String category, List<Article> displayList, List<Article> fullList, NewsAdapter adapter,
                                   View content, View loader, View errorView, TextView tvError, Button btnRetry, TextView btnSeeAll) {

        showLoadingState(content, loader, errorView, btnSeeAll);
        String specificKey = AppConstants.getApiKeyForCategory(category);

        NewsApiClient.getInterface().getCategoryNews(
                category, "en", "in", specificKey
        ).enqueue(new Callback<NewsResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().articles != null) {
                    displayList.clear();
                    fullList.clear();
                    List<Article> all = response.body().articles;
                    fullList.addAll(all); // Store Full List

                    if (!all.isEmpty()) {
                        displayList.addAll(all.subList(0, Math.min(all.size(), 5)));
                        adapter.notifyDataSetChanged();
                        showContentState(content, loader, errorView, btnSeeAll);
                    } else {
                        showErrorState("No articles found for " + category, content, loader, errorView, tvError, btnSeeAll);
                    }
                } else {
                    showErrorState(getErrorMessage(response.code()), content, loader, errorView, tvError, btnSeeAll);
                }
            }
            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                showErrorState("Network Error\nPlease check your internet connection.", content, loader, errorView, tvError, btnSeeAll);
            }
        });
    }

    private void fetchSearchNews(String query, List<Article> displayList, List<Article> fullList, NewsAdapter adapter,
                                 View content, View loader, View errorView, TextView tvError, Button btnRetry, TextView btnSeeAll) {
        showLoadingState(content, loader, errorView, btnSeeAll);
        NewsApiClient.getInterface().getSearchNews(
                query, "en", "in", AppConstants.getApiKey()
        ).enqueue(new Callback<NewsResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<NewsResponse> call, @NonNull Response<NewsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().articles != null) {
                    displayList.clear();
                    fullList.clear();
                    List<Article> all = response.body().articles;
                    fullList.addAll(all); // Store Full List

                    if (!all.isEmpty()) {
                        displayList.addAll(all.subList(0, Math.min(all.size(), 5)));
                        adapter.notifyDataSetChanged();
                        showContentState(content, loader, errorView, btnSeeAll);
                    } else {
                        showErrorState("No results found", content, loader, errorView, tvError, btnSeeAll);
                    }
                } else {
                    showErrorState(getErrorMessage(response.code()), content, loader, errorView, tvError, btnSeeAll);
                }
            }
            @Override
            public void onFailure(@NonNull Call<NewsResponse> call, @NonNull Throwable t) {
                showErrorState("Network Error\nPlease check your internet connection.", content, loader, errorView, tvError, btnSeeAll);
            }
        });
    }

    // --- State Helpers ---
    private void showLoadingState(View content, View loader, View error, TextView btnSeeAll) {
        content.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        if (btnSeeAll != null) btnSeeAll.setVisibility(View.GONE);
    }

    private void showContentState(View content, View loader, View error, TextView btnSeeAll) {
        loader.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        content.setVisibility(View.VISIBLE);
        if (btnSeeAll != null) btnSeeAll.setVisibility(View.VISIBLE);
    }

    private void showErrorState(String message, View content, View loader, View error, TextView tvError, TextView btnSeeAll) {
        loader.setVisibility(View.GONE);
        content.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        if (tvError != null) {
            tvError.setText(message);
            // Explicitly force text alignment for multiline error messages
            tvError.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        }
        if (btnSeeAll != null) btnSeeAll.setVisibility(View.GONE);
    }

    // RESTORED: Detailed error message logic
    private String getErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad Request (400)\nThe server could not understand the request.";
            case 401 -> "Unauthorized (401)\nInvalid API Key. Please check credentials.";
            case 403 -> "Forbidden (403)\nAccess denied or API limit reached.";
            case 404 -> "Not Found (404)\nThe requested resource was not found.";
            case 429 -> "Limit Reached (429)\nToo many requests. Please wait a moment.";
            case 500 -> "Server Error (500)\nSomething went wrong on our end.";
            case 502 -> "Bad Gateway (502)\nInvalid response from upstream.";
            case 503 -> "Service Unavailable (503)\nThe server is overloaded.";
            default -> "Unexpected Error (" + statusCode + ")\nPlease try again later.";
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!featuredArticles.isEmpty()) sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}