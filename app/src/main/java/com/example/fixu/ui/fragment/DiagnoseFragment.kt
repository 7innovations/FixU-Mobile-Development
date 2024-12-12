package com.example.fixu.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.fixu.DiagnoseActivity
import com.example.fixu.R
import com.example.fixu.database.AppDatabase
import com.example.fixu.database.Question
import com.example.fixu.databinding.FragmentDiagnoseBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiagnoseFragment : Fragment() {

    private var _binding: FragmentDiagnoseBinding? = null
    private val binding get() = _binding!!
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiagnoseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = AppDatabase.getInstance(requireContext())
        insertInitialQuestions()

        binding.btnProfessional.setOnClickListener {
            navigateToDiagnoseActivity("Professional")
        }

        binding.btnStudent.setOnClickListener {
            navigateToDiagnoseActivity("Student")
        }
    }

    private fun navigateToDiagnoseActivity(userStatus: String) {
        val intent = Intent(requireContext(), DiagnoseActivity::class.java).apply {
            putExtra("USER_STATUS", userStatus)
        }
        startActivity(intent)
    }


    private fun insertInitialQuestions() {
        val questions = listOf(
            Question(
                status = "Professional",
                questionText = "What is your gender?",
                answerType = "radio",
                options = listOf("Male", "Female")
            ),
            Question(
                status = "Professional",
                questionText = "How old are you?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Professional",
                questionText = "How would you rate the work pressure you experience?\n1 = Minimal stress, easy to manage!\n2 = Some stress, but manageable\n3 = Just average i guess\n4 = It is frequently punching me\n5 = Constantly overwhelmed, im cooked!",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "How satisfied are you with your current job?\n1: I want to quit.\n2: I kinda hate this job :(\n3: It is a love-hate relationship with it\n4: I kinda love this job\n5: I love my job to the bone",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "How many hours of sleep do you normally get each night?",
                answerType = "radio",
                options = listOf("Less than 5 hours", "5-6 hours", "7-8 hours", " More than 8 hours")
            ),
            Question(
                status = "Professional",
                questionText = "How would you describe your dietary habits?",
                answerType = "radio",
                options = listOf("Healthy", "Moderate", "Unhealthy")
            ),
            Question(
                status = "Professional",
                questionText = "Have you ever had self-harm or suicidal thoughts?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Professional",
                questionText = "On average, how many hours do you work each day?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Professional",
                questionText = "How much financial stress are you currently experiencing?\n1. Im doing well, zero stress on that front\n2. I rarely struggle with it\n3. Its all about up and down\n4. Frequently feel that financial stress\n5. Always stressful with finance :(",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Professional",
                questionText = "Do you have a family history of mental illness?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Student",
                questionText = "What is your gender?",
                answerType = "radio",
                options = listOf("Male", "Female")
            ),
            Question(
                status = "Student",
                questionText = "How old are you?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Student",
                questionText = "How would you rate the academic pressure you experience?\n" +
                        "1 = Minimal pressure, easy to manage!\n" +
                        "2 = Some pressure, but manageable\n" +
                        "3 = Just average level i think\n" +
                        "4 = It is frequently punching me\n" +
                        "5 = Constantly overwhelmed, im cooked!",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "How satisfied are you with your current academic progress?\n" +
                        "1: I want to drop out.\n" +
                        "2: I kinda hate my studies :(\n" +
                        "3: It is a love-hate relationship with studies\n" +
                        "4: I kinda love my studies\n" +
                        "5: I love it to the bone",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "How many hours of sleep do you normally get each night?",
                answerType = "radio",
                options = listOf("Less than 5 hours", "5-6 hours", "7-8 hours", " More than 8 hours")
            ),
            Question(
                status = "Student",
                questionText = "How would you describe your dietary habits?",
                answerType = "radio",
                options = listOf("Healthy", "Moderate", "Unhealthy")
            ),
            Question(
                status = "Student",
                questionText = "Have you ever had self-harm or suicidal thoughts?",
                answerType = "radio",
                options = listOf("Yes", "No")
            ),
            Question(
                status = "Student",
                questionText = "How many hours do you spend studying on an average day?",
                answerType = "text",
                options = null
            ),
            Question(
                status = "Student",
                questionText = "How much financial stress are you currently experiencing?\n" +
                        "1. Im doing well, zero stress on that front\n" +
                        "2. I rarely struggle with it\n" +
                        "3. Its all about up and down\n" +
                        "4. Frequently feel that financial stress\n" +
                        "5. Always stressful with my financial :(",
                answerType = "radio",
                options = listOf("1", "2", "3", "4", "5")
            ),
            Question(
                status = "Student",
                questionText = "Is there a family history of mental health issues?",
                answerType = "radio",
                options = listOf("Yes", "No")
            )
        )

        lifecycleScope.launch {
            val existingQuestions = database.questionDao().getQuestionsByStatus("Professional") +
                    database.questionDao().getQuestionsByStatus("Student")

            if (existingQuestions.isEmpty()) {
                withContext(Dispatchers.IO) {
                    database.questionDao().insertQuestions(questions)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
