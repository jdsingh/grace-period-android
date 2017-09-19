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

    fun init(gracePeriodConfig: GracePeriodConfig) {
        gracePeriodConfig.application.registerActivityLifecycleCallbacks(buildGracePeriodLifecycleCallbacks(gracePeriodConfig))
    }

    fun enable() {
        gracePeriodManager.enable()
    }

    fun disable() {
        gracePeriodManager.disable()
    }

    fun notifyInteraction() {
        gracePeriodManager.requestRestart()
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