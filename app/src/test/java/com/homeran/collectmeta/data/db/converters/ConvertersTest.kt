package com.homeran.collectmeta.data.db.converters

import com.homeran.collectmeta.data.db.entities.ReadingStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.util.Date

class ConvertersTest {
    private val converters = Converters()

    @Test
    fun testReadingStatusConversion() {
        // 测试枚举到字符串的转换
        val status = ReadingStatus.WANT_TO_READ
        val statusString = converters.fromReadingStatus(status)
        assertEquals("WANT_TO_READ", statusString)

        // 测试字符串到枚举的转换
        val convertedStatus = converters.toReadingStatus(statusString)
        assertEquals(status, convertedStatus)
    }

    @Test
    fun testInvalidReadingStatusConversion() {
        // 测试无效状态的处理
        val convertedStatus = converters.toReadingStatus("INVALID_STATUS")
        assertEquals(ReadingStatus.WANT_TO_READ, convertedStatus) // 应该返回默认值
    }

    @Test
    fun testDateConversion() {
        // 创建一个日期
        val now = Date()
        
        // 测试Date到Long的转换
        val timestamp = converters.fromDate(now)
        assertEquals(now.time, timestamp)
        
        // 测试Long到Date的转换
        val convertedDate = converters.toDate(timestamp)
        assertEquals(now, convertedDate)
    }
    
    @Test
    fun testNullDateConversion() {
        // 测试null值处理
        val timestamp = converters.fromDate(null)
        assertNull(timestamp)
        
        val date = converters.toDate(null)
        assertNull(date)
    }
    
    @Test
    fun testStringListConversion() {
        // 测试列表到字符串的转换
        val list = listOf("科幻", "悬疑", "历史")
        val convertedString = converters.fromStringList(list)
        assertEquals("科幻,悬疑,历史", convertedString)
        
        // 测试字符串到列表的转换
        val convertedList = converters.toStringList(convertedString)
        assertEquals(list, convertedList)
    }
    
    @Test
    fun testEmptyStringListConversion() {
        // 测试空列表处理
        val emptyList = emptyList<String>()
        val convertedString = converters.fromStringList(emptyList)
        assertEquals("", convertedString)
        
        // 测试空字符串处理
        val convertedList = converters.toStringList("")
        assertEquals(listOf(""), convertedList)
    }
    
    @Test
    fun testNullStringListConversion() {
        // 测试null值处理
        val convertedString = converters.fromStringList(null)
        assertNull(convertedString)
        
        val convertedList = converters.toStringList(null)
        assertNull(convertedList)
    }
} 