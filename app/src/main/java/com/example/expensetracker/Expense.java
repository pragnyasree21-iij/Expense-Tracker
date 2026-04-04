package com.example.expensetracker;

public class Expense {

    private String id;
    private String userId;
    private double amount;
    private String category;
    private String description;
    private String date;

    // Required empty constructor for Firebase
    public Expense() {
    }

    public Expense(String id, String userId, double amount, String category, String description, String date) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public void setId(String id) {
        this.id = id;
    }
}