package biz.riverone.jancodemaker

import org.junit.Test

import org.junit.Assert.*

/**
 * JanDigitのテスト
 * Created by kawahara on 2018/02/05.
 */
class JanDigitTest {
    @Test
    fun calcCheckDigit() {
        val cd = JanDigit.calcCheckDigit(49, 1234567890)
        assertEquals(4, cd)

        val cd0 = JanDigit.calcCheckDigit(10, 90)
        assertEquals(0, cd0)
    }

}