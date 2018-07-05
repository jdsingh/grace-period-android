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

package com.orchestral.graceperiod.lifecycle

import android.app.Activity
import android.app.Application
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.orchestral.graceperiod.presentation.KickedOutPresenter
import com.orchestral.graceperiod.usecases.CheckIfGracePeriodExpiredUseCase
import com.orchestral.graceperiod.usecases.RequestGracePeriodRestartUseCase
import io.reactivex.Completable
import org.junit.Before
import org.junit.Test

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
class GracePeriodActivityLifecycleCallbacksTest {

    private val mockCurrentActivityProvider: CurrentActivityProvider = mock()
    private val mockKickedOutPresenter: KickedOutPresenter = mock()
    private val mockRequestGracePeriodRestartUseCase: RequestGracePeriodRestartUseCase = mock()
    private val mockCheckIfGracePeriodExpiredUseCase: CheckIfGracePeriodExpiredUseCase = mock()
    private val mockAppInBackgroundAgent: AppInBackgroundAgent = mock()
    private val mockActiveActivitiesAgent: ActiveActivitiesAgent = mock()
    private val mockActivityCallbacks: Application.ActivityLifecycleCallbacks = mock()
    private val mockActivity: Activity = mock()

    private lateinit var gracePeriodLifecycleCallbacks: GracePeriodActivityLifecycleCallbacks

    @Before
    fun setUp() {
        gracePeriodLifecycleCallbacks =
                GracePeriodActivityLifecycleCallbacks(
                    mockActivityCallbacks,
                    mockCurrentActivityProvider,
                    mockRequestGracePeriodRestartUseCase,
                    mockCheckIfGracePeriodExpiredUseCase,
                    mockKickedOutPresenter,
                    mockAppInBackgroundAgent,
                    mockActiveActivitiesAgent
                )

        whenever(mockAppInBackgroundAgent.setAppInBackground(any()))
            .thenReturn(Completable.complete())
    }

    @Test
    fun onActivityCreated_shouldRequestGracePeriodRestart() {
        gracePeriodLifecycleCallbacks.onActivityCreated(mockActivity, null)

        verify(mockRequestGracePeriodRestartUseCase).requestRestart()
    }

    @Test
    fun onActivityStarted_noActiveActivities_shouldSetAppAsNotInBackground() {
        whenever(mockActiveActivitiesAgent.noActiveActivities())
            .thenReturn(true)

        gracePeriodLifecycleCallbacks.onActivityStarted(mockActivity)

        verify(mockAppInBackgroundAgent).setAppInBackground(false)
        verify(mockActiveActivitiesAgent).notifyActivityStarted()
    }

    @Test
    fun onActivityStopped_noActiveActivities_shouldSetAppAsInBackgroundAndNotifyActivityStopped() {
        whenever(mockActiveActivitiesAgent.noActiveActivities())
            .thenReturn(true)

        gracePeriodLifecycleCallbacks.onActivityStopped(mockActivity)

        verify(mockAppInBackgroundAgent).setAppInBackground(true)
        verify(mockActiveActivitiesAgent).notifyActivityStopped()
    }

    @Test
    fun onActivityResumed_shouldRegisterCurrentActivity() {
        gracePeriodLifecycleCallbacks.onActivityResumed(mockActivity)

        verify(mockCurrentActivityProvider).registerCurrentActivity(mockActivity)
    }

    @Test
    fun onActivityResumed_shouldCheckIfGracePeriodExpired() {
        gracePeriodLifecycleCallbacks.onActivityResumed(mockActivity)

        verify(mockCheckIfGracePeriodExpiredUseCase).checkExpired()
    }

    @Test
    fun onActivityDestroyed_shouldUnregisterCurrentActivity() {
        gracePeriodLifecycleCallbacks.onActivityDestroyed(mockActivity)

        verify(mockCurrentActivityProvider).unregisterCurrentActivity(mockActivity)
    }

    @Test
    fun shouldForwardAllMethodsToProvidedLifecycleCallbacks() {
        gracePeriodLifecycleCallbacks.run {
            onActivityCreated(mockActivity, null)
            verify(mockActivityCallbacks).onActivityCreated(mockActivity, null)

            onActivityStarted(mockActivity)
            verify(mockActivityCallbacks).onActivityStarted(mockActivity)

            onActivityResumed(mockActivity)
            verify(mockActivityCallbacks).onActivityResumed(mockActivity)

            onActivityPaused(mockActivity)
            verify(mockActivityCallbacks).onActivityPaused(mockActivity)

            onActivityStopped(mockActivity)
            verify(mockActivityCallbacks).onActivityStopped(mockActivity)

            onActivityDestroyed(mockActivity)
            verify(mockActivityCallbacks).onActivityDestroyed(mockActivity)

            onActivitySaveInstanceState(mockActivity, null)
            verify(mockActivityCallbacks).onActivitySaveInstanceState(mockActivity, null)
        }
    }

}
