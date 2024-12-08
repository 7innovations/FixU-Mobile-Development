package com.example.fixu

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.fixu.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbarAddNote
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)

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
        }
        return true
    }
}