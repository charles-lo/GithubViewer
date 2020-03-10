package com.charles.githubviewer;

import android.app.Application;

import com.charles.githubviewer.api.AppRepository;
import com.charles.githubviewer.data.BoxManager;

public class GithubApp extends Application {

    @Override
    public void onCreate() {
        init();
        super.onCreate();
    }

    private void init() {
        BoxManager.init(this);
        AppRepository.getInstance();
    }
}
