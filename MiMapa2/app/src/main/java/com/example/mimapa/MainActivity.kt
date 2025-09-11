package com.example.mimapa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.mimapa.ui.theme.MiMapaTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiMapaTheme {
                MainNavigation()
            }
        }
    }
}

/*
@Preview
@Composable
fun MainNavigationPreview(){
    MiMapaTheme {
        MainNavigation()
    }
}*/