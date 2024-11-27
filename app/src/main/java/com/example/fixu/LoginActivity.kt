package com.example.fixu

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fixu.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        onFocusEmailListener()
        onFocusPasswordListener()


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
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun loginAccount() {
        showLoading(true)
        email = binding.edtEmail.getText().toString().trim()
        password = binding.edtPassword.getText().toString().trim()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showLoading(false)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    showLoading(false)
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
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