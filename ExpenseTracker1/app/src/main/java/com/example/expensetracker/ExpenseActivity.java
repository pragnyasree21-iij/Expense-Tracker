package com.example.expensetracker;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpenseActivity extends AppCompatActivity {

    ArrayList<String> expenseList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    int selectedIndex = -1;
    double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);

        EditText category = findViewById(R.id.etCategory);
        EditText amount = findViewById(R.id.etAmount);
        EditText date = findViewById(R.id.etDate);
        TextView totalText = findViewById(R.id.tvTotal);
        ListView listView = findViewById(R.id.listView);

        Button add = findViewById(R.id.btnAdd);
        Button delete = findViewById(R.id.btnDelete);
        Button saveExit = findViewById(R.id.btnSaveExit);

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_activated_1, expenseList);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadData(totalText);

        listView.setOnItemClickListener((parent, view, position, id) -> selectedIndex = position);

        add.setOnClickListener(v -> {
            String cat = category.getText().toString();
            double amt = Double.parseDouble(amount.getText().toString());
            String dt = date.getText().toString();

            expenseList.add(dt + " | " + cat + " | " + amt);
            total += amt;
            totalText.setText("Total: " + total);

            adapter.notifyDataSetChanged();
        });

        delete.setOnClickListener(v -> {
            if (selectedIndex >= 0) {
                String item = expenseList.remove(selectedIndex);
                double amt = Double.parseDouble(item.split("\\|")[2].trim());
                total -= amt;
                totalText.setText("Total: " + total);
                adapter.notifyDataSetChanged();
            }
        });

        saveExit.setOnClickListener(v -> {
            saveData();
            finish();
        });
    }

    private void saveData() {
        SharedPreferences sp = getSharedPreferences("expenses", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("list", TextUtils.join(";", expenseList));
        editor.putFloat("total", (float) total);
        editor.apply();
    }

    private void loadData(TextView totalText) {
        SharedPreferences sp = getSharedPreferences("expenses", MODE_PRIVATE);
        String saved = sp.getString("list", "");
        total = sp.getFloat("total", 0);

        if (!saved.isEmpty()) {
            expenseList.addAll(Arrays.asList(saved.split(";")));
        }
        totalText.setText("Total: " + total);
    }
}
