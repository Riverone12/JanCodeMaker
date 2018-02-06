package biz.riverone.jancodemaker

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.oned.EAN13Writer

/**
 * JanImage.kt: JANコードの画像データ生成
 * Created by kawahara on 2018/02/06.
 */
object JanImage {
    fun toJanImage(barcode: String, width: Int, height: Int): Bitmap? {
        val writer = EAN13Writer()
        try {
            val bitMatrix = writer.encode(barcode, BarcodeFormat.EAN_13, width, height)
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                val offset = y * width
                for (x in 0 until width) {
                    pixels[offset + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
                }
            }
            // ビットマップ形式に変換する
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

            return bitmap
        }
        catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }
}