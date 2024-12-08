package com.example.fixu

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fixu.database.AddNoteRequest
import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.EditNoteRequest
import com.example.fixu.databinding.ActivityAddNoteBinding
import com.example.fixu.response.MLResponse
import com.example.fixu.response.NoteResponse
import com.example.fixu.response.PatchNoteResponse
import com.example.fixu.response.PostNoteResponse
import com.example.fixu.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private var isEdit = false
    private lateinit var sessionManager: SessionManager
    private lateinit var noteId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        val toolbar: Toolbar = binding.toolbarAddNote
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)

        val title = intent.getStringExtra("TITLE_EXTRA") ?: ""
        val content = intent.getStringExtra("CONTENT_EXTRA") ?: ""
        noteId = intent.getStringExtra("NOTEID_EXTRA") ?: ""

        if (noteId.isNotEmpty()) {
            isEdit = true
        }

        if (isEdit) {
            binding.toolbarAddNote.title = "Edit Note"
            binding.editTextTitle.setText(title)
            binding.editTextContent.setText(content)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_note_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        super.onPrepareOptionsMenu(menu)

        val deleteNoteMenu: MenuItem? = menu?.findItem(R.id.delete_note)
        deleteNoteMenu?.isVisible = isEdit

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.save_note -> {
                saveNote()
            }
        }
        return true
    }

    private fun saveNote() {
        val titleText = binding.editTextTitle.text.toString()
        val contentText = binding.editTextContent.text.toString()

        if (titleText.isNotEmpty()) {
            if (isEdit) {
                editNote(
                    noteId,
                    EditNoteRequest(
                        title = titleText,
                        content = contentText
                    )
                )
            } else {
                postNote(
                    AddNoteRequest(
                        uid = sessionManager.getUserId() ?: "",
                        title = titleText,
                        content = contentText
                    )
                )
            }

            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)

        } else {
            binding.editTextTitle.error = "Title is required"
        }
    }

    private fun postNote(note: AddNoteRequest) {

            showLoading(true)
            val client = ApiConfig.getApiService(this).postNote(note)

            client.enqueue(object : Callback<PostNoteResponse> {
                override fun onResponse(call: Call<PostNoteResponse>, response: Response<PostNoteResponse>) {
                    showLoading(false)
                    val apiResponse = response.body()
                    if (response.isSuccessful && apiResponse != null) {
                        Toast.makeText(this@AddNoteActivity, "Note Saved", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        if (response.code() == 401){
                            Toast.makeText(this@AddNoteActivity, "Token Expired", Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(this@AddNoteActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<PostNoteResponse>, t: Throwable) {
                    showLoading(false)
                    Toast.makeText(this@AddNoteActivity, t.message, Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun editNote(noteId: String, note: EditNoteRequest) {
        showLoading(true)
        val client = ApiConfig.getApiService(this).patchNote(noteId, note)

        client.enqueue(object : Callback<PatchNoteResponse> {
            override fun onResponse(call: Call<PatchNoteResponse>, response: Response<PatchNoteResponse>) {
                showLoading(false)
                val apiResponse = response.body()
                if (response.isSuccessful && apiResponse != null) {
                    Toast.makeText(this@AddNoteActivity, "Note Edited", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    if (response.code() == 401){
                        Toast.makeText(this@AddNoteActivity, "Unauthorized or token expired", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@AddNoteActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<PatchNoteResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@AddNoteActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.addNoteLoading.visibility = View.VISIBLE
        } else {
            binding.addNoteLoading.visibility = View.GONE
        }
    }
}

