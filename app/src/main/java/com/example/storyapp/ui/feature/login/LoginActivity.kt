package com.example.storyapp.ui.feature.login

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import com.example.storyapp.R
import com.example.storyapp.data.story.StoryResult
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.ui.feature.factory.LoginViewModelFactory
import com.example.storyapp.ui.feature.main.HomeActivity
import com.example.storyapp.ui.feature.story.StoryApp

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel: LoginViewModel by viewModels {
        LoginViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState != null) {
            val emailText = savedInstanceState.getString("email")
            val passwordText = savedInstanceState.getString("password")

            binding.emailEditText.setText(emailText)
            binding.passwordEditText.setText(passwordText)
        }
        setupUI()
        observeViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("email", binding.emailEditText.text.toString())
        outState.putString("password", binding.passwordEditText.text.toString())
    }

    private fun setupUI() {
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()


            if (validateInputs(email, password)) {
                loginViewModel.login(email, password)
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            binding.emailEditTextLayout.error = getString(R.string.error_empty_email)
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailEditTextLayout.error = getString(R.string.validationEmail)
            isValid = false
        } else {
            binding.emailEditTextLayout.error = null
        }

        if (password.isEmpty()) {
            binding.passwordEditTextLayout.error = getString(R.string.error_empty_password)
            isValid = false
        } else if (password.length < 8) {
            binding.passwordEditTextLayout.error = getString(R.string.validationPassword)
            isValid = false
        } else {
            binding.passwordEditTextLayout.error = null
        }
        return isValid
    }

    private fun observeViewModel() {
        loginViewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) {
                showProgressBar()
            } else {
                hideProgressBar()
            }
        })

        loginViewModel.loginResult.observe(this, Observer { storyResult ->
            when (storyResult) {
                is StoryResult.Loading -> {
                    showProgressBar()
                }

                is StoryResult.Success -> {
                    hideProgressBar()
                    Toast.makeText(this, getString(R.string.login_success), Toast.LENGTH_SHORT)
                        .show()

                    val appWidgetManager = AppWidgetManager.getInstance(this)
                    val componentName = ComponentName(this, StoryApp::class.java)
                    val ids = appWidgetManager.getAppWidgetIds(componentName)
                    Log.d("LoginActivity", "Widget IDs: ${ids.joinToString()}")
                    appWidgetManager.notifyAppWidgetViewDataChanged(ids, R.id.stack_view)

                    val updateIntent = Intent(this, StoryApp::class.java)
                    updateIntent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                    updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    sendBroadcast(updateIntent)

                    val homeIntent = Intent(this, HomeActivity::class.java)
                    homeIntent.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(homeIntent)
                    finish()
                }

                is StoryResult.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, storyResult.error, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.w("LoginActivity", "Unexpected StoryResult: $storyResult")
                }
            }
        })
    }

    private fun showProgressBar() {
        binding.btnLogin.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.btnLogin.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }
}