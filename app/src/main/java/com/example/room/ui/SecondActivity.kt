package com.example.room.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.room.databinding.ActivitySecondBinding
import com.example.room.database.Note
import com.example.room.database.NoteDao
import com.example.room.database.NoteRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors




class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding
    private lateinit var mNotesDao: NoteDao
    private lateinit var executorService: ExecutorService
    private var updateId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        executorService = Executors.newSingleThreadExecutor()
        val db = NoteRoomDatabase.getDatabase(this)
        mNotesDao = db!!.noteDao()!!
        setContentView(binding.root)

        getAll() // Memanggil fungsi getAll() saat Activity dibuat

        // Button onClickListeners
        with(binding) {
            btnnn.setOnClickListener(View.OnClickListener {
                insert(
                    Note(
                        0,
                        txtTitle.text.toString(),
                        txtDesc.text.toString(),
                        txtDate.text.toString()
                    )
                )
                println("run insert")
                setEmptyField()
            })

            btnUpdate.setOnClickListener {
                update(
                    Note(
                        id = updateId,
                        txtTitle.text.toString(),
                        txtDesc.text.toString(),
                        txtDate.text.toString()
                    )
                )
                updateId = 0
                setEmptyField()
            }
        }
    }

    private fun getAll() {
        mNotesDao.getAllNotes.observe(this) { notes ->
            val adapter = NoteAdapter(notes,
                onItemClick = { item ->
                    updateId = item.id
                    binding.txtTitle.setText(item.title)
                    binding.txtDesc.setText(item.description)
                    binding.txtDate.setText(item.date)
                },
                onItemLongClick = { item ->
                    delete(item)
                }
            )
//            binding.recyclerView.adapter = adapter
        }
    }

    private fun insert(notes: Note) {
        println("Data to be inserted: $notes")
        executorService.execute {
            mNotesDao.insert(notes)
            println("Data inserted!")
            val intent = Intent() 
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }



    private fun update(notes: Note) {
        executorService.execute { mNotesDao.update(notes) }
    }

    private fun delete(notes: Note) {
        executorService.execute { mNotesDao.delete(notes) }
    }

    override fun onResume() {
        super.onResume()
        getAll()
    }

    private fun setEmptyField() {
        with(binding) {
            txtTitle.setText("")
            txtDate.setText("")
            txtDesc.setText("")
        }
    }
}