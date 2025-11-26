package com.biuea.concurrency

class ViewPage(
    private var _count: Int = 0
) {
    val count get() = this._count

    fun view() {
        this._count++
        println("${Thread.currentThread().name} - current view count: ${this._count}")
    }
}

class VolatileViewPage(
    @Volatile var _count: Int = 0
) {
    val count get() = this._count

    fun view() {
        this._count++
        println("${Thread.currentThread().name} - current view count: ${this._count}")
    }
}