package com.example.fixu.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fixu.AddNoteActivity
import com.example.fixu.LoginActivity
import com.example.fixu.R
import com.example.fixu.SessionManager
import com.example.fixu.databinding.FragmentNotesBinding
import com.example.fixu.databinding.FragmentProfileBinding


class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater,container, false)
        val view = binding.root

        binding.btnAddNote.setOnClickListener {
            openAddNote()
        }

        return view
    }

    private fun openAddNote() {
        val intent = Intent(requireActivity(), AddNoteActivity::class.java)
        startActivity(intent)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}