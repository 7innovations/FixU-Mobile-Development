package com.example.fixu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.Toast
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
    private lateinit var database: AppDatabase
    private var questions = listOf<Question>()
    private var currentQuestionIndex = 0
    private var answers = mutableListOf<String>()
    private var userStatus = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDiagnoseBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun saveAnswer() {
        val question = questions[currentQuestionIndex]
        val answer = when (question.answerType) {
            "radio" -> {
                val selectedId = binding.radioGroupAnswers.checkedRadioButtonId
                if (selectedId != -1) {
                    val radioButton = findViewById<RadioButton>(selectedId)
                    radioButton.text.toString()
                } else {
                    ""
                }
            }
            "text" -> binding.editTextAnswer.text.toString()
            else -> ""
        }
        answers.add(answer)
    }

    private fun moveToNextQuestion() {
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


    private fun updateProgressBar() {
        val progress = ((currentQuestionIndex + 1).toFloat() / questions.size * 100).toInt()
        binding.progressBar.progress = progress
    }

    private fun submitAnswers() {
        if (userStatus == "Professional") {
            val answersProfessional = AnswersProfessional(
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
        val apiService = ApiConfig.getApiService()
        val call = apiService.postProfessionalAnswers(answers)
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val apiResponse = response.body()
                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponse?.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponse?.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponse?.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    showLoading(false)
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            "Unknown error occurred"
                        }
                    } else {
                        "No error details available"
                    }
                    Toast.makeText(this@DiagnoseActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<MLResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@DiagnoseActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendStudentData(answers: AnswersStudent) {
        val apiService = ApiConfig.getApiService()
        val call = apiService.postStudentAnswers(answers)
        showLoading(true)

        call.enqueue(object : Callback<MLResponse> {
            override fun onResponse(call: Call<MLResponse>, response: Response<MLResponse>) {
                if (response.isSuccessful) {
                    showLoading(false)
                    val apiResponse = response.body()
                    val intent = Intent(this@DiagnoseActivity, ResultActivity::class.java)
                    intent.putExtra(ResultActivity.EXTRA_FEEDBACK, apiResponse?.feedback)
                    intent.putExtra(ResultActivity.EXTRA_PROBABILITY, apiResponse?.probability.toString())
                    intent.putExtra(ResultActivity.EXTRA_RESULT, apiResponse?.result)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    showLoading(false)
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = if (errorBody != null) {
                        try {
                            JSONObject(errorBody).getString("error")
                        } catch (e: Exception) {
                            "Unknown error occurred"
                        }
                    } else {
                        "No error details available"
                    }
                    Toast.makeText(this@DiagnoseActivity, errorMessage, Toast.LENGTH_SHORT).show()
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