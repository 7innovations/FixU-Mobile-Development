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
import com.example.fixu.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

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
                loginWithFirebase()
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

        val currentUser = auth.currentUser
        if (currentUser != null) {
            moveToMain()
        }
    }

    private fun loginWithFirebase() {
        email = binding.edtEmail.text.toString().trim()
        password = binding.edtPassword.text.toString().trim()
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    if (user != null) {
                        getToken { token ->
                            sessionManager.saveSession(
                                token = token,
                                userId = user.uid,
                                email = user.email ?: "",
                                name = user.displayName ?: ""
                            )
                            moveToMain()
                        }
                    } else {
                        Toast.makeText(this, "User not found after login", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Wrong email or password", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun getToken(callback: (String) -> Unit) {
        FirebaseAuth.getInstance().currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token ?: ""
                callback(idToken)
            } else {
                Log.e("Token Firebase", "Failed to get token", task.exception)
                callback("")
            }
        }
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