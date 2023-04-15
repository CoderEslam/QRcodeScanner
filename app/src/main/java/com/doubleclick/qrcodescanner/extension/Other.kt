package com.doubleclick.qrcodescanner.extension
fun <T> unsafeLazy(initializer: () -> T): Lazy<T> = lazy(LazyThreadSafetyMode.NONE, initializer)