package com.charles.githubviewer.api;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.charles.githubviewer.api.model.User;

import java.util.List;

public class GithubViewModel extends ViewModel {

    public LiveData<List<User>> users = AppRepository.getInstance().getUsers();
    public LiveData<User> userDetail = AppRepository.getInstance().getUserDetail();
    public LiveData<Boolean> networkStatus = AppRepository.getInstance().getNetworkStatus();

    public void fetchAllUser(long since) {
        AppRepository.getInstance().fetchAllUser(since);
    }

    public void fetchUser(String user) {
        AppRepository.getInstance().fetchUser(user);
    }

    public void setNetworkStatus(Boolean status) {
        AppRepository.getInstance().setNetworkStatus(status);
    }

    public void clearDetail() {
        AppRepository.getInstance().clearDetail();
    }
}
