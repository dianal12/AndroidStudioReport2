package com.example.homework2;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TEXT_URL = "http://http://127.0.0.1:8080/textfile.txt"; // Local Server
    private static final String[] IMAGE_URLS = {
            "http://127.0.0.1:8080/image1.jpeg",
            "http://127.0.0.1:8080/image2.jpeg",
            "http://127.0.0.1:8080/image3.jpeg"
    };

    private TextView textView;
    private ImageView imageView;
    private Button downloadButton;
    private RadioGroup radioGroup;
    private RadioButton strictModeBtn, asyncTaskBtn, threadBtn;

    private boolean isTextFile = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        downloadButton = findViewById(R.id.downloadButton);
        radioGroup = findViewById(R.id.radioGroup);
        strictModeBtn = findViewById(R.id.strictMode);
        asyncTaskBtn = findViewById(R.id.asyncTask);
        threadBtn = findViewById(R.id.thread);

        downloadButton.setOnClickListener(v -> downloadFile());
    }

    private void downloadFile() {
        if (isTextFile) {
            if (strictModeBtn.isChecked()) {
                downloadTextFileStrictMode();
            } else if (asyncTaskBtn.isChecked()) {
                new DownloadTextTask().execute(TEXT_URL);
            } else {
                new Thread(this::downloadTextFileThread).start();
            }
        } else {
            int imageIndex = (int) (Math.random() * IMAGE_URLS.length);
            String imageUrl = IMAGE_URLS[imageIndex];

            if (strictModeBtn.isChecked()) {
                downloadImageStrictMode(imageUrl);
            } else if (asyncTaskBtn.isChecked()) {
                new DownloadImageTask().execute(imageUrl);
            } else {
                new Thread(() -> downloadImageThread(imageUrl)).start();
            }
        }
    }

    private void downloadTextFileStrictMode() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        downloadTextFileThread();
    }

    private void downloadTextFileThread() {
        try {
            URL url = new URL(TEXT_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            String text = new String(buffer);
            runOnUiThread(() -> {
                textView.setText(text);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream is = conn.getInputStream();
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                return new String(buffer);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                textView.setText(result);
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void downloadImageStrictMode(String url) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        downloadImageThread(url);
    }

    private void downloadImageThread(String url) {
        try {
            Bitmap bitmap = downloadImage(url);
            runOnUiThread(() -> {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(MainActivity.this, "Image Download", "다운로드 중입니다. 잠시 기다려 주세요");
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            return downloadImage(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
            }
        }
    }

    private Bitmap downloadImage(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.text_file) {
            isTextFile = true;
            downloadButton.setText("TEXT FILE DOWNLOAD");
        } else if (item.getItemId() == R.id.image_file) {
            isTextFile = false;
            downloadButton.setText("IMAGE FILE DOWNLOAD");
        }
        return super.onOptionsItemSelected(item);
    }
}
