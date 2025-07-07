package com.example.reglab7firebase.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

interface QrCodeGenerator {
    fun generate(data: String): Bitmap?
}

// AndroidQrCodeGenerator.kt (Implementasi Nyata)
class AndroidQrCodeGenerator : QrCodeGenerator {
    override fun generate(data: String): Bitmap? {
        if (data.isBlank()) {
            return null
        }
        return try {
            val writer = QRCodeWriter()
            // Encode teks menjadi BitMatrix. Ini adalah representasi 2D dari QR code.
            val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512) // Ukuran 512x512 piksel

            // Buat Bitmap kosong dengan ukuran yang sama
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

            // Loop melalui setiap piksel di BitMatrix
            for (x in 0 until width) {
                for (y in 0 until height) {
                    // Jika sel-nya true, warnai hitam. Jika false, warnai putih.
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
