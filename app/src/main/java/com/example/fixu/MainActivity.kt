package com.example.fixu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.fixu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_menu -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.notes_menu -> {
                    replaceFragment(NotesFragment())
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}