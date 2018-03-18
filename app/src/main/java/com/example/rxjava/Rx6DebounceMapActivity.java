package com.example.rxjava;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.reactivestreams.Subscription;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * Created on 6/16/16.
 */
public class Rx6DebounceMapActivity extends AppCompatActivity {
    private RestClient mRestClient;
    private EditText mSearchInput;
    private TextView mNoResultsIndicator;
    private RecyclerView mSearchResults;
    private SimpleStringAdapter mSearchResultsAdapter;

    private PublishSubject<String> mSearchResultsSubject;
    private Subscription mTextWatchSubscription;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx6);
        mSearchInput = (EditText) findViewById(R.id.search_input);
        mNoResultsIndicator = (TextView) findViewById(R.id.no_results_indicator);
        mSearchResults = (RecyclerView) findViewById(R.id.search_results);
        mSearchResults.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsAdapter = new SimpleStringAdapter(this);
        mSearchResults.setAdapter(mSearchResultsAdapter);

        mRestClient = new RestClient(this);

        createObservables();
        listenToSearchInput();
    }

    private void createObservables() {
        mSearchResultsSubject = PublishSubject.create();

        disposables.add(
                mSearchResultsSubject
                        .debounce(400, TimeUnit.MILLISECONDS)
                        .observeOn(Schedulers.io())
                        .map(new Function<String, List<String>>() {
                            @Override
                            public List<String> apply(String s) {
                                return mRestClient.searchForCity(s);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<List<String>>() {
                            @Override
                            public void onNext(List<String> cities) {
                                if (cities.isEmpty()) {
                                    mNoResultsIndicator.setVisibility(View.VISIBLE);
                                    mSearchResults.setVisibility(View.GONE);
                                } else {
                                    mNoResultsIndicator.setVisibility(View.GONE);
                                    mSearchResults.setVisibility(View.VISIBLE);
                                    mSearchResultsAdapter.setStrings(cities);
                                }
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

    private void listenToSearchInput() {
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchResultsSubject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

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
}
