package com.charles.githubviewer.views.githublist;

import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.charles.githubviewer.R;
import com.charles.githubviewer.api.GithubViewModel;
import com.charles.githubviewer.api.model.User;
import com.charles.githubviewer.receiver.ConnectivityStatusReceiver;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.prefs.Prefs;

import static org.apache.commons.collections4.CollectionUtils.filter;


public class GithubListView extends AppCompatActivity {
    static final String TAG = GithubListView.class.getSimpleName();
    static final String KEYWORD = "keyword";

    Context context;
    RelativeLayout main;
    TextView txtNoGithub;
    ShimmerRecyclerView listUsers;
    SwipeRefreshLayout swipeRefreshLayout;
    Snackbar snackBar;
    SearchView searchView;
    Menu menuView;

    private List<User> dataUsers = new ArrayList<>();
    private UserAdapter adapter;
    String keyWord;
    private boolean loadingMore = false, isShimmer;
    private int pastVisibleItems, visibleItemCount, totalItemCount;


    ConnectivityStatusReceiver connectivityStatusReceiver;
    GithubViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_list);
        viewModel = new ViewModelProvider(this).get(GithubViewModel.class);
        context = GithubListView.this;
        keyWord = Prefs.with(this).read(KEYWORD);

        main = findViewById(R.id.github_list_main);
        txtNoGithub = findViewById(R.id.txtNoGithub);
        listUsers = findViewById(R.id.listGithub);
        swipeRefreshLayout = findViewById(R.id.swipe_container);
        swipeRefreshLayout.setOnRefreshListener(() -> fetchData(0));
        adapter = new UserAdapter(context, dataUsers);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listUsers.setLayoutManager(mLayoutManager);
        listUsers.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        listUsers.setItemAnimator(new DefaultItemAnimator());
        listUsers.setAdapter(adapter);
        listUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = mLayoutManager.getChildCount();
                    totalItemCount = mLayoutManager.getItemCount();
                    pastVisibleItems = mLayoutManager.findFirstVisibleItemPosition();

                    if (!loadingMore) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loadingMore = true;
                            Log.d("charles111", "pastVisibleItems start   " + pastVisibleItems);
                            //Do pagination.. i.e. fetch new data
                            fetchData(dataUsers.get(dataUsers.size() - 1).getId());
                            loadMore();
                        }
                    }
                }
            }
        });

        registerConnectivityMonitor();
    }

    @Override
    protected void onStop() {
        Prefs.with(this).write(KEYWORD, keyWord);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (connectivityStatusReceiver != null) {
            // unregister receiver
            unregisterReceiver(connectivityStatusReceiver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menuView = menu;
        getMenuInflater().inflate(R.menu.search, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        if (searchManager != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setQueryHint(getString(R.string.search_github_name));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterData(newText);
                keyWord = newText;
                return false;
            }
        });
        observeData();
        return true;
    }

    private void observeData() {
        viewModel.users.observe(this, result -> {

            if (loadingMore) {
                dataUsers.remove(dataUsers.size() - 1);
                int scrollPosition = dataUsers.size();
                adapter.notifyItemRemoved(scrollPosition);
                adapter.notifyDataSetChanged();
                loadingMore = false;
            }

            if (result != null) {
                txtNoGithub.setVisibility(View.GONE);
                if (result.size() > 0) {
                    displayUsers(result);
                    if (searchView != null && !TextUtils.isEmpty(keyWord)) {
                        String word = keyWord;
                        filterData(word);
                        menuView.performIdentifierAction(R.id.action_search, 0);
                        searchView.setQuery(word, true);
                    }
                } else {
                    displayMessage(getString(R.string.error_no_github), Snackbar.LENGTH_LONG);
                    dataUsers.clear();
                }
            } else {
                displayMessage(getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE);
                if (adapter.getItemCount() < 1) {
                    txtNoGithub.setVisibility(View.VISIBLE);
                    dataUsers.clear();
                }
            }
        });
        viewModel.networkStatus.observe(this, result -> {
            if (result != null) {
                if (result) {
                    fetchData(0);
                    if (snackBar != null) {
                        snackBar.dismiss();
                    }
                } else {
                    displayMessage(getString(R.string.error_network), Snackbar.LENGTH_INDEFINITE);
                }
            }
        });
    }

    private void registerConnectivityMonitor() {
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        connectivityStatusReceiver = new ConnectivityStatusReceiver();
        registerReceiver(connectivityStatusReceiver, intentFilter);
    }

    private void filterData(String keyword) {
        Predicate<User> validPredicate = item -> {
            assert item != null;
            return item.getLogin().toLowerCase().contains(keyword.toLowerCase());
        };

        if (viewModel.users.getValue() != null) {
            List<User> filterData = new ArrayList<>(viewModel.users.getValue());
            filter(filterData, validPredicate);
            displayUsers(filterData);
        }
        adapter.setHighLight(keyword);
    }

    public void fetchData(long since) {
        swipeRefreshLayout.setRefreshing(false);
        if (since == 0) {
            setLoadingIndicator(true);
        }
        viewModel.fetchAllUser(since);
    }

    public void displayUsers(List<User> data) {
        if (isShimmer) {
            setLoadingIndicator(false);
        }
        this.dataUsers.clear();
        this.dataUsers.addAll(data);
        adapter.notifyDataSetChanged();
    }

    public void displayMessage(String message, int duration) {
        setLoadingIndicator(false);
        snackBar = Snackbar.make(main, message, duration);
        snackBar.show();
    }

    public void setLoadingIndicator(boolean isLoading) {
        if (isLoading) {
            listUsers.showShimmerAdapter();
        } else {
            listUsers.hideShimmerAdapter();
        }
        isShimmer = isLoading;
    }

    private void loadMore() {
        dataUsers.add(null);
        adapter.notifyItemInserted(dataUsers.size() - 1);
    }
}