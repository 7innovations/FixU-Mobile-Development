package com.example.fixu.ui.history

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fixu.ui.auth.LoginActivity
import com.example.fixu.R
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.FragmentHistoryBinding
import com.example.fixu.response.HistoryDataItem
import com.example.fixu.response.HistoryResponse
import com.example.fixu.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager  = SessionManager(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val view = binding.root

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryList.layoutManager = layoutManager

        getHistoryData()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar: Toolbar = binding.toolbarMain
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_arrow_back_24)
            setDisplayShowTitleEnabled(false)
        }

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().supportFragmentManager.popBackStack()
            }
        })
    }

    private fun getHistoryData() {
        showLoading(true)
        val client = ApiConfig.getApiService(requireContext()).getHistory()
        client.enqueue(object: Callback<HistoryResponse> {
            override fun onResponse(
                call: Call<HistoryResponse>,
                response: Response<HistoryResponse>
            ) {
                if (view == null) return
                showLoading(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        setHistoryData(responseBody.data)
                    }
                } else if (response.code() == 401) {
                    Toast.makeText(requireContext(), "Session Ended: Token Expired", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<HistoryResponse>, t: Throwable) {
                if (view == null) return
                showLoading(false)
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun setHistoryData(historyData: List<HistoryDataItem>) {
        _binding?.let {
            if (historyData.isEmpty()) {
                binding.emptyStateContainer.visibility = View.VISIBLE
                binding.rvHistoryList.visibility = View.GONE
            } else {
                binding.emptyStateContainer.visibility = View.GONE
                binding.rvHistoryList.visibility = View.VISIBLE

                val dateFormat =
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val sortedHistoryData = historyData.sortedByDescending { item ->
                    runCatching { dateFormat.parse(item.createdAt) }.getOrNull()
                }

                val adapter = HistoryAdapter()
                adapter.submitList(sortedHistoryData)
                binding.rvHistoryList.adapter = adapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _binding.let {
            if (isLoading) {
                binding.historyListLoading.visibility = View.VISIBLE
            } else {
                binding.historyListLoading.visibility = View.GONE
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}