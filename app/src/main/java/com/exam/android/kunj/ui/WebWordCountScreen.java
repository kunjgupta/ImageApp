package com.exam.android.kunj.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.widget.EditText;

import com.exam.android.kunj.R;
import com.exam.android.kunj.adapters.WebContentListAdapter;
import com.exam.android.kunj.api.listeners.get.GetWebContentListener;
import com.exam.android.kunj.api.managers.WebContentApiManager;
import com.exam.android.kunj.db.models.WordCountModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Kunj Gupta on 22-Dec-2018.
 */
public class WebWordCountScreen extends AppCompatActivity {

    private static final int API_SUCCESS = 1;
    private static final int API_FAILURE = 2;

    @BindView(R.id.recyclerView) RecyclerView mRecyclerView;
    @BindView(R.id.web_word_count_search_edit_text) EditText mSearchEditText;
    private WebContentListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_word_count_screen);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setElevation(0);
            actionBar.setTitle(R.string.word_count_screen_action_bar_title);
            actionBar.setDisplayShowHomeEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                layoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mSearchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        new fetchWebContent().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    class fetchWebContent extends AsyncTask<String, Void, Integer> {
        private ProgressDialog progressDialog;
        private String apiResponse;
        private int status;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(WebWordCountScreen.this);
            progressDialog.setMessage(getString(R.string.loading_string));
            progressDialog.show();
            progressDialog.setCancelable(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            WebContentApiManager.getWebContent(new GetWebContentListener() {
                @Override
                public void onDoneApiCall(String webContentString) {
                    status = API_SUCCESS;
                    apiResponse = webContentString;
                }

                @Override
                public void onFailApiCall() {
                    status = API_FAILURE;
                    apiResponse = getString(R.string.failed_to_fetch_web_content_string);
                }
            });
            return status;
        }

        @Override
        protected void onPostExecute(Integer status) {
            super.onPostExecute(status);
            if(status.intValue() == API_SUCCESS) {
                String[] wordArray = apiResponse.split("\\s+");
                List<WordCountModel> wordCountModelList = new ArrayList<>(0);
                for (int i = 0; i < wordArray.length; i++) {
                    if(wordArray[i].length() == 0) {
                        continue;
                    }
                    int count = 1;
                    for (int j = i+1; j < wordArray.length; j++) {
                        if(wordArray[i].equals(wordArray[j])) {
                            count++;
                        }
                    }
                    WordCountModel wordCountModel = new WordCountModel();
                    wordCountModel.setCount(count);
                    wordCountModel.setWord(wordArray[i]);
                    wordCountModelList.add(wordCountModel);
                }

                // specify an mAdapter (see also next example)
                mAdapter = new WebContentListAdapter(wordCountModelList);
                mRecyclerView.setAdapter(mAdapter);
            }

            if(progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
