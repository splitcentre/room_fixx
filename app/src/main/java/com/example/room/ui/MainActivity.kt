package com.example.room.ui

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

class MainActivity : AppCompatActivity() {
    private val firestore = FirebaseFirestore.getInstance()
    private val budgetCollectionRef = firestore.collection("budgets")
    private lateinit var binding: ActivityMainBinding
    private var updateId = ""
    private val budgetListLiveData: MutableLiveData<List<Budget>> by lazy {
        MutableLiveData<List<Budget>>()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        with(binding) {
            btnAdd.setOnClickListener {
                val nominal = edtNominal.text.toString()
                val description = edtDesc.text.toString()
                val date = edtDate.text.toString()
                val newBudget = Budget(nominal = nominal, description = description,
                    date = date)
                addBudget(newBudget)
            }
            btnUpdate.setOnClickListener {
                val nominal = edtNominal.text.toString()
                val description = edtDesc.text.toString()
                val date = edtDate.text.toString()
                val budgetToUpdate = Budget(nominal = nominal, description =
                description, date = date)
                updateBudget(budgetToUpdate)
                updateId = ""
                setEmptyField()
            }
            listView.setOnItemClickListener { adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Budget
                updateId = item.id
                edtNominal.setText(item.nominal)
                edtDesc.setText(item.description)
                edtDate.setText(item.date)
            }
            listView.onItemLongClickListener = AdapterView.OnItemLongClickListener {
                    adapterView, _, i, _ ->
                val item = adapterView.adapter.getItem(i) as Budget
                deleteBudget(item)
                true
            }
        }
        observeBudgets()
        getAllBudgets()
    }

    private fun getAllBudgets() {
        observeBudgetChanges()
    }
    private fun observeBudgets() {
        budgetListLiveData.observe(this) { budgets ->
            val adapter = ArrayAdapter(
                this,
                android.R.layout.simple_list_item_1,
                budgets.toMutableList()
            )
            binding.listView.adapter = adapter
        }
    }
    private fun observeBudgetChanges() {
        budgetCollectionRef.addSnapshotListener { snapshots, error ->
            if (error != null) {
                Log.d("MainActivity", "Error listening for budget changes: ", error)
                return@addSnapshotListener
            }
            val budgets = snapshots?.toObjects(Budget::class.java)
            if (budgets != null) {
                budgetListLiveData.postValue(budgets)
            }
        }
    }
    private fun addBudget(budget: Budget) {
        budgetCollectionRef.add(budget)
            .addOnSuccessListener { documentReference ->
                val createdBudgetId = documentReference.id
                budget.id = createdBudgetId
                documentReference.set(budget)
                    .addOnFailureListener {
                        Log.d("MainActivity", "Error updating budget ID: ", it)
                    }
            }
            .addOnFailureListener {
                Log.d("MainActivity", "Error adding budget: ", it)
            }
    }
    private fun updateBudget(budget: Budget) {
        budget.id = updateId
        budgetCollectionRef.document(updateId).set(budget)
            .addOnFailureListener {
                Log.d("MainActivity", "Error updating budget: ", it)
            }
    }
    private fun deleteBudget(budget: Budget) {
        if (budget.id.isEmpty()) {
            Log.d("MainActivity", "Error deleting: budget ID is empty!")
            return
        }
        budgetCollectionRef.document(budget.id).delete()
            .addOnFailureListener {
                Log.d("MainActivity", "Error deleting budget: ", it)
            }
    }
    private fun setEmptyField() {
        with(binding) {
            edtNominal.setText("")
            edtDesc.setText("")
            edtDate.setText("")
        }
    }


}