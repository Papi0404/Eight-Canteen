package com.januarzidanetinendeng.eightcanteen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WhatsAppIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = size.toPx()
        val h = size.toPx()
        
        // Draw speech bubble body with tail
        val path = Path().apply {
            moveTo(w * 0.5f, h * 0.08f)
            cubicTo(w * 0.88f, h * 0.08f, w * 0.95f, h * 0.38f, w * 0.92f, h * 0.54f)
            cubicTo(w * 0.88f, h * 0.76f, w * 0.70f, h * 0.92f, w * 0.50f, h * 0.92f)
            cubicTo(w * 0.42f, h * 0.92f, w * 0.33f, h * 0.89f, w * 0.26f, h * 0.85f)
            lineTo(w * 0.08f, h * 0.92f)
            lineTo(w * 0.15f, h * 0.75f)
            cubicTo(w * 0.08f, h * 0.67f, w * 0.05f, h * 0.58f, w * 0.05f, h * 0.48f)
            cubicTo(w * 0.05f, h * 0.26f, w * 0.25f, h * 0.08f, w * 0.50f, h * 0.08f)
            close()
        }
        
        drawPath(path, color = tint)
        
        // Phone handset curve cutout / line
        val phonePath = Path().apply {
            moveTo(w * 0.35f, h * 0.32f)
            cubicTo(w * 0.38f, h * 0.30f, w * 0.43f, h * 0.32f, w * 0.45f, h * 0.37f)
            lineTo(w * 0.48f, h * 0.44f)
            cubicTo(w * 0.50f, h * 0.48f, w * 0.48f, h * 0.52f, w * 0.45f, h * 0.54f)
            cubicTo(w * 0.50f, h * 0.62f, w * 0.58f, h * 0.68f, w * 0.65f, h * 0.72f)
            cubicTo(w * 0.68f, h * 0.69f, w * 0.72f, h * 0.68f, w * 0.75f, h * 0.70f)
            lineTo(w * 0.82f, h * 0.74f)
            cubicTo(w * 0.87f, h * 0.77f, w * 0.88f, h * 0.82f, w * 0.85f, h * 0.86f)
            cubicTo(w * 0.80f, h * 0.92f, h * 0.70f, h * 0.90f, h * 0.55f, h * 0.78f)
            cubicTo(w * 0.40f, h * 0.65f, w * 0.28f, h * 0.48f, w * 0.28f, h * 0.38f)
            cubicTo(w * 0.28f, h * 0.35f, w * 0.31f, h * 0.33f, w * 0.35f, h * 0.32f)
            close()
        }
        
        val cutColor = if (tint == Color.White) Color(0xFF25D366) else Color.White
        drawPath(phonePath, color = cutColor)
    }
}

@Composable
fun EKantinLogoIcon(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF2563EB)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.75f)) {
            val w = this.size.width
            val h = this.size.height
            
            // Yellow sun / bowl glow curve
            drawCircle(
                color = Color(0xFFFBBF24),
                radius = w * 0.32f,
                center = Offset(w * 0.5f, h * 0.38f)
            )
            
            // Food cloche / dome line
            val domePath = Path().apply {
                moveTo(w * 0.2f, h * 0.65f)
                cubicTo(w * 0.2f, h * 0.35f, w * 0.8f, h * 0.35f, w * 0.8f, h * 0.65f)
                close()
            }
            drawPath(domePath, color = Color.White)
            
            // Plate base
            drawLine(
                color = Color.White,
                start = Offset(w * 0.15f, h * 0.68f),
                end = Offset(w * 0.85f, h * 0.68f),
                strokeWidth = w * 0.08f
            )
        }
    }
}

@Composable
fun ShieldCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 16.dp,
    tint: Color = Color(0xFF059669)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        
        val shieldPath = Path().apply {
            moveTo(w * 0.5f, h * 0.05f)
            lineTo(w * 0.9f, h * 0.22f)
            cubicTo(w * 0.9f, h * 0.62f, w * 0.65f, h * 0.88f, w * 0.5f, h * 0.95f)
            cubicTo(w * 0.35f, h * 0.88f, w * 0.1f, h * 0.62f, w * 0.1f, h * 0.22f)
            close()
        }
        drawPath(shieldPath, color = tint, style = Stroke(width = w * 0.12f))
        
        val checkPath = Path().apply {
            moveTo(w * 0.32f, h * 0.5f)
            lineTo(w * 0.45f, h * 0.64f)
            lineTo(w * 0.68f, h * 0.38f)
        }
        drawPath(checkPath, color = tint, style = Stroke(width = w * 0.12f))
    }
}
