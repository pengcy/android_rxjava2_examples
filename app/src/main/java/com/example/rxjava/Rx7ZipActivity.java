package com.example.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function3;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created on 6/16/16.
 */
public class Rx7ZipActivity extends AppCompatActivity {
    private static final String TAG = "Rx7ZipActivity";
    private Subscription mTvShowSubscription;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private RecyclerView mTvShowListView;
    private ProgressBar mProgressBar;
    private SimpleStringAdapter mSimpleStringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx7);
        mProgressBar = (ProgressBar) findViewById(R.id.loader);
        mTvShowListView = (RecyclerView) findViewById(R.id.tv_show_list);
        mTvShowListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mTvShowListView.setAdapter(mSimpleStringAdapter);
        createObservable();
    }


    private void createObservable() {
        disposables.add(
                Observable
                        .zip(getStrings("One", "Two").subscribeOn(Schedulers.newThread()),
                                getStrings("Three", "Four").subscribeOn(Schedulers.newThread()),
                                getStrings("Five", "Six").subscribeOn(Schedulers.newThread()),
                                mergeStringLists())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<String>>() {
                            @Override
                            public void onNext(List<String> value) {
                                displayTvShows(value);
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {

                            }
                        })
        );

    }

    private Function3<List<String>, List<String>, List<String>, List<String>> mergeStringLists() {
        return new Function3<List<String>, List<String>, List<String>, List<String>>() {
            @Override
            public List<String> apply(List<String> strings, List<String> strings2, List<String> strings3) {
                Log.d("rxzip", "...");

                for (String s : strings2) {
                    strings.add(s);
                }

                for (String s : strings3) {
                    strings.add(s);
                }

                return strings;
            }
        };
    }

    private void sleep() {
        try {
            // "Simulate" the delay of network.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private Observable<List<String>> getStrings(final String str1, final String str2) {
        return Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() {
                //simulating a heavy duty computational expensive operation
                for (int i=0; i<1000000000; i++) {}
                Log.d("rxzip", Thread.currentThread().getName() + " " + str1 + " " + str2);
                List<String> strings = new ArrayList<>();
                strings.add(str1);
                strings.add(str2);
                return strings;
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposables != null) {
            disposables.clear();
        }
    }

    private void displayTvShows(List<String> tvShows) {
        mSimpleStringAdapter.setStrings(tvShows);
        mProgressBar.setVisibility(View.GONE);
        mTvShowListView.setVisibility(View.VISIBLE);
    }

}
