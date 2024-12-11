package com.example.fixu.ui.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.FragmentNotesBinding
import com.example.fixu.response.NoteDataItem
import java.text.SimpleDateFormat
import java.util.Locale


class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var notesViewModel: NotesViewModel
    private lateinit var saveNoteLauncher: ActivityResultLauncher<Intent>
    private lateinit var noteAdapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(requireContext())

        val factory = NotesViewModelFactory(requireActivity().application)
        notesViewModel = ViewModelProvider(this, factory).get(NotesViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesBinding.inflate(inflater,container, false)
        val view = binding.root


        noteAdapter = NoteAdapter { note ->
            openEditNote(note)
        }

        binding.rvNotesList.adapter = noteAdapter

        notesViewModel.notesLiveData.observe(viewLifecycleOwner) { notes ->
            setNotesData(notes)
        }

        notesViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }


        saveNoteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                notesViewModel.refreshNote()
            }
        }

        binding.btnAddNote.setOnClickListener {
            openAddNote()
        }


        return view
    }

    private fun openAddNote() {
        val intent = Intent(requireActivity(), AddNoteActivity::class.java)
        saveNoteLauncher.launch(intent)
    }
    private fun openEditNote(note: NoteDataItem) {
        val intent = Intent(requireContext(), AddNoteActivity::class.java).apply {
            putExtra("TITLE_EXTRA", note.title)
            putExtra("CONTENT_EXTRA", note.content)
            putExtra("NOTEID_EXTRA", note.id.toString())
        }
        saveNoteLauncher.launch(intent)
    }

    private fun setNotesData(noteData: List<NoteDataItem>) {
        _binding?.let {
            if (noteData.isEmpty()) {
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.rvNotesList.visibility = View.GONE
            } else {
                binding.emptyStateContainer.visibility = View.GONE
                binding.rvNotesList.visibility = View.VISIBLE

                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val sortedNoteData = noteData.sortedByDescending { item ->
                    runCatching { dateFormat.parse(item.createdAt) }.getOrNull()
                }

                noteAdapter.submitList(sortedNoteData) {
                    binding.rvNotesList.invalidateItemDecorations()
                    binding.rvNotesList.post {
                        binding.rvNotesList.scrollToPosition(0)
                    }
                }
            }
        }
    }


    private fun showLoading(isLoading: Boolean) {
        _binding.let {
            binding.notesLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}