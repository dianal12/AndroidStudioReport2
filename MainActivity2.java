package com.example.homework2;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {
    private TextView tvResult;
    private int selectedMethod = R.id.menu_strict;  // 기본 StrictMode 선택

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        tvResult = findViewById(R.id.tvResult);
        Button btnHttp = findViewById(R.id.btnHttp);

        btnHttp.setOnClickListener(v -> fetchData());
    }

    private void fetchData() {
        String url = "http://www.soen.kr/html5/html/htmlonly.html";

        if (selectedMethod == R.id.menu_strict) {
            fetchWithStrictMode(url);
        } else if (selectedMethod == R.id.menu_async) {
            new FetchDataTask().execute(url);
        } else {
            fetchWithThread(url);
        }
    }

    private void fetchWithStrictMode(String urlStr) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String result = getHttpContent(urlStr);
        tvResult.setText(result);
    }

    private void fetchWithThread(String urlStr) {
        new Thread(() -> {
            String result = getHttpContent(urlStr);
            runOnUiThread(() -> tvResult.setText(result));
        }).start();
    }

    private class FetchDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return getHttpContent(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
        }
    }

    private String getHttpContent(String urlStr) {
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line).append("\n");
            }
            reader.close();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
        return result.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        selectedMethod = item.getItemId();
        Toast.makeText(this, item.getTitle() + " 선택됨", Toast.LENGTH_SHORT).show();
        return true;
    }
}
