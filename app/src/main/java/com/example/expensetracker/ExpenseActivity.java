package com.example.expensetracker;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExpenseActivity extends AppCompatActivity {

    ArrayList<Expense> expenseList = new ArrayList<>();
    ArrayList<String> displayList = new ArrayList<>();
    ArrayAdapter<String> adapter;

    int selectedIndex = -1;
    double total = 0;

    String currentUserId = "default_user";

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    // IMPORTANT: Use your computer IP instead of 10.0.2.2
    private static final String API_URL = "http://192.168.1.8:3000/expenses";

    private static final MediaType JSON =
            MediaType.get("application/json; charset=utf-8");

    TextView totalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        if (getIntent() != null && getIntent().hasExtra("USER_ID")) {
            currentUserId = getIntent().getStringExtra("USER_ID");
        }

        EditText category = findViewById(R.id.etCategory);
        EditText amount = findViewById(R.id.etAmount);
        EditText date = findViewById(R.id.etDate);

        totalText = findViewById(R.id.tvTotal);
        ListView listView = findViewById(R.id.listView);

        Button add = findViewById(R.id.btnAdd);
        Button delete = findViewById(R.id.btnDelete);
        Button saveExit = findViewById(R.id.btnSaveExit);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, displayList);

        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedIndex = position;
        });

        add.setOnClickListener(v -> {

            String cat = category.getText().toString().trim();
            String amtStr = amount.getText().toString().trim();
            String dt = date.getText().toString().trim();

            if (TextUtils.isEmpty(cat) || TextUtils.isEmpty(amtStr) || TextUtils.isEmpty(dt)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amt = Double.parseDouble(amtStr);

            Expense expense = new Expense(null, currentUserId, amt, cat, "Expense Added", dt);

            String jsonPayload = gson.toJson(expense);

            RequestBody body = RequestBody.create(jsonPayload, JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {

                    runOnUiThread(() ->
                            Toast.makeText(ExpenseActivity.this,
                                    "Server connection failed",
                                    Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (response.isSuccessful()) {

                        runOnUiThread(() -> {

                            category.setText("");
                            amount.setText("");
                            date.setText("");

                            Toast.makeText(ExpenseActivity.this,
                                    "Expense Added",
                                    Toast.LENGTH_SHORT).show();

                            loadData();

                        });

                    } else {

                        runOnUiThread(() ->
                                Toast.makeText(ExpenseActivity.this,
                                        "Error adding expense",
                                        Toast.LENGTH_SHORT).show());

                    }
                }
            });

        });

        delete.setOnClickListener(v -> {

            if (selectedIndex >= 0 && selectedIndex < expenseList.size()) {

                Expense item = expenseList.get(selectedIndex);

                if (item.getId() != null) {

                    Request request = new Request.Builder()
                            .url(API_URL + "/" + item.getId())
                            .delete()
                            .build();

                    client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(Call call, IOException e) {

                            runOnUiThread(() ->
                                    Toast.makeText(ExpenseActivity.this,
                                            "Failed to delete expense",
                                            Toast.LENGTH_SHORT).show());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            if (response.isSuccessful()) {

                                runOnUiThread(() -> {

                                    selectedIndex = -1;
                                    listView.clearChoices();

                                    loadData();

                                });

                            }
                        }
                    });
                }
            }

        });

        saveExit.setOnClickListener(v -> finish());
    }

    private void loadData() {

        Request request = new Request.Builder()
                .url(API_URL + "/" + currentUserId)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {

                runOnUiThread(() ->
                        Toast.makeText(ExpenseActivity.this,
                                "Server not reachable",
                                Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful() && response.body() != null) {

                    String jsonResponse = response.body().string();

                    try {

                        ApiResponse apiResponse = gson.fromJson(jsonResponse, ApiResponse.class);

                        if (apiResponse != null && apiResponse.data != null) {

                            runOnUiThread(() -> {

                                expenseList.clear();
                                displayList.clear();
                                total = 0;

                                for (Expense exp : apiResponse.data) {

                                    expenseList.add(exp);

                                    displayList.add(
                                            exp.getDate() + " | " +
                                                    exp.getCategory() + " | " +
                                                    exp.getAmount());

                                    total += exp.getAmount();
                                }

                                totalText.setText("Total: " + total);

                                adapter.notifyDataSetChanged();

                            });

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

            }

        });

    }

    private static class ApiResponse {

        boolean success;
        List<Expense> data;

    }
}