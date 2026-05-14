package com.example.lemonade

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lemonade.ui.theme.LemonadeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LemonadeTheme {
                LemonApp()
            }
        }
    }
}

@Composable
fun LemonApp() {

    // Step tracker
    var currentStep by remember { mutableStateOf(1) }

    // Random squeeze counter
    var squeezeCount by remember { mutableStateOf(0) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {

        // Image selector
        val imageRes = when (currentStep) {
            1 -> R.drawable.lemon_tree
            2 -> R.drawable.lemon_squeeze
            3 -> R.drawable.lemon_drink
            else -> R.drawable.lemon_restart
        }

        // Text selector
        val textRes = when (currentStep) {
            1 -> R.string.lemon_select
            2 -> R.string.lemon_squeeze
            3 -> R.string.lemon_drink
            else -> R.string.lemon_restart
        }

        // Content description selector
        val contentDescRes = when (currentStep) {
            1 -> R.string.lemon_tree_content_description
            2 -> R.string.lemon_content_description
            3 -> R.string.lemonade_content_description
            else -> R.string.empty_glass_content_description
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = stringResource(id = textRes),
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = imageRes),
                contentDescription = stringResource(id = contentDescRes),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .border(
                        width = 2.dp,
                        color = Color(105, 205, 216),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clickable {

                        when (currentStep) {

                            // Step 1 → Go to squeeze
                            1 -> {
                                currentStep = 2
                                squeezeCount = (2..4).random()
                            }

                            // Step 2 → Squeeze lemon
                            2 -> {
                                squeezeCount--
                                if (squeezeCount == 0) {
                                    currentStep = 3
                                }
                            }

                            // Step 3 → Drink lemonade
                            3 -> currentStep = 4

                            // Step 4 → Restart
                            4 -> currentStep = 1
                        }
                    }
            )
        }
    }
}