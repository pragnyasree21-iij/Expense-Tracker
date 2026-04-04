package com.example.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText username = findViewById(R.id.etUsername);
        EditText password = findViewById(R.id.etPassword);
        CheckBox showPass = findViewById(R.id.cbShowPassword);
        Button login = findViewById(R.id.btnLogin);

        showPass.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            password.setSelection(password.getText().length());
        });

        login.setOnClickListener(v -> {
            String userStr = username.getText().toString().trim();
            String passStr = password.getText().toString().trim();

            if (!userStr.isEmpty() && !passStr.isEmpty()) {
                Intent intent = new Intent(this, ExpenseActivity.class);
                intent.putExtra("USER_ID", userStr);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
