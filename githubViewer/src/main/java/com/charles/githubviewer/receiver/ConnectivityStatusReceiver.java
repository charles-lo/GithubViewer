package com.charles.githubviewer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.charles.githubviewer.api.GithubViewModel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class ConnectivityStatusReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
            GithubViewModel viewModel = new ViewModelProvider((AppCompatActivity) context).get(GithubViewModel.class);
            if (activeNetworkInfo != null) {
                viewModel.setNetworkStatus(true);
            } else {
                viewModel.setNetworkStatus(false);
            }
        }
    }
}
