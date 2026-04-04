package com.example.expensetracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText amountInput, categoryInput, descriptionInput, dateInput;
    Button addExpenseBtn;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        amountInput = findViewById(R.id.amount);
        categoryInput = findViewById(R.id.category);
        descriptionInput = findViewById(R.id.description);
        dateInput = findViewById(R.id.date);
        addExpenseBtn = findViewById(R.id.addExpense);

        // Connect to Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("Expenses");

        addExpenseBtn.setOnClickListener(v -> addExpense());
    }

    private void addExpense() {

        try {

            String amountStr = amountInput.getText().toString();
            String category = categoryInput.getText().toString();
            String description = descriptionInput.getText().toString();
            String date = dateInput.getText().toString();

            if (amountStr.isEmpty() || category.isEmpty() || description.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Generate unique id
            String id = databaseReference.push().getKey();

            // Create Expense object
            Expense expense = new Expense(
                    id,
                    "user1",
                    amount,
                    category,
                    description,
                    date
            );

            // Save to Firebase
            databaseReference.child(id).setValue(expense)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(MainActivity.this,
                                "Expense Added Successfully",
                                Toast.LENGTH_SHORT).show();

                        Log.d("FirebaseTest", "Expense saved");

                        // Clear fields
                        amountInput.setText("");
                        categoryInput.setText("");
                        descriptionInput.setText("");
                        dateInput.setText("");
                    })

                    .addOnFailureListener(e -> {
                        Toast.makeText(MainActivity.this,
                                "Failed: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();

                        Log.e("FirebaseTest", "Error", e);
                    });

        } catch (Exception e) {

            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();

            Log.e("FirebaseTest", "Exception", e);
        }
    }
}