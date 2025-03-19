package com.homeran.collectmeta.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.homeran.collectmeta.data.db.dao.BookDao
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var bookDao: BookDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        bookDao = db.bookDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun testDatabaseCreation() {
        // 验证数据库实例创建成功
        assertNotNull(db)
    }
    
    @Test
    fun testBookDaoRetrieval() {
        // 验证DAO实例获取成功
        assertNotNull(bookDao)
    }
    
    @Test
    fun testSingletonInstanceCreation() {
        // 验证单例模式正常工作
        val context = ApplicationProvider.getApplicationContext<Context>()
        val instance1 = AppDatabase.getInstance(context)
        val instance2 = AppDatabase.getInstance(context)
        
        // 验证两次获取到的是同一个实例
        assert(instance1 === instance2)
    }
} 