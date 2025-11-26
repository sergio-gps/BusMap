package com.example.mimapa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.mimapa.ui.screens.MainNavigation
import com.example.mimapa.ui.theme.MiMapaTheme
import com.example.mimapa.util.FindBusLine
import com.example.mimapa.util.NearestLocation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //FindBusLine(this).main()
        setContent {
            MiMapaTheme {
                MainNavigation()
            }
        }
    }
}
