package com.biuea.concurrency.language

import com.biuea.concurrency.Lock
import com.biuea.concurrency.ViewPage
import java.util.concurrent.locks.ReentrantLock

class ReentrantLockView: Lock() {
    val reentrantLock = ReentrantLock()

    override fun lock() {
        this.reentrantLock.lock()
        this.viewPage.view()
        this.reentrantLock.unlock()
    }
}