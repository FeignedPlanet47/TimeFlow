package ru.it.timeflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import ru.it.timeflow.presentation.navigation.TimeFlowApp
import ru.it.timeflow.ui.theme.TimeFlowTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TimeFlowTheme {
                TimeFlowApp()
            }
        }
    }
}
