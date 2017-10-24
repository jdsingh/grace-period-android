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

import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.orchestral.common.testrules.TestSchedulerRule
import com.orchestral.graceperiod.callback.GracePeriodCallback
import com.orchestral.graceperiod.lifecycle.AppInBackgroundAgent
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import rx.Observable
import java.util.concurrent.TimeUnit


class GracePeriodManagerTest {

    @Mock lateinit var mockGracePeriodCallback: GracePeriodCallback
    @Mock lateinit var mockAppInBackgroundAgent: AppInBackgroundAgent

    @get:Rule val testSchedulerRule = TestSchedulerRule()

    private lateinit var gracePeriodManager: GracePeriodManager

    private val DEFAULT_EXPIRY_TIME = 10

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        gracePeriodManager = GracePeriodManager(mockGracePeriodCallback, DEFAULT_EXPIRY_TIME, mockAppInBackgroundAgent)
    }

    @Test
    fun `when grace period expires should tell callback about it`() {
        gracePeriodManager.onGracePeriodExpired()

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @Test
    fun `when grace period times out and app is in foreground should tell callback about expired grace period`() {
        `when`(mockAppInBackgroundAgent.isAppInBackground()).thenReturn(Observable.just(false))

        gracePeriodManager.onGracePeriodTimeout()

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @Test
    fun `when grace period times out and app is in background should not tell callback about expired grace period`() {
        `when`(mockAppInBackgroundAgent.isAppInBackground()).thenReturn(Observable.just(true))

        gracePeriodManager.onGracePeriodTimeout()

        verify(mockGracePeriodCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `when the expiry time is updated it should not expire after the old time set up elapsed`() {
        val newExpiryTime = DEFAULT_EXPIRY_TIME + 1
        `when`(mockAppInBackgroundAgent.isAppInBackground()).thenReturn(Observable.just(false))
        gracePeriodManager.enable()

        gracePeriodManager.updateExpiryTime(newExpiryTime)
        testSchedulerRule.testScheduler.advanceTimeBy(DEFAULT_EXPIRY_TIME.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `when the expiry time is updated it should expire after the new time set up elapsed`() {
        val newExpiryTime = DEFAULT_EXPIRY_TIME + 1
        `when`(mockAppInBackgroundAgent.isAppInBackground()).thenReturn(Observable.just(false))
        gracePeriodManager.enable()

        gracePeriodManager.updateExpiryTime(newExpiryTime)
        testSchedulerRule.testScheduler.advanceTimeBy(newExpiryTime.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @After
    fun tearDown() {
        gracePeriodManager.disable()
    }

}