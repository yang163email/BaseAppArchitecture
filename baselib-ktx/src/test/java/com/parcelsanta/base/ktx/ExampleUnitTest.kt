package com.parcelsanta.base.ktx

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testStack() {
        val stack = Stack<Int>()
        stack.push(1)
        stack.push(2)
        stack.push(3)
        stack.push(4)

        println(stack)
        while (true) {
            if (stack.isNotEmpty()) {
                val topElement = stack.peek()
                if (topElement != 2) stack.pop()
                else break
            } else break
        }
        println(stack)
    }
}
