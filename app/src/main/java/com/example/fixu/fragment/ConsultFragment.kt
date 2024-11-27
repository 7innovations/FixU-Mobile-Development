package com.example.fixu.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.fixu.ApiTestActivity
import com.example.fixu.R
import com.example.fixu.databinding.FragmentConsultBinding
import com.example.fixu.databinding.FragmentProfileBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ConsultFragment : Fragment() {

    private var _binding: FragmentConsultBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentConsultBinding.inflate(inflater,container, false)
        val view = binding.root

        binding.btnTestSurvey.setOnClickListener {
            val intent = Intent(requireActivity(), ApiTestActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}