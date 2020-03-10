package com.charles.githubviewer.api;

import com.charles.githubviewer.api.model.User;
import com.charles.githubviewer.api.model.User_;
import com.charles.githubviewer.data.BoxManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import io.objectbox.query.Query;
import io.objectbox.rx.RxQuery;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class AppRepository {
    private static final String TAG = AppRepository.class.getSimpleName();
    private static final String PER_PAGE = "per_page";
    private static final String SINCE = "since";
    private static volatile AppRepository INSTANCE;

    private CompositeDisposable disposables = new CompositeDisposable();

    private MutableLiveData<List<User>> allUsers = new MutableLiveData<>();
    private MutableLiveData<User> userDetail = new MutableLiveData<>();
    private MutableLiveData<Boolean> networkStatus = new MutableLiveData<>();

    private AppRepository() {
        // Prevent form the reflection api.
        if (INSTANCE != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static AppRepository getInstance() {
        if (INSTANCE == null) {
            synchronized (AppRepository.class) {
                if (INSTANCE == null) INSTANCE = new AppRepository();
            }
        }
        return INSTANCE;
    }

    LiveData<List<User>> getUsers() {
        return allUsers;
    }

    LiveData<User> getUserDetail() {
        return userDetail;
    }

    void clearDetail() {
        userDetail.postValue(null);
    }

    void fetchAllUser(final long since) {
        Map<String, Object> params = new HashMap<>();
        params.put(SINCE, since);
        params.put(PER_PAGE, 20);
        userDetail.postValue(null);

        disposables.add(ServiceFactory.getInstance().fetchAllUser(params)
                .subscribeOn(Schedulers.io())
                .subscribe(
                        result -> {
                            if (since == 0) {
                                allUsers.postValue(result);
                            } else {
                                if (allUsers.getValue() != null && allUsers.getValue().size() != 100) {
                                    allUsers.getValue().addAll(result);
                                }
                                allUsers.postValue(allUsers.getValue());
                            }
                        },
                        throwable -> networkStatus.postValue(false)
                ));
    }

    void fetchUser(final String name) {
        Query<User> postBox = BoxManager.getStore().boxFor(User.class).query().equal(User_.login, name).build();
        disposables.add(RxQuery.single(postBox)
                .observeOn(Schedulers.io())
                .flatMap(result -> {
                    if (result.size() > 0) {
                        userDetail.postValue(result.get(0));
                    }
                    return ServiceFactory.getInstance().fetchUser(name);
                }).subscribeOn(Schedulers.io())
                .subscribe(
                        result -> userDetail.postValue(result),
                        throwable -> networkStatus.postValue(false)
                ));

    }

    LiveData<Boolean> getNetworkStatus() {
        return networkStatus;
    }

    void setNetworkStatus(Boolean status) {
        networkStatus.postValue(status);
    }
}
