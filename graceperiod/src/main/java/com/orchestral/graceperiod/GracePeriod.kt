/*
 * The MIT License
 *
 * Copyright (c) Orchestral Developments Ltd and the Orion Health group of companies (2001 - 2017).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.orchestral.graceperiod

import android.app.Application
import com.orchestral.graceperiod.callback.RestartAppCallback
import com.orchestral.graceperiod.config.GracePeriodConfig
import com.orchestral.graceperiod.lifecycle.ActiveActivitiesAgent
import com.orchestral.graceperiod.lifecycle.AppInBackgroundAgent
import com.orchestral.graceperiod.lifecycle.CurrentActivityProvider
import com.orchestral.graceperiod.lifecycle.GracePeriodActivityLifecycleCallbacks
import com.orchestral.graceperiod.presentation.KickedOutPresenterImpl
import com.orchestral.graceperiod.storage.SharedPreferencesStorageAgent

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
object GracePeriod {

    private lateinit var gracePeriodManager: GracePeriodManager

    /**
     * Init Grace Period with a given configuration. Note that Grace Period timer won't start until
     * {@link #enable()} method is called.
     * @param gracePeriodConfig the Grace Period configuration
     */
    fun init(gracePeriodConfig: GracePeriodConfig) {
        gracePeriodConfig.application.registerActivityLifecycleCallbacks(buildGracePeriodLifecycleCallbacks(gracePeriodConfig))
    }

    /**
     * Enable Grace Period. This will immediately start the timer and make Grace Period expire in the
     * expiry time set.
     * This method must usually be called when the session started, for example, once the user logs in.
     */
    fun enable() {
        gracePeriodManager.enable()
    }

    /**
     * Disable Grace Period so the timer is fully stopped and the session can no longer expire nor the
     * user be kicked out by the library.
     * Must be called when you no longer want to make use of Grace Period, for example after logging out.
     * Not calling this method might cause unwanted behaviour.
     */
    fun disable() {
        gracePeriodManager.disable()
    }

    /**
     * Notify Grace Period that an interaction happened so the timer must be reset. What an interaction
     * means is up to your app, but will normally refer to touches, swipes or any action done by the user
     * on the UI.
     * It is normally handled by Activities in {@link android.app.Activity#onUserInteraction()} method.
     */
    fun notifyInteraction() {
        gracePeriodManager.requestRestart()
    }

    /**
     * Update the Grace Period expiry time. Be aware this will reset the timer, meaning that from this
     * very moment, the newExpiryTimeInSeconds should elapse for Grace Period to expire.
     *
     * @param newExpiryTimeInSeconds the new expiry time for Grace Period expressed in seconds.
     */
    fun updateExpiryTime(newExpiryTimeInSeconds: Int) {
        gracePeriodManager.updateExpiryTime(newExpiryTimeInSeconds)
    }

    private fun buildGracePeriodLifecycleCallbacks(gracePeriodConfig: GracePeriodConfig): Application.ActivityLifecycleCallbacks? {
        val appInBackgroundAgent = AppInBackgroundAgent(SharedPreferencesStorageAgent(gracePeriodConfig.application, "GracePeriod"))
        val currentActivityProvider = CurrentActivityProvider()
        val gracePeriodCallback = gracePeriodConfig.callback ?: RestartAppCallback(currentActivityProvider)
        gracePeriodManager =
                GracePeriodManager(
                        gracePeriodCallback,
                        gracePeriodConfig.expiryTime,
                        appInBackgroundAgent)

        return GracePeriodActivityLifecycleCallbacks(
                gracePeriodConfig.activityLifeCycleCallbacks,
                currentActivityProvider,
                gracePeriodManager,
                gracePeriodManager,
                KickedOutPresenterImpl(gracePeriodManager, gracePeriodConfig.dialogConfig, gracePeriodCallback),
                appInBackgroundAgent,
                ActiveActivitiesAgent())
    }

}