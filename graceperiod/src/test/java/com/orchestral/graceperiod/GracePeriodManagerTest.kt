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

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.never
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import com.orchestral.graceperiod.callback.GracePeriodCallback
import com.orchestral.graceperiod.lifecycle.AppInBackgroundAgent
import com.orchestral.graceperiod.utils.TestSchedulerRule
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

private const val DEFAULT_EXPIRY_TIME = 10

class GracePeriodManagerTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()
    private val testScheduler = testSchedulerRule.testScheduler

    private val mockGracePeriodCallback: GracePeriodCallback = mock()
    private val mockAppInBackgroundAgent: AppInBackgroundAgent = mock()

    private lateinit var gracePeriodManager: GracePeriodManager

    @Before
    fun setUp() {
        gracePeriodManager = GracePeriodManager(
            mockGracePeriodCallback,
            DEFAULT_EXPIRY_TIME,
            mockAppInBackgroundAgent
        )
    }

    @Test
    fun `when grace period expires should tell callback about it`() {
        gracePeriodManager.onGracePeriodExpired()

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @Test
    fun `when grace period times out and app is in foreground should tell callback about expired grace period`() {
        whenever(mockAppInBackgroundAgent.isAppInBackground())
            .thenReturn(Single.just(false))

        gracePeriodManager.onGracePeriodTimeout()

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @Test
    fun `when grace period times out and app is in background should not tell callback about expired grace period`() {
        whenever(mockAppInBackgroundAgent.isAppInBackground())
            .thenReturn(Single.just(true))

        gracePeriodManager.onGracePeriodTimeout()

        verify(mockGracePeriodCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `when the expiry time is updated it should not expire after the old time set up elapsed`() {
        val newExpiryTime = DEFAULT_EXPIRY_TIME + 1
        whenever(mockAppInBackgroundAgent.isAppInBackground())
            .thenReturn(Single.just(false))

        gracePeriodManager.enable()
        gracePeriodManager.updateExpiryTime(newExpiryTime)
        testScheduler.advanceTimeBy(DEFAULT_EXPIRY_TIME.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `when the expiry time is updated it should expire after the new time set up elapsed`() {
        val newExpiryTime = DEFAULT_EXPIRY_TIME + 1
        whenever(mockAppInBackgroundAgent.isAppInBackground())
            .thenReturn(Single.just(false))

        gracePeriodManager.enable()
        gracePeriodManager.updateExpiryTime(newExpiryTime)
        testScheduler.advanceTimeBy(newExpiryTime.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodCallback).onGracePeriodExpired()
    }

    @After
    fun tearDown() {
        gracePeriodManager.disable()
    }

}
