package com.joaofranco.basil.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import com.joaofranco.basil.ui.theme.BasilTheme

@Composable
fun Section(title: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier) {
        // Title Text
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 8.dp).padding(horizontal = 16.dp),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold
        )

        // Children Composables
        content() // This will be replaced by the child composables passed in
    }
}

@Preview(showBackground = true)
@Composable
fun SectionPreview() {
    BasilTheme {
        // Using rememberNavController for preview only
        Section(title = "Section Name") {

        }
    }
}