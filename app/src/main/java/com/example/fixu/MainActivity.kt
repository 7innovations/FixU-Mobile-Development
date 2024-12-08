package com.example.fixu

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.fixu.fragment.ConsultFragment
import com.example.fixu.fragment.DiagnoseFragment
import com.example.fixu.fragment.HomeFragment
import com.example.fixu.notes.NotesFragment
import com.example.fixu.fragment.ProfileFragment
import com.example.fixu.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        binding.bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_menu -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.diagnose_menu -> {
                    loadFragment(DiagnoseFragment())
                    true
                }R.id.consult_menu -> {
                    loadFragment(ConsultFragment())
                    true
                }
                R.id.notes_menu -> {
                    loadFragment(NotesFragment())
                    true
                }
                R.id.profile_menu -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

}