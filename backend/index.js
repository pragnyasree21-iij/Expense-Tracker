const express = require('express');
const admin = require('firebase-admin');
const cors = require('cors');

const app = express();
app.use(cors());
app.use(express.json());-------;'
'

// Firebase Admin Initialization
const serviceAccount = require('./serviceAccountKey.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://expensetrackerdb-8e368-default-rtdb.firebaseio.com"
});

const db = admin.database();
const expensesRef = db.ref("Expenses");

// Validation helper
const validateExpenseInput = (data) => {
  const errors = [];

  if (!data.amount || isNaN(data.amount))
    errors.push("Valid amount is required.");

  if (!data.category || typeof data.category !== "string" || data.category.trim() === "")
    errors.push("Category is required.");

  if (!data.date || typeof data.date !== "string" || data.date.trim() === "")
    errors.push("Date is required.");

  return errors;
};


// ---------------- ADD EXPENSE ----------------
app.post("/expenses", async (req, res) => {
  try {

    const { userId, amount, category, description, date } = req.body;

    const validationErrors = validateExpenseInput(req.body);

    if (!userId)
      validationErrors.push("userId is required.");

    if (validationErrors.length > 0)
      return res.status(400).json({ success: false, errors: validationErrors });

    const newExpenseRef = expensesRef.push();

    const expenseId = newExpenseRef.key;

    const newExpense = {
      id: expenseId,
      userId: userId,
      amount: Number(amount),
      category: category,
      description: description || "Expense Added",
      date: date
    };

    await newExpenseRef.set(newExpense);

    res.status(201).json({
      success: true,
      message: "Expense added successfully",
      data: newExpense
    });

  } catch (error) {

    console.error("Error adding expense:", error);

    res.status(500).json({
      success: false,
      message: "Internal server error"
    });
  }
});


// ---------------- GET EXPENSES ----------------
app.get("/expenses/:userId", async (req, res) => {

  try {

    const userId = req.params.userId;

    const snapshot = await expensesRef
      .orderByChild("userId")
      .equalTo(userId)
      .once("value");

    const data = snapshot.val();

    const expenses = [];

    if (data) {
      Object.keys(data).forEach(key => {
        expenses.push(data[key]);
      });
    }

    res.status(200).json({
      success: true,
      data: expenses
    });

  } catch (error) {

    console.error("Error fetching expenses:", error);

    res.status(500).json({
      success: false,
      message: "Internal server error"
    });

  }
});


// ---------------- UPDATE EXPENSE ----------------
app.put("/expenses/:id", async (req, res) => {

  try {

    const expenseId = req.params.id;

    const updates = req.body;

    const expenseRef = expensesRef.child(expenseId);

    const snapshot = await expenseRef.once("value");

    if (!snapshot.exists())
      return res.status(404).json({
        success: false,
        message: "Expense not found"
      });

    await expenseRef.update(updates);

    res.status(200).json({
      success: true,
      message: "Expense updated successfully"
    });

  } catch (error) {

    console.error("Error updating expense:", error);

    res.status(500).json({
      success: false,
      message: "Internal server error"
    });

  }
});


// ---------------- DELETE EXPENSE ----------------
app.delete("/expenses/:id", async (req, res) => {

  try {

    const expenseId = req.params.id;

    const expenseRef = expensesRef.child(expenseId);

    const snapshot = await expenseRef.once("value");

    if (!snapshot.exists())
      return res.status(404).json({
        success: false,
        message: "Expense not found"
      });

    await expenseRef.remove();

    res.status(200).json({
      success: true,
      message: "Expense deleted successfully"
    });

  } catch (error) {

    console.error("Error deleting expense:", error);

    res.status(500).json({
      success: false,
      message: "Internal server error"
    });

  }
});


// ---------------- SERVER START ----------------
const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
  console.log(`Expense Tracker REST API is running on port ${PORT}`);
});



