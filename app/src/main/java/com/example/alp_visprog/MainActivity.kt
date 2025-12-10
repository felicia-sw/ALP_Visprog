package com.example.alp_visprog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.alp_visprog.ui.route.AppRouting
import com.example.alp_visprog.ui.theme.ALP_VisprogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ALP_VisprogTheme {
                AppRouting()
            }
        }
    }
}
