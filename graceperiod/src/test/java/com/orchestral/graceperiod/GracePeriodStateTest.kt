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
import com.orchestral.graceperiod.GracePeriodInternal.State.*
import com.orchestral.graceperiod.utils.TestSchedulerRule
import org.junit.After
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit

private const val DEFAULT_GRACE_PERIOD_TIME_SECONDS = 2000

class GracePeriodStateTest {

    @get:Rule
    val testSchedulerRule = TestSchedulerRule()
    private val testScheduler = testSchedulerRule.testScheduler

    private val mockGracePeriodStateCallback: GracePeriodStateCallback = mock()
    private var gracePeriodState: GracePeriodInternal.State = STATE_DISABLED

    @Test
    fun `if grace period is disabled should never invoke any callback`() {
        gracePeriodState = STATE_DISABLED
        gracePeriodState.init(mockGracePeriodStateCallback, DEFAULT_GRACE_PERIOD_TIME_SECONDS)
        gracePeriodState.restart()
        gracePeriodState.checkExpired()

        verify(mockGracePeriodStateCallback, never()).onGracePeriodExpired()
        verify(mockGracePeriodStateCallback, never()).onGracePeriodTimeout()
    }

    @Test
    fun `if grace period is expired and restart is requested should never invoke grace period expired callback`() {
        gracePeriodState = STATE_EXPIRED
        gracePeriodState.init(mockGracePeriodStateCallback, DEFAULT_GRACE_PERIOD_TIME_SECONDS)
        gracePeriodState.restart()

        verify(mockGracePeriodStateCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `if grace period is expired should invoke callback when asked if it expired`() {
        gracePeriodState = STATE_EXPIRED
        gracePeriodState.init(mockGracePeriodStateCallback, DEFAULT_GRACE_PERIOD_TIME_SECONDS)
        gracePeriodState.checkExpired()

        verify(mockGracePeriodStateCallback).onGracePeriodExpired()
    }

    @Test
    fun `if grace period is enabled and it times out should invoke timeout callback`() {
        initStateEnabled()

        testScheduler.advanceTimeBy(DEFAULT_GRACE_PERIOD_TIME_SECONDS.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodStateCallback).onGracePeriodTimeout()
        verify(mockGracePeriodStateCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `if grace period is enabled and restarted before timeout it should not timeout after all time elapses`() {
        initStateEnabled()

        val halfTheTime = (DEFAULT_GRACE_PERIOD_TIME_SECONDS / 2).toLong()

        testScheduler.advanceTimeBy(halfTheTime, TimeUnit.SECONDS)
        gracePeriodState.restart()
        testScheduler.advanceTimeBy(halfTheTime, TimeUnit.SECONDS)

        verify(mockGracePeriodStateCallback, never()).onGracePeriodTimeout()
        verify(mockGracePeriodStateCallback, never()).onGracePeriodExpired()
    }

    @Test
    fun `if grace period is enabled and canceled before timeout it should not timeout after all time elapses`() {
        initStateEnabled()

        val halfTheTime = (DEFAULT_GRACE_PERIOD_TIME_SECONDS / 2).toLong()

        testScheduler.advanceTimeBy(halfTheTime, TimeUnit.SECONDS)
        gracePeriodState.cancel()
        testScheduler.advanceTimeBy(DEFAULT_GRACE_PERIOD_TIME_SECONDS.toLong(), TimeUnit.SECONDS)

        verify(mockGracePeriodStateCallback, never()).onGracePeriodTimeout()
        verify(mockGracePeriodStateCallback, never()).onGracePeriodExpired()
    }

    private fun initStateEnabled() {
        gracePeriodState = STATE_ENABLED
        gracePeriodState.init(mockGracePeriodStateCallback, DEFAULT_GRACE_PERIOD_TIME_SECONDS)
    }

    @After
    fun tearDown() {
        gracePeriodState.cancel()
    }

}
