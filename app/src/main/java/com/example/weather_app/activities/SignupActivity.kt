package com.example.weather_app.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.weather_app.R
import com.example.weather_app.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

    companion object {
        private const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            
            if (validateInput(email, password, confirmPassword)) {
                signUpWithEmailPassword(email, password)
            }
        }

        binding.btnGoogleSignUp.setOnClickListener {
            signUpWithGoogle()
        }

        binding.tvLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInput(email: String, password: String, confirmPassword: String): Boolean {
        var isValid = true

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Please enter a valid email"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        if (password.isEmpty() || password.length < 6) {
            binding.tilPassword.error = "Password must be at least 6 characters"
            isValid = false
        } else {
            binding.tilPassword.error = null
        }

        if (password != confirmPassword) {
            binding.tilConfirmPassword.error = "Passwords do not match"
            isValid = false
        } else {
            binding.tilConfirmPassword.error = null
        }

        return isValid
    }

    private fun signUpWithEmailPassword(email: String, password: String) {
        showLoading(true)
        lifecycleScope.launch {
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                navigateToWeatherScreen()
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@SignupActivity, "Sign up failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signUpWithGoogle() {
        showLoading(true)
        try {
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } catch (e: Exception) {
            showLoading(false)
            Toast.makeText(this, "Google Sign-Up failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                } else {
                    showLoading(false)
                    Toast.makeText(this, "Google sign up failed: No account found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                showLoading(false)
                val errorMessage = when (e.statusCode) {
                    10 -> "Google Sign-Up configuration error. Please check your SHA-1 certificate fingerprint in Firebase Console."
                    7 -> "Network error. Please check your internet connection."
                    12500 -> "Google Sign-Up configuration error. Please check your project configuration."
                    else -> "Google sign up failed: ${e.message}"
                }
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        showLoading(true)
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        lifecycleScope.launch {
            try {
                val authResult = auth.signInWithCredential(credential).await()
                if (authResult.user != null) {
                    navigateToWeatherScreen()
                } else {
                    showLoading(false)
                    Toast.makeText(this@SignupActivity, "Authentication failed: No user found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                showLoading(false)
                Toast.makeText(this@SignupActivity, "Authentication failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToWeatherScreen() {
        showLoading(false)
        startActivity(Intent(this, WeatherActivity::class.java))
        finish()
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnSignUp.isEnabled = !show
        binding.btnGoogleSignUp.isEnabled = !show
    }
} 