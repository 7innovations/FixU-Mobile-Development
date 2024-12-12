package com.example.fixu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AppDatabase
import com.example.fixu.database.Question
import com.example.fixu.database.SessionManager
import com.example.fixu.databinding.ActivityDiagnoseBinding
import com.example.fixu.response.MLResponse
import com.example.fixu.retrofit.ApiConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)
        database = AppDatabase.getInstance(this)

        btnNext = binding.btnNext

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
                    val radioButton = RadioButton(this).apply {
                        text = option
                        setTextAppearance(R.style.TextAppearance_App_RadioButton) // Gaya sesuai tema
                    }
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

        if (answers.size > currentQuestionIndex) {
            return true // Jawaban sudah ada, abaikan proses selanjutnya
        }

        val answer = when (question.answerType) {
            "radio" -> {
                val selectedId = binding.radioGroupAnswers.checkedRadioButtonId
                if (selectedId != -1) {
                    val radioButton = findViewById<RadioButton>(selectedId)
                    radioButton.text.toString()
                } else {
                    null
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

        if (answers.size <= currentQuestionIndex) {
            answers.add(answer)
            Log.d("Answer", "Answers: $answers")
        }
        return true
    }

    private fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            if (!saveAnswer()) return

            currentQuestionIndex++
            showQuestion()

            if (currentQuestionIndex == questions.size - 1) {
                binding.btnNext.text = getString(R.string.submit)
            }
        } else {
            if (saveAnswer()) {
                submitAnswers()
            }
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
                age = answers[1].toIntOrNull() ?: 0,
                dietaryHabits = answers[5],
                familyHistory = answers[9],
                financialStress = answers[8].toIntOrNull() ?: 0,
                gender = answers[0],
                suicidalThoughts = answers[6],
                jobSatisfaction = answers[3].toIntOrNull() ?: 0,
                sleepDuration = answers[4],
                workHours = answers[7].toIntOrNull() ?: 0,
                workPressure = answers[2].toIntOrNull() ?: 0
            )

            Log.d("DiagnoseActivity", "Answers for Professional: $answersProfessional")

            sendProfessionalData(answersProfessional)

        } else if (userStatus == "Student") {
            val answersStudent = AnswersStudent(
                academicPressure = answers[2].toIntOrNull() ?: 0,
                age = answers[1].toIntOrNull() ?: 0,
                dietaryHabits = answers[5],
                familyHistory = answers[9],
                financialStress = answers[8].toIntOrNull() ?: 0,
                gender = answers[0],
                suicidalThoughts = answers[6],
                sleepDuration = answers[4],
                studyHours = answers[7].toIntOrNull() ?: 0,
                studySatisfaction = answers[3].toIntOrNull() ?: 0
            )

            Log.d("DiagnoseActivity", "Answers for Student: $answersStudent")
            sendStudentData(answersStudent)
        }
    }

    private fun sendProfessionalData(answers: AnswersProfessional) {
        val apiService = ApiConfig.getApiService(this)
        val call = apiService.postProfessionalAnswers(answers)
        btnNext.isEnabled = false
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                val apiResponse = response.body()
                if (response.isSuccessful && apiResponse != null) {
                    val apiResponseObject = apiResponse.result.firstOrNull()

                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponseObject?.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponseObject?.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponseObject?.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    showLoading(false)
                    startActivity(intent)
                    finish()
                } else {
                    btnNext.isEnabled = true
                    showLoading(false)
                    if (response.code() == 401){
                        Toast.makeText(this@DiagnoseActivity, "Token Expired", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@DiagnoseActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MLResponse>, t: Throwable) {
                btnNext.isEnabled = true
                showLoading(false)
                Log.d("Error API", "Failed ${t.message}")
                Toast.makeText(this@DiagnoseActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendStudentData(answers: AnswersStudent) {
        val apiService = ApiConfig.getApiService(this)
        val call = apiService.postStudentAnswers(answers)
        btnNext.isEnabled = false
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                val apiResponse = response.body()
                if (response.isSuccessful && apiResponse != null) {
                    val apiResponseObject = apiResponse.result.firstOrNull()

                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponseObject?.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponseObject?.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponseObject?.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    showLoading(false)
                    finish()
                } else {
                    btnNext.isEnabled = true
                    showLoading(false)
                    if (response.code() == 401){
                        Toast.makeText(this@DiagnoseActivity, "Token Expired", Toast.LENGTH_SHORT).show()

                    } else {
                        Toast.makeText(this@DiagnoseActivity, "Failed to reach API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<MLResponse>, t: Throwable) {
                btnNext.isEnabled = true
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