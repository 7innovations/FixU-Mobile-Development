package com.example.fixu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AppDatabase
import com.example.fixu.database.Question
import com.example.fixu.databinding.ActivityDiagnoseBinding
import com.example.fixu.response.MLResponse
import com.example.fixu.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiagnoseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDiagnoseBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase
    private var questions = listOf<Question>()
    private var currentQuestionIndex = 0
    private var answers = mutableListOf<String>()
    private var userStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        database = AppDatabase.getInstance(this)

        userStatus = intent.getStringExtra("USER_STATUS") ?: ""

        binding.progressBar.max = 100 // Setting max to 100 to represent percentages
        updateProgressBar()

        loadQuestions()

        binding.btnNext.setOnClickListener {
            saveAnswer()
            moveToNextQuestion()
        }

        binding.btnBack.setOnClickListener{
            BackToBeforeQuestion()
        }

    }

    private fun loadQuestions() {
        CoroutineScope(Dispatchers.IO).launch {
            questions = database.questionDao().getQuestionsByStatus(userStatus)
            withContext(Dispatchers.Main) {
                if (questions.isNotEmpty()) {
                    showQuestion()
                } else {
                    Toast.makeText(this@DiagnoseActivity, "No questions found", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        binding.tvQuestion.text = question.questionText

        binding.radioGroupAnswers.clearCheck()

        when (question.answerType) {
            "radio" -> {
                binding.viewSwitcher.displayedChild = 0
                binding.radioGroupAnswers.removeAllViews()
                question.options?.forEach { option ->
                    val radioButton = RadioButton(this).apply { text = option }
                    binding.radioGroupAnswers.addView(radioButton)
                }
            }
            "text" -> {
                binding.viewSwitcher.displayedChild = 1
                binding.editTextAnswer.setText("")
            }
        }
        updateProgressBar()
    }

    private fun saveAnswer(): Boolean {
        val question = questions[currentQuestionIndex]
        val answer = when (question.answerType) {
            "radio" -> {
                val selectedId = binding.radioGroupAnswers.checkedRadioButtonId
                if (selectedId != -1) {
                    val radioButton = findViewById<RadioButton>(selectedId)
                    radioButton.text.toString()
                } else {
                    null // Tidak ada jawaban
                }
            }
            "text" -> {
                val inputText = binding.editTextAnswer.text.toString()
                if (inputText.isNotEmpty()) inputText else null
            }
            else -> null
        }

        if (answer.isNullOrEmpty()) {
            showAlertDialog("Please answer the question before proceeding.")
            return false
        }

        answers.add(answer)
        return true
    }


    private fun moveToNextQuestion() {
        if (!saveAnswer()) return // Berhenti jika jawaban tidak valid

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            showQuestion()
            if (currentQuestionIndex == questions.size - 1) {
                binding.btnNext.text = getString(R.string.submit)
            }
        } else {
            submitAnswers()
        }
    }


    private fun BackToBeforeQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--

            if (answers.isNotEmpty()) {
                answers.removeAt(answers.size - 1)
            }

            binding.btnNext.text = if (currentQuestionIndex == questions.size - 1) {
                getString(R.string.submit)
            } else {
                getString(R.string.next)
            }

            showQuestion()
        } else {
            Toast.makeText(this, "You are already on the first question", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("Incomplete Answer")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun updateProgressBar() {
        val progress = ((currentQuestionIndex + 1).toFloat() / questions.size * 100).toInt()
        binding.progressBar.progress = progress
    }

    private fun submitAnswers() {
        if (userStatus == "Professional") {
            val answersProfessional = AnswersProfessional(
                userId = sessionManager.getUserId() ?: "",
                gender = answers[0],
                age = answers[1].toIntOrNull() ?: 0,
                workPressure = answers[2].toIntOrNull() ?: 0,
                jobSatisfaction = answers[3].toIntOrNull() ?: 0,
                sleepDuration = answers[4],
                dietaryHabits = answers[5],
                suicidalThoughts = answers[6],
                workHours = answers[7].toIntOrNull() ?: 0,
                financialStress = answers[8].toIntOrNull() ?: 0,
                familyHistory = answers[9]
            )

            // Tampilkan data pada Logcat
            Log.d("DiagnoseActivity", "Answers for Professional: $answersProfessional")

            sendProfessionalData(answersProfessional)

        } else if (userStatus == "Student") {
            val answersStudent = AnswersStudent(
                userId = sessionManager.getUserId() ?: "",
                gender = answers[0],
                age = answers[1].toIntOrNull() ?: 0,
                academicPressure = answers[2].toIntOrNull() ?: 0,
                studySatisfaction = answers[3].toIntOrNull() ?: 0,
                sleepDuration = answers[4],
                dietaryHabits = answers[5],
                suicidalThoughts = answers[6],
                studyHours = answers[7].toIntOrNull() ?: 0,
                financialStress = answers[8].toIntOrNull() ?: 0,
                familyHistory = answers[9]
            )

            // Tampilkan data pada Logcat
            Log.d("DiagnoseActivity", "Answers for Student: $answersStudent")
            sendStudentData(answersStudent)
        }
    }

    private fun sendProfessionalData(answers: AnswersProfessional) {
        val apiService = ApiConfig.getApiService(this)
        val call = apiService.postProfessionalAnswers(answers)
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                showLoading(false)
                val apiResponse = response.body()
                if (response.isSuccessful && apiResponse != null) {
                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponse.result.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponse.result.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponse.result.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    if (response.code() == 401){
                        Toast.makeText(this@DiagnoseActivity, "Token Expired", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@DiagnoseActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MLResponse>, t: Throwable) {
                showLoading(false)
                Log.d("Error API", "Failed ${t.message}")
                Toast.makeText(this@DiagnoseActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendStudentData(answers: AnswersStudent) {
        val apiService = ApiConfig.getApiService(this)
        val call = apiService.postStudentAnswers(answers)
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                showLoading(false)
                val apiResponse = response.body()
                if (response.isSuccessful && apiResponse != null) {

                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponse.result.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponse.result.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponse.result.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    if (response.code() == 401){
                        Toast.makeText(this@DiagnoseActivity, "Token Expired", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@DiagnoseActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MLResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@DiagnoseActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.resultLoading.visibility = View.VISIBLE
        } else {
            binding.resultLoading.visibility = View.GONE
        }
    }

}