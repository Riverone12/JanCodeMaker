package biz.riverone.jancodemaker

import java.text.DecimalFormat

/**
 * JanDigit.kt: JAN数値
 * Created by kawahara on 2018/02/05.
 */
object JanDigit {

    fun calcCheckDigit(prefix: Int, number: Long): Int {
        var oddSum = 0
        var evenSum = 0

        var n = number
        var col = 0
        while (n > 0) {
            val num = (n % 10).toInt()
            if (col % 2 == 0) {
                evenSum += num
            } else {
                oddSum += num
            }
            n = (n / 10)
            col += 1
        }

        evenSum += (prefix % 10)
        oddSum += (prefix / 10)

        val sum = evenSum * 3 + oddSum
        val cd = 10 - (sum % 10)
        if (cd >= 10) {
            return 0
        }
        return cd
    }

    fun toJanString(prefix: Int, number: Long): String {

        // チェックデジットを取得する
        val cd = calcCheckDigit(prefix, number)

        // プリフィクスを整形（0埋め）
        val prefixFormat = DecimalFormat("00")
        val strPrefix = prefixFormat.format(prefix)

        // 数値部分を整形（0埋め）
        val format = DecimalFormat("0000000000")
        val strNumber = format.format(number)

        return strPrefix + strNumber + cd.toString()
    }
}