package com.example.fixu.notes

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.fixu.AddNoteActivity
import com.example.fixu.LoginActivity
import com.example.fixu.SessionManager
import com.example.fixu.databinding.FragmentNotesBinding
import com.example.fixu.response.NoteDataItem
import com.example.fixu.response.NoteResponse
import com.example.fixu.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class NotesFragment : Fragment() {

    private var _binding: FragmentNotesBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager
    private lateinit var addNoteLauncher: ActivityResultLauncher<Intent>

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

        addNoteLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                getNotesData()
            }
        }

        binding.btnAddNote.setOnClickListener {
            openAddNote()
        }

        getNotesData()

        return view
    }

    private fun openAddNote() {
        val intent = Intent(requireActivity(), AddNoteActivity::class.java)
        addNoteLauncher.launch(intent)
    }

    private fun getNotesData() {
        showLoading(true)
        val client = ApiConfig.getApiService(requireContext()).getNotes()
        client.enqueue(object: Callback<NoteResponse> {
            override fun onResponse(
                call: Call<NoteResponse>,
                response: Response<NoteResponse>
            ) {
                if (view == null) return
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setNotesData(responseBody.data)
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(requireContext(), "Unauthorized or token expired", Toast.LENGTH_SHORT).show()
                    logoutWhenTokenExpired()
                } else {
                    Log.d("API Error", "Code: ${response.code()}")
                    Log.d("API Error", "Code: ${response.message()} \n ${response.errorBody()}")
                    Toast.makeText(requireContext(), "Failed to load notes data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                if (view == null) return
                showLoading(false)
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setNotesData(noteData: List<NoteDataItem>) {
        _binding?.let {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val sortedNoteData = noteData.sortedByDescending { item ->
                runCatching { dateFormat.parse(item.createdAt) }.getOrNull()
            }

            val adapter = NoteAdapter()
            adapter.submitList(sortedNoteData)
            binding.rvNotesList.adapter = adapter
        }
    }

    fun logoutWhenTokenExpired() {
        sessionManager.clearSession()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun showLoading(isLoading: Boolean) {
        _binding.let {
            if (isLoading) {
                binding.notesLoading.visibility = View.VISIBLE
            } else {
                binding.notesLoading.visibility = View.GONE
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}