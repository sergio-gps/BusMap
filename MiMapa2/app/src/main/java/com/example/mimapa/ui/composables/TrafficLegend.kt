package com.example.mimapa.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mimapa.data.model.TrafficSegment

/**
 * Composable que muestra una leyenda de los colores de tráfico.
 */
@Composable
fun TrafficLegend(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .padding(16.dp)
            .background(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tráfico",
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            TrafficLegendItem(
                color = Color(0xFF4CAF50),
                label = "Fluido"
            )
            TrafficLegendItem(
                color = Color(0xFFFF9800),
                label = "Lento"
            )
            TrafficLegendItem(
                color = Color(0xFFF44336),
                label = "Atasco"
            )
        }
    }
}

@Composable
private fun TrafficLegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Surface(
            modifier = Modifier.size(16.dp),
            color = color,
            shape = RoundedCornerShape(2.dp)
        ) {}
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp
        )
    }
}
