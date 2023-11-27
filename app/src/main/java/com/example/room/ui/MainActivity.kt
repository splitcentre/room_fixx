package com.example.room.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.room.Budget
import com.example.room.R
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import com.example.room.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity  : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("budgets")
    private lateinit var binding: ActivityMainBinding
    private var updateId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        updateId = intent.getStringExtra("UPDATE_ID") ?: ""
        val receivedNominal = intent.getStringExtra("NOMINAL")
        val receivedDescription = intent.getStringExtra("DESCRIPTION")
        val receivedDate = intent.getStringExtra("DATE")


        binding.edtNominal.setText(receivedNominal)
        binding.edtDesc.setText(receivedDescription)
        binding.edtDate.setText(receivedDate)

        binding.btnUpdate.setOnClickListener {
            onUpdateClicked()
        }

        binding.btnAdd.setOnClickListener {
            val nominal = binding.edtNominal.text.toString()
            val description = binding.edtDesc.text.toString()
            val date = binding.edtDate.text.toString()
            val newBudget = Budget(aduan = nominal, description = description,
                pengadu = date)
            if (updateId.isNotEmpty()) {
                newBudget.id = updateId
                updateBudget(newBudget)
            } else {
                addBudget(newBudget)
            }
        }
    }

    private fun addBudget(budget: Budget) {
        budgetCollectionRef.add(budget)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                budget.id = createdBudgetId
                documentReference.set(budget)
                    .addOnSuccessListener {
                        Log.d("SecondActivity", "Budget successfully added!")
                        navigateToMainActivity()
                    }
                    .addOnFailureListener { e ->
                        Log.d("SecondActivity", "Error adding budget: ", e)
                    }
            }
            .addOnFailureListener { e ->
                Log.d("SecondActivity", "Error adding budget: ", e)
            }
    }

    private fun updateBudget(budget: Budget) {
        budgetCollectionRef.document(budget.id)
            .set(budget)
            .addOnSuccessListener {
                Log.d("SecondActivity", "Budget successfully updated!")
                navigateToMainActivity()
            }
            .addOnFailureListener { e ->
                Log.d("SecondActivity", "Error updating budget: ", e)
            }
    }

    private fun onUpdateClicked() {
        val nominal = binding.edtNominal.text.toString()
        val description = binding.edtDesc.text.toString()
        val date = binding.edtDate.text.toString()
        val updateBudget = Budget(aduan = nominal, description = description,
            pengadu = date)

        if (updateId.isNotEmpty()) {
            updateBudget.id = updateId
            updateBudget(updateBudget)
        } else {
            addBudget(updateBudget)
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@MainActivity, NewActivity::class.java)
        startActivity(intent)
        finish()
    }
}