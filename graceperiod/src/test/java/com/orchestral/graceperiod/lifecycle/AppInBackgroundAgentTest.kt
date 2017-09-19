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

import com.nhaarman.mockito_kotlin.verify
import com.orchestral.graceperiod.BuildConfig
import com.orchestral.graceperiod.storage.StorageAgent
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import rx.Observable
import rx.observers.TestSubscriber

/**
 * Copyright © 2017 Orion Health. All rights reserved.
 */
class AppInBackgroundAgentTest {

    @Mock lateinit var mockStorageAgent: StorageAgent

    lateinit var appInBackgroundAgent: AppInBackgroundAgent
    val key = BuildConfig.APPLICATION_ID + "AppInBackground"

    val testSubscriber = TestSubscriber<Boolean>()

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        appInBackgroundAgent = AppInBackgroundAgent(mockStorageAgent)
    }

    @Test
    fun isAppInBackground_shouldGetValueFromStorageWithCorrectKey() {
        `when`(mockStorageAgent.getObjectForKey(key, Boolean::class.java)).thenReturn(Observable.just(false))

        appInBackgroundAgent.isAppInBackground()

        verify(mockStorageAgent).getObjectForKey(key, Boolean::class.java)
    }

    @Test
    fun appInBackground_shouldReturnTrue() {
        `when`(mockStorageAgent.getObjectForKey(key, Boolean::class.java)).thenReturn(Observable.just(true))

        appInBackgroundAgent.isAppInBackground().subscribe(testSubscriber)

        with(testSubscriber) {
            assertCompleted()
            assertValue(true)
        }
    }

    @Test
    fun appNotInBackground_shouldReturnFalse() {
        `when`(mockStorageAgent.getObjectForKey(key, Boolean::class.java)).thenReturn(Observable.just(false))

        appInBackgroundAgent.isAppInBackground().subscribe(testSubscriber)

        with(testSubscriber) {
            assertCompleted()
            assertValue(false)
        }
    }

    @Test
    fun error_shouldReturnFalse() {
        `when`(mockStorageAgent.getObjectForKey(key, Boolean::class.java)).thenReturn(Observable.error(RuntimeException("Unexpected Error")))

        appInBackgroundAgent.isAppInBackground().subscribe(testSubscriber)

        with(testSubscriber) {
            assertCompleted()
            assertValue(false)
        }
    }

    @Test
    fun setAppInBackground_NotInBackground_shouldStoreFalseValueWithCorrectKey() {
        appInBackgroundAgent.setAppInBackground(false)

        verify(mockStorageAgent).storeObjectWithKey(key, false)
    }

    @Test
    fun setAppInBackground_InBackground_shouldStoreTrueValueWithCorrectKey() {
        appInBackgroundAgent.setAppInBackground(true)

        verify(mockStorageAgent).storeObjectWithKey(key, true)
    }

}