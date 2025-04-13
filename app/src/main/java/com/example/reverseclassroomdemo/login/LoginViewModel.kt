package com.example.reverseclassroomdemo.login

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reverseclassroomdemo.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LoginViewModel(private val context: Context) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var showPassword by mutableStateOf(false)



    fun onEmailLogin(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("LoginViewModel", "Login successful: ${task.result?.user?.email}")
                    Toast.makeText(context, "Welcome ${task.result?.user?.email}", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("LoginViewModel", "Login failed", task.exception)
                    Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun onEmailChanged(value: String) {
        email = value
    }

    fun onPasswordChanged(value: String) {
        password = value
    }

    fun togglePasswordVisibility() {
        showPassword = !showPassword
    }

    fun onGoogleSignIn(context: Context) {
        val credentialManager = CredentialManager.create(context)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.default_web_client_id))
            .setNonce("")
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                val result = credentialManager.getCredential(request = request, context = context)
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val email = googleIdTokenCredential.displayName
                Log.i("LoginViewModel", "Google ID Token: $googleIdToken")

                Toast.makeText(context, "Google ID Token: $googleIdToken", Toast.LENGTH_SHORT).show()
                Toast.makeText(context, "Welcome $email", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Error getting credential", e)
            }
        }
    }



}
