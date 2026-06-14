package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment

@Composable
fun AppTourOverlay(
    targetRect: Rect?,
    title: String,
    description: String,
    onNext: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onNext)
    ) {
        Canvas(modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { alpha = 0.99f } // Needed for DST_OUT blend mode
        ) {
            // Draw dimmed background
            drawRect(color = Color.Black.copy(alpha = 0.7f))
            
            // Cut out the target
            if (targetRect != null) {
                // expand target a bit
                val expandedRect = targetRect.inflate(16.dp.toPx())
                
                drawRoundRect(
                    color = Color.Transparent,
                    topLeft = Offset(expandedRect.left, expandedRect.top),
                    size = androidx.compose.ui.geometry.Size(expandedRect.width, expandedRect.height),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx()),
                    blendMode = BlendMode.DstOut
                )
            }
        }
        
        if (targetRect != null) {
            val isTopHalf = targetRect.center.y < 1000f // approx
            val pX = targetRect.center.x
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
                    .align(if (isTopHalf) Alignment.BottomCenter else Alignment.TopCenter)
                    .offset(y = if (isTopHalf) (-64).dp else 64.dp)
            ) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, color = Color.White, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tap anywhere to continue", color = Color.LightGray, fontSize = 14.sp)
            }
        }
    }
}
