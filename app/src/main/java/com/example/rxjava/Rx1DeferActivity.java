package com.example.rxjava;

import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

import static android.os.Process.THREAD_PRIORITY_BACKGROUND;

/**
 * Created on 6/17/16.
 */
public class Rx1DeferActivity extends AppCompatActivity {
    private static final String TAG = "Rx1DeferActivity";
    private Looper backgroundLooper;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rx1);

        BackgroundThread backgroundThread = new BackgroundThread();
        backgroundThread.start();
        backgroundLooper = backgroundThread.getLooper();
    }

    public void defer(View v) {
        onRunSchedulerExampleButtonClicked();
    }

    /*
    When the button is clicked it will log the following after 5 seconds.
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onNext(one)
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onNext(two)
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onNext(three)
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onNext(four)
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onNext(five)
        06-13 10:22:46.423 16707-16707/? D/RxAndroidSamples: onCompleted()
    */
    void onRunSchedulerExampleButtonClicked() {
        sampleObservable()
                // Run on a background thread
                .subscribeOn(AndroidSchedulers.from(backgroundLooper))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String value) {
                        Log.d(TAG, "onNext(" + value + ")");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onCompleted()");
                    }
                });
    }

    static Observable<String> sampleObservable() {
        return Observable.defer(new Callable<ObservableSource<String>>() {
            @Override
            public ObservableSource<String> call() throws Exception {
                // Do some long running operation
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                Log.d(TAG, "The sleep is over, now produce something");
                return Observable.just("one", "two", "three", "four", "five");
            }
        });
    }

    static class BackgroundThread extends HandlerThread {
        BackgroundThread() {
            super("SchedulerSample-BackgroundThread", THREAD_PRIORITY_BACKGROUND);
        }
    }
}
