package com.example.fixu.ui.notes

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fixu.response.NoteDataItem
import com.example.fixu.response.NoteResponse
import com.example.fixu.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val _notesLiveData = MutableLiveData<List<NoteDataItem>>()
    val notesLiveData: LiveData<List<NoteDataItem>> = _notesLiveData

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        getNotes()
    }

    fun refreshNote() {
        getNotes()
    }

     private fun getNotes() {
        _isLoading.value = true
        ApiConfig.getApiService(getApplication()).getNotes().enqueue(object : Callback<NoteResponse> {
            override fun onResponse(call: Call<NoteResponse>, response: Response<NoteResponse>) {
                if (response.isSuccessful) {
                    _isLoading.value = false
                    response.body()?.let { noteResponse ->
                        _notesLiveData.value = noteResponse.data
                    }
                }
                else {
                    _isLoading.value = false
                    Log.d("API Error", "Code: ${response.code()}")
                    Log.d("API Error", "Code: ${response.message()} \n ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<NoteResponse>, t: Throwable) {
                _isLoading.value = false
            }
        })
    }
}