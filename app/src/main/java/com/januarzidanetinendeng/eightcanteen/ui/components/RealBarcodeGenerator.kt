package com.januarzidanetinendeng.eightcanteen.ui.components

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.oned.Code128Writer
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.util.EnumMap

/**
 * Helper generator Bitmap QR Code standar ZXing (100% Scannable oleh CameraX / Google ML Kit).
 */
fun generateQrCodeBitmap(content: String, sizePixels: Int = 600): Bitmap? {
    if (content.isBlank()) return null
    return try {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.CHARACTER_SET, "UTF-8")
            put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M)
            put(EncodeHintType.MARGIN, 1) // 1 module margin agar QR terbaca jelas & tajam
        }
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePixels, sizePixels, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Helper generator Bitmap Barcode Garis Linear Code 128 standar ZXing.
 */
fun generateBarcode128Bitmap(content: String, widthPixels: Int = 640, heightPixels: Int = 140): Bitmap? {
    if (content.isBlank()) return null
    return try {
        val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java).apply {
            put(EncodeHintType.MARGIN, 2)
        }
        val bitMatrix = Code128Writer().encode(content, BarcodeFormat.CODE_128, widthPixels, heightPixels, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) AndroidColor.BLACK else AndroidColor.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Komponen Compose menampilkan QR Code asli berstandar internasional.
 */
@Composable
fun RealQrCodeImage(
    content: String,
    modifier: Modifier = Modifier,
    size: Dp = 180.dp
) {
    val qrBitmap = remember(content) { generateQrCodeBitmap(content, sizePixels = 600) }

    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        if (qrBitmap != null) {
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "Kode QR Pesanan $content",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(color = Color(0xFF0052CC), modifier = Modifier.size(28.dp))
        }
    }
}

/**
 * Komponen Compose menampilkan Barcode Garis Linear 1D asli (Code 128).
 */
@Composable
fun RealBarcode128Image(
    content: String,
    modifier: Modifier = Modifier,
    width: Dp = 230.dp,
    height: Dp = 48.dp
) {
    // Sanitasi kode untuk Code 128 (hanya karakter ASCII standar)
    val sanitizedContent = remember(content) {
        content.filter { it.code in 32..126 }.ifBlank { "ORDER-1" }
    }
    val barcodeBitmap = remember(sanitizedContent) {
        generateBarcode128Bitmap(sanitizedContent, widthPixels = 640, heightPixels = 140)
    }

    Box(
        modifier = modifier
            .size(width = width, height = height)
            .background(Color.White)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        if (barcodeBitmap != null) {
            Image(
                bitmap = barcodeBitmap.asImageBitmap(),
                contentDescription = "Barcode Garis Pesanan $content",
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

/**
 * Box lengkap tiket pesanan: QR Code Asli + Barcode Garis Asli + Teks Kode Pesanan.
 * Sangat mudah dipindai oleh kamera penjual di segala kondisi cahaya.
 */
@Composable
fun OrderBarcodeSection(
    orderCode: String,
    modifier: Modifier = Modifier,
    qrSize: Dp = 175.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // QR Code Asli
            RealQrCodeImage(
                content = orderCode,
                size = qrSize
            )

            // Barcode Garis 1D Asli
            RealBarcode128Image(
                content = orderCode,
                width = 220.dp,
                height = 42.dp
            )

            // Label Nomor Pesanan
            Text(
                text = orderCode,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A),
                letterSpacing = 1.8.sp
            )

            Text(
                text = "Tunjukkan layar ini ke kamera penjual di loket stand",
                fontSize = 10.5.sp,
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
