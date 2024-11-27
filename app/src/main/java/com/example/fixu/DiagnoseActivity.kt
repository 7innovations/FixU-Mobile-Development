package com.example.fixu

import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.database.AnswersProfessional
import com.example.fixu.database.AnswersStudent
import com.example.fixu.database.AppDatabase
import com.example.fixu.database.Question
import com.example.fixu.databinding.ActivityDiagnoseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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

        loadQuestions()

        binding.btnNext.setOnClickListener {
            saveAnswer()
            moveToNextQuestion()
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
        } else {
            submitAnswers()
        }
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
        }
    }

}