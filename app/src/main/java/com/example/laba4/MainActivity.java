package com.example.laba4;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DatabaseHelper dbHelper;
    private Handler handler;
    private static final int POLLING_INTERVAL = 20000; // 20 seconds
    private TextView currentSongTextView;
    private TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        handler = new Handler(Looper.getMainLooper());

        currentSongTextView = findViewById(R.id.currentSongTextView);
        statusTextView = findViewById(R.id.statusTextView);

        Button viewHistoryButton = findViewById(R.id.viewHistoryButton);
        viewHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SongListActivity.class);
            startActivity(intent);
        });

        if (!isNetworkAvailable()) {
            statusTextView.setText("Нет подключения к интернету. Автономный режим.");
            Toast.makeText(this, "Нет подключения к интернету. Автономный режим.",
                    Toast.LENGTH_LONG).show();
        } else {
            statusTextView.setText("Подключено к интернету");
            startPolling();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void startPolling() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkAvailable()) {
                    new FetchSongTask().execute();
                }
                handler.postDelayed(this, POLLING_INTERVAL);
            }
        }, 0); // Start immediately, then every POLLING_INTERVAL
    }

    private class FetchSongTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            RequestBody formBody = new FormBody.Builder()
                    .add("login", "4707login")
                    .add("password", "4707pass")
                    .build();

            Request request = new Request.Builder()
                    .url("http://media.ifmo.ru/api_get_current_song.php")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String jsonData) {
            if (jsonData != null) {
                try {
                    JSONObject json = new JSONObject(jsonData);
                    if (json.getString("result").equals("success")) {
                        String info = json.getString("info");
                        currentSongTextView.setText("Текущий трек: " + info);
                        String[] parts = info.split(" – ");
                        if (parts.length == 2) {
                            String artist = parts[0];
                            String title = parts[1];
                            String lastSong = dbHelper.getLastSong();
                            if (!lastSong.equals(artist + " - " + title)) {
                                dbHelper.addSong(artist, title);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    currentSongTextView.setText("Ошибка получения данных");
                }
            } else {
                currentSongTextView.setText("Не удалось получить данные");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}