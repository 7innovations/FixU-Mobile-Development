package com.example.fixu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.fixu.R
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.FragmentHomeBinding
import com.example.fixu.response.HistoryDataItem
import com.example.fixu.response.HistoryResponse
import com.example.fixu.response.QuotesResponse
import com.example.fixu.retrofit.ApiConfig
import com.example.fixu.ui.auth.LoginActivity
import com.example.fixu.ui.history.HistoryAdapter
import com.example.fixu.ui.history.HistoryFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        val layoutManager = LinearLayoutManager(requireContext())
        binding.rvHistoryHome.layoutManager = layoutManager

        getHistoryData()
        getQuoteImg()

        binding.btnViewMore.setOnClickListener {
            val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragment_container, HistoryFragment())
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }


        return view
    }

    private fun getQuoteImg() {
        showLoadingQuote(true)
        val client = ApiConfig.getApiService(requireContext()).getQuotes()
        client.enqueue(object: Callback<QuotesResponse> {
            override fun onResponse(
                call: Call<QuotesResponse>,
                response: Response<QuotesResponse>
            ) {
                if (view == null) return
                showLoadingQuote(false)
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        Glide.with(requireContext())
                            .load(responseBody.image)
                            .apply(RequestOptions.bitmapTransform(RoundedCorners(50)))
                            .into(binding.ivQuoteImage)
                    }
                }
            }

            override fun onFailure(call: Call<QuotesResponse>, t: Throwable) {
                if (view == null) return
                showLoadingQuote(false)
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
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
                   Toast.makeText(requireContext(), "Unauthorized: Invalid Token", Toast.LENGTH_SHORT).show()
                   invalidTokenLogout()
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
                binding.rvHistoryHome.visibility = View.GONE
            } else {
                binding.emptyStateContainer.visibility = View.GONE
                binding.rvHistoryHome.visibility = View.VISIBLE

                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val sortedHistoryData = historyData.sortedByDescending { item ->
                    runCatching { dateFormat.parse(item.createdAt) }.getOrNull()
                }

                val adapter = HistoryAdapter()
                adapter.submitList(sortedHistoryData.take(5))
                binding.rvHistoryHome.adapter = adapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        _binding.let {
            if (isLoading) {
                binding.historyHomeLoading.visibility = View.VISIBLE
            } else {
                binding.historyHomeLoading.visibility = View.GONE
            }
        }
    }

    private fun showLoadingQuote(isLoading: Boolean) {
        _binding.let {
            if (isLoading) {
                binding.quotesLoading.visibility = View.VISIBLE
            } else {
                binding.quotesLoading.visibility = View.GONE
            }
        }
    }

    private fun invalidTokenLogout() {
        sessionManager.clearSession()
        Firebase.auth.signOut()
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}