package com.example.mimapa.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.mimapa.data.model.CreditItemCcBySa3

/**
 * Lista de créditos y licencias para imágenes.
 */
val imageCredits = listOf(
    CreditItemCcBySa3(
        title = "Nuevo autobús de Almería",
        author = "Almju",
        authorUrl = "https://commons.wikimedia.org/wiki/User:Almju",
        sourceUrl = "https://commons.wikimedia.org/wiki/File:Autob%C3%BAs_Almer%C3%ADa_n.jpg"
    )
)

/**
 * Composable que muestra un elemento de crédito y licencia para imágenes.
 */
@Composable
fun CreditItemCard(item: CreditItemCcBySa3) {
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
            append(item.title)
        }
        append(" de ")

        // Enlace para el autor
        withLink(LinkAnnotation.Url(item.authorUrl)) {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(item.author)
            }
        }

        append(" está licenciada bajo ")

        // Enlace para la licencia
        withLink(LinkAnnotation.Url(item.licenseUrl)) {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(item.licenseName)
            }
        }

        append(". Obtenida de ")

        // Enlace para la fuente
        withLink(LinkAnnotation.Url(item.sourceUrl)) {
            withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                append(item.sourceUrl)
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Text(
                text = annotatedString,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Composable que muestra la pantalla de créditos y licencias.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreditsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Créditos y licencias") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(imageCredits) { credit ->
                CreditItemCard(item = credit)
            }
        }
    }
}
