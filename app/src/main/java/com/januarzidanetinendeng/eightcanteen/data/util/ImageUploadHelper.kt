package com.januarzidanetinendeng.eightcanteen.data.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

object ImageUploadHelper {

    val ALLOWED_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp")
    val ALLOWED_MIME_TYPES = listOf("image/jpeg", "image/jpg", "image/png", "image/webp")

    fun isAllowedImageFormat(context: Context, uri: Uri): Boolean {
        val mime = context.contentResolver.getType(uri)?.lowercase()
        if (mime != null && ALLOWED_MIME_TYPES.any { mime.startsWith(it) || mime == it }) {
            return true
        }
        val fileName = getFileName(context, uri).lowercase()
        return ALLOWED_EXTENSIONS.any { ext -> fileName.endsWith(".$ext") }
    }

    fun getFileName(context: Context, uri: Uri): String {
        var name = "product_image.jpg"
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index) ?: name
                }
            }
        }
        return name
    }

    fun uriToMultipartPart(
        context: Context,
        uri: Uri,
        partName: String = "image"
    ): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val rawName = getFileName(context, uri)
            val extension = when {
                mimeType.contains("png") -> "png"
                mimeType.contains("webp") -> "webp"
                mimeType.contains("jpg") || mimeType.contains("jpeg") -> "jpg"
                else -> rawName.substringAfterLast('.', "jpg")
            }
            val fileName = "menu_${System.currentTimeMillis()}.$extension"

            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bytes = inputStream?.use { it.readBytes() } ?: return null

            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, fileName, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
