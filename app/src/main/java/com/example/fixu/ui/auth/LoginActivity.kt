package com.example.fixu.ui.auth

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.MainActivity
import com.example.fixu.database.SessionManager
import com.example.fixu.database.LoginRequest
import com.example.fixu.databinding.ActivityLoginBinding
import com.example.fixu.response.LoginResponse
import com.example.fixu.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        onFocusEmailListener()
        onFocusPasswordListener()

        sessionManager = SessionManager(this)

        binding.loginButton.setOnClickListener {
            if (isInputNotEmpty()) {
                loginAccount()
            } else {
                binding.emailContainer.error = validateEmail()
                binding.passwordContainer.error = validatePassword()
            }

        }

        binding.signupDirect.setOnClickListener {
            moveToRegister()
        }

    }

    public override fun onStart() {
        super.onStart()

        if (sessionManager.isLoggedIn()) {
            moveToMain()
        }
    }

    private fun loginAccount() {

        showLoading(true)

        email = binding.edtEmail.getText().toString().trim()
        password = binding.edtPassword.getText().toString().trim()

        val loginRequest = LoginRequest(email, password)

        val client = ApiConfig.getApiService(this).login(loginRequest)
        client.enqueue(object: Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                val loginResponseBody = response.body()
                if (response.isSuccessful && loginResponseBody != null) {
                    loginResponseBody.let {
                        sessionManager.saveSession(
                            token = it.token,
                            userId = it.user.uid,
                            email = it.user.email,
                            name = it.user.fullname,
                            whatsapp = it.user.whatsapp
                        )
                        Toast.makeText(this@LoginActivity, it.message, Toast.LENGTH_SHORT).show()
                        moveToMain()
                    }
                }
                else {
                    Toast.makeText(this@LoginActivity, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Log.d("Error API", "Error: ${t.message}")
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun moveToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.signInLoading.visibility = View.VISIBLE
        } else {
            binding.signInLoading.visibility = View.GONE
        }
    }

    private fun onFocusEmailListener() {
        binding.edtEmail.setOnFocusChangeListener{ _, focused ->
            if (!focused) {
                binding.emailContainer.error = validateEmail()
            }
        }
    }

    private fun validateEmail(): String? {
        val emailText = binding.edtEmail.text.toString()
        if (emailText.isEmpty()){
            return "Email is required"
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            return "Invalid email address"
        }

        return null
    }

    private fun onFocusPasswordListener() {
        binding.edtPassword.setOnFocusChangeListener{ _, focused ->
            if (!focused) {
                binding.passwordContainer.error = validatePassword()
            }
        }
    }

    private fun validatePassword(): String? {
        val passwordText = binding.edtPassword.text.toString()
        if (passwordText.isEmpty()) {
            return "Password is required"
        }

        return null
    }

    private fun isInputNotEmpty(): Boolean {
        val emailText = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()

        return emailText.isNotEmpty() && password.isNotEmpty()
    }
}