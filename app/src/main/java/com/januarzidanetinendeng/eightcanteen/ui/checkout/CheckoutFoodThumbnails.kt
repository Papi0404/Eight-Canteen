package com.januarzidanetinendeng.eightcanteen.ui.checkout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FoodItemThumbnail(
    imageType: FoodImageType,
    foodEmoji: String = "🍱",
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when (imageType) {
                    FoodImageType.KEBAB -> Color(0xFF2C221E)
                    FoodImageType.ES_JERUK -> Color(0xFF332014)
                    FoodImageType.KETOPRAK -> Color(0xFFFEF3C7)
                    FoodImageType.AYAM_GEPREK -> Color(0xFFFEE2E2)
                    FoodImageType.DIMSUM -> Color(0xFFFEF9C3)
                    FoodImageType.ES_KOPI -> Color(0xFFE0E7FF)
                    FoodImageType.GENERIC -> Color(0xFFFEF3C7)
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (imageType) {
            FoodImageType.KEBAB -> KebabIllustration(size = size)
            FoodImageType.ES_JERUK -> EsJerukIllustration(size = size)
            else -> Text(text = foodEmoji, fontSize = (size.value * 0.55f).sp)
        }
    }
}

@Composable
private fun KebabIllustration(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = Color(0xFF4A3428),
            cornerRadius = CornerRadius(w * 0.15f),
            size = Size(w, h)
        )

        val wrapPath = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.45f, h * 0.25f)
            lineTo(w * 0.85f, h * 0.45f)
            lineTo(w * 0.55f, h * 0.90f)
            close()
        }
        drawPath(wrapPath, color = Color(0xFFEED09D))

        val foilPath = Path().apply {
            moveTo(w * 0.15f, h * 0.75f)
            lineTo(w * 0.35f, h * 0.52f)
            lineTo(w * 0.65f, h * 0.68f)
            lineTo(w * 0.55f, h * 0.90f)
            close()
        }
        drawPath(foilPath, color = Color(0xFFE2E8F0))

        drawCircle(
            color = Color(0xFF4ADE80),
            radius = w * 0.12f,
            center = Offset(w * 0.60f, h * 0.35f)
        )

        val meatPath = Path().apply {
            moveTo(w * 0.50f, h * 0.28f)
            lineTo(w * 0.72f, h * 0.38f)
            lineTo(w * 0.68f, h * 0.48f)
            lineTo(w * 0.46f, h * 0.38f)
            close()
        }
        drawPath(meatPath, color = Color(0xFF78350F))

        drawCircle(
            color = Color(0xFFEF4444),
            radius = w * 0.07f,
            center = Offset(w * 0.52f, h * 0.38f)
        )

        val mayoPath = Path().apply {
            moveTo(w * 0.42f, h * 0.32f)
            quadraticTo(w * 0.55f, h * 0.40f, w * 0.75f, h * 0.42f)
        }
        drawPath(mayoPath, color = Color.White, style = Stroke(width = w * 0.06f))
    }
}

@Composable
private fun EsJerukIllustration(size: Dp) {
    Canvas(modifier = Modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        drawRoundRect(
            color = Color(0xFF3B2519),
            cornerRadius = CornerRadius(w * 0.15f),
            size = Size(w, h)
        )

        val glassPath = Path().apply {
            moveTo(w * 0.28f, h * 0.22f)
            lineTo(w * 0.34f, h * 0.85f)
            lineTo(w * 0.66f, h * 0.85f)
            lineTo(w * 0.72f, h * 0.22f)
            close()
        }
        drawPath(glassPath, color = Color(0x33FFFFFF))

        val juicePath = Path().apply {
            moveTo(w * 0.29f, h * 0.32f)
            lineTo(w * 0.34f, h * 0.83f)
            lineTo(w * 0.66f, h * 0.83f)
            lineTo(w * 0.71f, h * 0.32f)
            close()
        }
        drawPath(juicePath, color = Color(0xFFF97316))

        drawRoundRect(
            color = Color(0xCCFFFFFF),
            topLeft = Offset(w * 0.36f, h * 0.42f),
            size = Size(w * 0.15f, h * 0.15f),
            cornerRadius = CornerRadius(w * 0.03f)
        )
        drawRoundRect(
            color = Color(0xCCFFFFFF),
            topLeft = Offset(w * 0.50f, h * 0.55f),
            size = Size(w * 0.14f, h * 0.14f),
            cornerRadius = CornerRadius(w * 0.03f)
        )

        drawPath(glassPath, color = Color(0xB3FFFFFF), style = Stroke(width = w * 0.04f))

        drawCircle(
            color = Color(0xFFFBBF24),
            radius = w * 0.12f,
            center = Offset(w * 0.72f, h * 0.22f)
        )
        drawCircle(
            color = Color(0xFFF97316),
            radius = w * 0.09f,
            center = Offset(w * 0.72f, h * 0.22f)
        )

        drawLine(
            color = Color.White,
            start = Offset(w * 0.45f, h * 0.80f),
            end = Offset(w * 0.22f, h * 0.10f),
            strokeWidth = w * 0.05f
        )
    }
}
