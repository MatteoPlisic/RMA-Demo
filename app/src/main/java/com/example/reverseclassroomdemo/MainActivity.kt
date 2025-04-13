package com.example.reverseclassroomdemo

import androidx.credentials.CredentialManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.GetCredentialRequest
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.reverseclassroomdemo.login.LoginScreen
import com.example.reverseclassroomdemo.login.LoginViewModel

import com.example.reverseclassroomdemo.students.StudentsScreen
import com.example.reverseclassroomdemo.ui.screens.CitiesScreen
import com.example.reverseclassroomdemo.movies.MoviesScreen

import com.example.reverseclassroomdemo.ui.theme.ReverseClassroomDemoTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           LoginScreen(
                modifier = Modifier.fillMaxSize(),
                viewModel = LoginViewModel(LocalContext.current)
            )
            //CitiesScreen()
            //StudentsScreen()
            //MoviesScreen()
        }
    }
}


