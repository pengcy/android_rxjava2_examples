package com.example.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class Rx8FlapmapActivity extends AppCompatActivity {
    private TextView mValueDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx5);
        mValueDisplay = (TextView) findViewById(R.id.value_display);
        Log.d("Rx8FlapmapActivity", "onCreate");

        flapMapEx1();
        flapMapEx2();
    }


    // Input 2
    // 1st flapMap  2 * 2
    // 2nd flapMap 4 * 3
    // 3nd flapMap 12 * 4
    // result in the subscribe 48
    private void flapMapEx1() {
        Observable.just(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(Integer integer) {
                        Log.d("flatMap", integer + " * 2");
                        return multiplyInt(integer, 2);
                    }
                })
                .flatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(Integer integer) {
                        Log.d("flatMap", integer + " * 3");
                        return multiplyInt(integer, 3);
                    }
                })
                .flatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(Integer integer) {
                        Log.d("flatMap", integer + " * 4");
                        return multiplyInt(integer, 4);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d("flatMap", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("flatMap", "onError");
                    }

                    @Override
                    public void onNext(Integer integer) {
                        mValueDisplay.setText(integer.toString() + "\n");
                    }
                });

    }

    private Observable<Integer> multiplyInt(final Integer integer, final int multiplier) {
        for (int i=0; i<1000000000; i++) {}
        return Observable.just(new Integer(integer * multiplier));
    }



    // 1,2,3,4,5,6,7,8,9
    // filter out odd numbers 1, 3, 5, 7, 9
    // pass down the even numbers 2, 4, 6, 8 to the last flatMap
    // the last flapMap multiply each even number by 2
    // the onNext in the subscribe prints 4, 8, 12, 16 one by one
    private void flapMapEx2() {
        List<Integer> ints = new ArrayList<>();
        for (int i=1; i<10; i++) {
            ints.add(new Integer(i));
        }
        Log.d("flatMap", "1,2,3,4,5,6,7,8,9");

        Observable.just(ints)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<List<Integer>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(List<Integer> ints) {
                        return Observable.fromIterable(ints);
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        Log.d("flatMap", "filter out odd numbers.........");
                        return integer.intValue() % 2 == 0;
                    }
                })
                .flatMap(new Function<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(Integer integer) {
                        //simulating a heavy duty computational expensive operation
                        for (int i = 0; i < 1000000000; i++) {
                        }
                        return multiplyInt(integer, 2);
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }
                    @Override
                    public void onComplete() {
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                    @Override
                    public void onNext(Integer integer) {
                        Log.d("flatMap", "onNext: " + integer.toString());
                    }
                });
    }


}
