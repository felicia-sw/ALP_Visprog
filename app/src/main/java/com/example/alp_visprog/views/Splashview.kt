package com.example.alp_visprog.views

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alp_visprog.R
import kotlinx.coroutines.delay

@Composable
fun SplashView(
    onSplashComplete: () -> Unit = {}
) {
    // Define the gradient colors matching the Tukerin brand
    val orangeTop = Color(0xFFF9794D) // Coral orange
    val orangeBottom = Color(0xFFFFB499) // Lighter orange/peach
    val turquoiseBottom = Color(0xFF4ECDC4) // Teal/turquoise

    // Create gradient background
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(
            orangeTop,
            orangeBottom,
            turquoiseBottom
        ),
        startY = 0f,
        endY = 2000f
    )

    // Animation states
    var startAnimation by remember { mutableStateOf(false) }

    // Logo scale animation
    val logoScale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )

    // Logo alpha animation
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            easing = FastOutSlowInEasing
        ),
        label = "logoAlpha"
    )

    // Text alpha animation (delayed)
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "textAlpha"
    )

    // Tagline alpha animation (more delayed)
    val taglineAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            delayMillis = 700,
            easing = FastOutSlowInEasing
        ),
        label = "taglineAlpha"
    )

    // Start animations and navigate after delay
    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2500) // Show splash for 2.5 seconds
        onSplashComplete()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(32.dp)
            ) {
                // Logo
                Image(
                    painter = painterResource(id = R.drawable.app_icon_4),
                    contentDescription = "Tuker.In Logo",
                    modifier = Modifier
                        .size(140.dp)
                        .scale(logoScale)
                        .alpha(logoAlpha)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // App Name
                Text(
                    text = "Tuker.in",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(textAlpha)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tagline
                Text(
                    text = "Tukerin aja.",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(taglineAlpha)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle
                Text(
                    text = "Platform Barter Komunitas",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(taglineAlpha)
                )
            }
        }
    }
}