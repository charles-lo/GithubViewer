package com.charles.githubviewer.views.githublist;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.charles.githubviewer.R;
import com.charles.githubviewer.api.model.User;
import com.charles.githubviewer.views.githubdetail.GithubDetailView;

import java.util.List;

/**
 *
 */

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context context;
    private List<User> users;
    private String strHighLight;

    class UserViewHolder extends RecyclerView.ViewHolder {
        LinearLayout row;
        ImageView imgThumb;
        TextView txtName, txtInfo, txtStaff;

        UserViewHolder(View view) {
            super(view);
            row = view.findViewById(R.id.github_item_row);
            imgThumb = view.findViewById(R.id.thumb);
            txtName = view.findViewById(R.id.github_name);
            txtInfo = view.findViewById(R.id.info);
            txtStaff = view.findViewById(R.id.staff);
            row.setOnClickListener(view1 -> {
                Intent detail = new Intent(context, GithubDetailView.class);
                detail.putExtra("github", users.get(getAdapterPosition()));
                context.startActivity(detail);
            });
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }

    UserAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof UserViewHolder) {
            User user = users.get(position);

            UserViewHolder holder = (UserViewHolder) viewHolder;

            final String artworkUrl = user.getAvatarUrl();
            Glide.with(context).load(artworkUrl).apply(RequestOptions.circleCropTransform()).placeholder(R.drawable.ic_logo).into(holder.imgThumb);

            if (TextUtils.isEmpty(strHighLight)) {
                holder.txtName.setText(user.getLogin());
            } else {
                holder.txtName.setText(Html.fromHtml(user.getLogin().replaceAll(strHighLight, "<font color='red'>" + strHighLight + "</font>")));
            }
            holder.txtInfo.setText(String.format(context.getString(R.string.info), user.getId(), position + 1, user.getSiteAdmin(), user.getType()));
            holder.txtStaff.setVisibility(user.getSiteAdmin() ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return users.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    void setHighLight(String keyword) {
        strHighLight = keyword;
    }
}