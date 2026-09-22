package com.januarzidanetinendeng.eightcanteen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.januarzidanetinendeng.eightcanteen.ui.theme.BlueChipBg
import com.januarzidanetinendeng.eightcanteen.ui.theme.BluePrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextPrimary
import com.januarzidanetinendeng.eightcanteen.ui.theme.TextSecondary

@Composable
fun StudentBannerIllustration(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Pill Chip: • SMKN 8 JAKARTA • E-KANTIN
        Box(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BlueChipBg)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(BluePrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SMKN 8 JAKARTA • E-KANTIN",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BluePrimary,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Banner Card Box with Illustration
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .height(180.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(24.dp), ambientColor = Color(0x222563EB))
                .clip(RoundedCornerShape(24.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFBAE6FD),
                            Color(0xFFE0F2FE),
                            Color(0xFFF0F9FF)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas artwork (stars, bubbles, food graphics, student character illustration)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Floating sparkles / stars
                drawCircle(Color(0x332563EB), radius = 60f, center = Offset(w * 0.15f, h * 0.25f))
                drawCircle(Color(0x4438BDF8), radius = 40f, center = Offset(w * 0.85f, h * 0.35f))
                drawCircle(Color(0x33FBBF24), radius = 30f, center = Offset(w * 0.8f, h * 0.7f))

                // Student Hair / Head outline
                drawCircle(Color(0xFF1E293B), radius = 70f, center = Offset(w * 0.5f, h * 0.32f)) // Hair
                drawCircle(Color(0xFFFDE68A), radius = 55f, center = Offset(w * 0.5f, h * 0.36f)) // Face

                // Hair bangs & smile details
                val smilePath = Path().apply {
                    moveTo(w * 0.46f, h * 0.40f)
                    quadraticTo(w * 0.50f, h * 0.45f, w * 0.54f, h * 0.40f)
                }
                drawPath(smilePath, Color(0xFFE11D48), style = Stroke(width = 6f))

                // OSIS Uniform body
                val bodyPath = Path().apply {
                    moveTo(w * 0.35f, h * 0.95f)
                    lineTo(w * 0.42f, h * 0.52f)
                    lineTo(w * 0.58f, h * 0.52f)
                    lineTo(w * 0.65f, h * 0.95f)
                    close()
                }
                drawPath(bodyPath, Color.White)

                // OSIS Tie & Pocket badge
                val tiePath = Path().apply {
                    moveTo(w * 0.49f, h * 0.53f)
                    lineTo(w * 0.51f, h * 0.53f)
                    lineTo(w * 0.52f, h * 0.70f)
                    lineTo(w * 0.50f, h * 0.75f)
                    lineTo(w * 0.48f, h * 0.70f)
                    close()
                }
                drawPath(tiePath, Color(0xFF1D4ED8))

                // Left hand holding golden food cloche
                drawCircle(Color(0xFFF59E0B), radius = 32f, center = Offset(w * 0.28f, h * 0.55f))
                drawLine(Color(0xFFD97706), start = Offset(w * 0.20f, h * 0.62f), end = Offset(w * 0.36f, h * 0.62f), strokeWidth = 8f)

                // Right hand holding Boba Milk Tea
                drawRoundRect(
                    color = Color(0xFF38BDF8),
                    topLeft = Offset(w * 0.68f, h * 0.48f),
                    size = androidx.compose.ui.geometry.Size(45f, 65f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(10f, 10f)
                )
                // Boba straw
                drawLine(Color(0xFF0284C7), start = Offset(w * 0.76f, h * 0.40f), end = Offset(w * 0.73f, h * 0.50f), strokeWidth = 6f)
            }

            // Floating Badge at bottom of Banner Illustration
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-12).dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.White.copy(alpha = 0.92f))
                    .border(1.dp, Color(0x332563EB), RoundedCornerShape(30.dp))
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Pesan Cepat 10 Menit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "|",
                        fontSize = 12.sp,
                        color = TextSecondary.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFDBEAFE))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Bebas Antre",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BluePrimary
                        )
                    }
                }
            }
        }
    }
}
