package com.example.homework2;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class MainActivity3 extends AppCompatActivity {
    private EditText urlInput;
    private Button viewSourceButton, viewPageButton;
    private WebView webView;
    private TextView sourceCodeView;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        urlInput = findViewById(R.id.urlInput);
        viewSourceButton = findViewById(R.id.viewSourceButton);
        viewPageButton = findViewById(R.id.viewPageButton);
        webView = findViewById(R.id.webView);
        sourceCodeView = findViewById(R.id.sourceCodeView);

        client = new OkHttpClient();

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        viewSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString();
                fetchHtmlSource(url);
            }
        });

        viewPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlInput.getText().toString();
                webView.loadUrl(url);
            }
        });
    }

    private void fetchHtmlSource(String url) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String source = response.body().string();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sourceCodeView.setText(source);
                        }
                    });
                }
            }
        });
    }
}
