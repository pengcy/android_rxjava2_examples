package com.example.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created on 6/16/16.
 */
public class Rx4PublishSubjectActivity extends AppCompatActivity {
    private TextView mCounterDisplay;
    private Button mIncrementButton;
    private PublishSubject<Integer> mCounterEmitter;

    private int mCounter = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx4);
        mCounterDisplay = (TextView) findViewById(R.id.counter_display);
        mCounterDisplay.setText(String.valueOf(mCounter));
        mIncrementButton = (Button) findViewById(R.id.increment_button);
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCounter++;
                mCounterEmitter.onNext(mCounter);
            }
        });

        createCounterEmitter();
    }

    private void createCounterEmitter() {
        mCounterEmitter = PublishSubject.create();
        mCounterEmitter.subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onComplete() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                mCounterDisplay.setText(String.valueOf(integer));
            }
        });
    }

}
