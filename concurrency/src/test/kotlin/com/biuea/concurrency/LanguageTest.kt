package com.biuea.concurrency

import com.biuea.concurrency.language.ReentrantLockView
import com.biuea.concurrency.language.SynchronizedView
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import kotlin.time.measureTime

class LanguageTest {
    @Test
    fun `Not Lock`() {
        val threadPools = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)
        val viewPage = ViewPage()

        val measure = measureTime {
            repeat(10000) {
                threadPools.submit {
                    try {
                        viewPage.view()
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }

        latch.await()

        println("measureTime: $measure")
        Assertions.assertEquals(10000, viewPage.count)
    }

    @Test
    fun `SynchronizeTest`() {
        val threadPools = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)
        val synchronized = SynchronizedView()

        val measure = measureTime {
            repeat(10000) {
                threadPools.submit {
                    try {
                        synchronized.lock()
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }
        latch.await()

        println("measureTime: $measure")
        Assertions.assertEquals(10000, synchronized.count)
    }

    @Test
    fun `ReentrantLockViewTest`() {
        val threadPools = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)
        val reentrantLockView = ReentrantLockView()

        val measure = measureTime {
            repeat(10000) {
                threadPools.submit {
                    try {
                        reentrantLockView.lock()
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }
        latch.await()

        println("measureTime: $measure")
        Assertions.assertEquals(10000, reentrantLockView.count)
    }

    @Test
    fun `VolatileViewPageTest`() {
        val threadPools = Executors.newFixedThreadPool(100)
        val latch = CountDownLatch(10000)
        val volatileViewPage = VolatileViewPage()

        val measure = measureTime {
            repeat(10000) {
                threadPools.submit {
                    try {
                        volatileViewPage.view()
                    } finally {
                        latch.countDown()
                    }
                }
            }
        }
        latch.await()

        println("measureTime: $measure")
        Assertions.assertEquals(10000, volatileViewPage.count)
    }
}