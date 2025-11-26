package com.example.mimapa.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.mimapa.R

@Composable
fun AppBackground(){
    Box(modifier = Modifier.Companion.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.autobus_almeria),
            contentDescription = "Background",
            modifier = Modifier.Companion
                .fillMaxSize()
                .blur(8.dp),
            contentScale = ContentScale.Companion.Crop
        )
    }
}
