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
import com.nhaarman.mockito_kotlin.verify
import com.orchestral.consumermobile.presentation.kickedout.KickedOutPresenter
import com.orchestral.graceperiod.usecases.CheckIfGracePeriodExpiredUseCase
import com.orchestral.graceperiod.usecases.RequestGracePeriodRestartUseCase
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import rx.Observable

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
class GracePeriodActivityLifecycleCallbacksTest {

    @Mock private lateinit var mockCurrentActivityProvider: CurrentActivityProvider
    @Mock private lateinit var mockKickedOutPresenter: KickedOutPresenter
    @Mock lateinit var mockRequestGracePeriodRestartUseCase: RequestGracePeriodRestartUseCase
    @Mock lateinit var mockCheckIfGracePeriodExpiredUseCase: CheckIfGracePeriodExpiredUseCase
    @Mock lateinit var mockAppInBackgroundAgent: AppInBackgroundAgent
    @Mock lateinit var mockActiveActivitiesAgent: ActiveActivitiesAgent
    @Mock lateinit var mockActivityCallbacks: Application.ActivityLifecycleCallbacks

    @Mock lateinit var mockActivity: Activity

    private lateinit var gracePeriodLifecycleCallbacks: GracePeriodActivityLifecycleCallbacks

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
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

        `when`(mockAppInBackgroundAgent.setAppInBackground(any()))
                .thenReturn(Observable.just(null))
    }

    @Test
    fun onActivityCreated_shouldRequestGracePeriodRestart() {
        gracePeriodLifecycleCallbacks.onActivityCreated(mockActivity, null)

        verify(mockRequestGracePeriodRestartUseCase).requestRestart()
    }

    @Test
    fun onActivityStarted_noActiveActivities_shouldSetAppAsNotInBackground() {
        `when`(mockActiveActivitiesAgent.noActiveActivities()).thenReturn(true)

        gracePeriodLifecycleCallbacks.onActivityStarted(mockActivity)

        verify(mockAppInBackgroundAgent).setAppInBackground(false)
        verify(mockActiveActivitiesAgent).notifyActivityStarted()
    }

    @Test
    fun onActivityStopped_noActiveActivities_shouldSetAppAsInBackgroundAndNotifyActivityStopped() {
        `when`(mockActiveActivitiesAgent.noActiveActivities()).thenReturn(true)

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