package br.com.marcottc.weatherpeek.espresso

import androidx.test.espresso.IdlingResource
import java.util.concurrent.atomic.AtomicBoolean

class BooleanIdlingResource(private val resourceName: String) : IdlingResource {

    private val isIdle = AtomicBoolean(true)

    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName() = resourceName

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        this.resourceCallback = callback
    }

    override fun isIdleNow() = isIdle.get()

    fun setIdle(isIdleNow: Boolean) {
        if (isIdleNow == isIdle.get()) return
        isIdle.set(isIdleNow)
        if (isIdleNow) {
            resourceCallback?.onTransitionToIdle()
        }
    }
}