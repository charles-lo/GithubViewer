package com.charles.githubviewer.views.githubdetail;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.charles.githubviewer.R;
import com.charles.githubviewer.api.GithubViewModel;
import com.charles.githubviewer.api.model.User;
import com.google.android.material.snackbar.Snackbar;

/**
 *
 */

public class GithubDetailView extends AppCompatActivity {

    Context context;
    LinearLayout main;
    ImageView imgThumb;
    TextView txtName, txtBio, txtLogon, txtLocation, txtBlog, txtStaff;
    GithubViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_detail);

        context = GithubDetailView.this;
        viewModel = new ViewModelProvider(this).get(GithubViewModel.class);
        viewModel.userDetail.observe(this, user -> {
            if (user != null) {
                txtName.setText(user.getName());
                txtBio.setText(user.getBio());
                txtLogon.setText(user.getLogin());
                txtLocation.setText(user.getLocation());
                txtBlog.setText(user.getBlog());
                txtStaff.setVisibility(user.getSiteAdmin() ? View.VISIBLE : View.GONE);
            }
        });
        main = findViewById(R.id.github_detail_main);
        imgThumb = findViewById(R.id.imgThumbDetail);
        txtName = findViewById(R.id.name_detail);
        txtBio = findViewById(R.id.bio);
        txtLogon = findViewById(R.id.person);
        txtLocation = findViewById(R.id.location);
        txtBlog = findViewById(R.id.blog);
        txtStaff = findViewById(R.id.staff);

        try {
            displayUser((User) getIntent().getSerializableExtra("github"));
        } catch (Exception e) {
            displayMessage(getString(R.string.error_detail));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        viewModel.clearDetail();
    }

    public void displayMessage(String message) {
        Snackbar.make(main, message, Snackbar.LENGTH_LONG).show();
    }

    public void displayUser(User user) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(user.getLogin());
        }
        viewModel.fetchUser(user.getLogin());
        Glide.with(context).load(user.getAvatarUrl()).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_logo).into(imgThumb);
    }
}