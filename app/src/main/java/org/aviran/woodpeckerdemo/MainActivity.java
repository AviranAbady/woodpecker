package org.aviran.woodpeckerdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.aviran.woodpecker.Woodpecker;
import org.aviran.woodpecker.WoodpeckerError;
import org.aviran.woodpecker.WoodpeckerFileStream;
import org.aviran.woodpecker.WoodpeckerProgressListener;
import org.aviran.woodpecker.WoodpeckerResponse;
import org.aviran.woodpecker.WoodpeckerSettings;
import org.aviran.woodpeckerdemo.model.DownloadFileRequest;
import org.aviran.woodpeckerdemo.model.ItemRequest;
import org.aviran.woodpeckerdemo.model.ItemResponse;
import org.aviran.woodpeckerdemo.model.ListRequest;
import org.aviran.woodpeckerdemo.model.LoginRequest;
import org.aviran.woodpeckerdemo.model.LoginResponse;
import org.aviran.woodpeckerdemo.model.ReviewRequest;
import org.aviran.woodpeckerdemo.model.UploadRequest;
import org.aviran.woodpeckerdemo.model.UploadResponse;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView logTextView;
    private Button button;
    private ProgressBar progressBar;
    private StringBuilder log;
    private TextView progressTextView;
    private WoodpeckerProgressListener progressListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        log = new StringBuilder();
        logTextView = (TextView) findViewById(R.id.log_textView);
        progressTextView = (TextView) findViewById(R.id.progress_textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.runButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setEnabled(false);
                button.setText(R.string.runnning);
                runDemo();
            }
        });
        progressListener = createProgressListener();

        // Initialize woodpecker with a base url
        Woodpecker.initialize(new WoodpeckerSettings("http://woodpecker.aviran.org"));
    }

    private WoodpeckerProgressListener createProgressListener() {
        return new WoodpeckerProgressListener() {
            @Override
            public void onProgress(String name, int progress, int totalSize) {
                int percentage = 100 * progress / totalSize;
                progressBar.setProgress(percentage);
                String text = String.format(Locale.getDefault(),
                        "%d%%  -  %,d / %,d   (kb)",
                        percentage,
                        progress / 1024,
                        totalSize / 1024);
                progressTextView.setText(text);
            }
        };
    }

    private void runDemo() {

        // POST  login           /login?username=user&password=password
        // GET   list            /list?page=1&pageSize=10
        // GET   item            /item/{id}
        // POST  review          { itemId: id, name: Aviran, review: This is awesome }
        Woodpecker
                .begin()
                .request(new LoginRequest("aviran", "12345"))
                .then(new WoodpeckerResponse<LoginResponse>() {
                    @Override
                    public void onSuccess(LoginResponse response) {
                        Woodpecker.getSettings().addHeader("token", response.getToken());
                        printLog("Login successful, token: " + response.getToken());
                    }
                })
                .request(new ListRequest(1, 10))
                .then(new WoodpeckerResponse<List<ItemResponse>>() {
                    @Override
                    public void onSuccess(List<ItemResponse> response) {
                        ItemRequest itemRequest = (ItemRequest) getNextRequest();
                        itemRequest.setId(response.get(0).getId());

                        printLog("List request successful, second element is: " + response.get(1).getName());
                    }
                })
                .request(new ItemRequest(-1))
                .then(new WoodpeckerResponse<ItemResponse>() {
                    @Override
                    public void onSuccess(ItemResponse response) {
                        printLog("Item request successful, element is: "
                                + response.getName() + " "
                                + Arrays.toString(response.getValues()));
                    }
                })
                .request(new ReviewRequest(1, "Aviran", "This is awesome!"))
                .then(new WoodpeckerResponse<String>() {
                    @Override
                    public void onSuccess(String response) {
                        printLog("Review request successful, response is:\n" + response);
                    }
                })
                .request(new DownloadFileRequest(progressListener))
                .then(new WoodpeckerResponse<InputStream>() {
                    @Override
                    public void onSuccess(InputStream response) {
                        printLog("file downloaded successfully");
                    }
                })
                .request(createFileUploadRequest())
                .then(new WoodpeckerResponse<UploadResponse>() {
                    @Override
                    public void onSuccess(UploadResponse response) {
                        printLog(response.getMsg());
                        printLog(response.getUrl());
                        button.setEnabled(true);
                        button.setText(R.string.run_requests);
                        printLog("========================================");
                    }
                })
                .error(new WoodpeckerError() {
                    @Override
                    public void onError(WoodpeckerResponse response) {
                        Log.e("WP", "ERROR");
                        button.setEnabled(true);
                        button.setText(R.string.run_requests);
                    }
                });
    }

    public UploadRequest createFileUploadRequest() {
        InputStream inputStream = getResources().openRawResource(R.raw.car);
        InputStream inputStream2 = getResources().openRawResource(R.raw.android);
        WoodpeckerFileStream stream1 = new WoodpeckerFileStream("file1", inputStream);
        WoodpeckerFileStream stream2 = new WoodpeckerFileStream("file2", inputStream2);
        return new UploadRequest("uploadToken", stream1, stream2, progressListener);
    }

    private void printLog(String text) {
        log.append(text);
        logTextView.setText(log.toString());
        log.append("\n\n");
    }
}