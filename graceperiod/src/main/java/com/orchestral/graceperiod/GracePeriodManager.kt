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

import com.orchestral.graceperiod.GracePeriodInternal.State.*
import com.orchestral.graceperiod.callback.GracePeriodCallback
import com.orchestral.graceperiod.lifecycle.AppInBackgroundAgent
import com.orchestral.graceperiod.usecases.*
import rx.Subscriber


internal class GracePeriodManager(private val gracePeriodCallback: GracePeriodCallback,
                                  private var gracePeriodExpiryTimeInSeconds: Int,
                                  private val appInBackgroundAgent: AppInBackgroundAgent) :
        ChangeGracePeriodStatusUseCase,
        RequestGracePeriodRestartUseCase,
        CheckIfGracePeriodExpiredUseCase,
        GracePeriodStateCallback,
        WasKickedOutByGracePeriodUseCase,
        UpdateExpiryTimeUseCase,
        GetCurrentExpiryTimeUseCase {

    private var currentGracePeriodState: GracePeriodInternal.State = STATE_DISABLED
    private var kickedOutByGracePeriod = false

    override fun enable() {
        kickedOutByGracePeriod = false
        changeToState(STATE_ENABLED)
    }

    override fun disable() {
        changeToState(STATE_DISABLED)
    }

    override fun requestRestart() {
        currentGracePeriodState.restart()
    }

    override fun checkExpired() {
        currentGracePeriodState.checkExpired()
    }

    override fun updateExpiryTime(newExpiryTimeInSeconds: Int) {
        gracePeriodExpiryTimeInSeconds = newExpiryTimeInSeconds
        reset()
    }

    override fun getCurrentExpiryTime(): Int {
        return gracePeriodExpiryTimeInSeconds
    }

    private fun reset() {
        disable()
        enable()
    }

    override fun onGracePeriodTimeout() {
        changeToState(STATE_EXPIRED)
        checkIfItShouldExpireImmediately()
    }

    private fun checkIfItShouldExpireImmediately() {
        appInBackgroundAgent.isAppInBackground()
                .subscribe(object : Subscriber<Boolean>() {
                    override fun onCompleted() {
                    }

                    override fun onError(e: Throwable?) {
                    }

                    override fun onNext(isInBackground: Boolean) {
                        if (!isInBackground) onGracePeriodExpired()
                    }
                })
    }

    override fun onGracePeriodExpired() {
        disable()
        kickedOutByGracePeriod = true
        gracePeriodCallback.onGracePeriodExpired()
    }

    override fun wasKickedOut(): Boolean {
        val currentKickedOutState = kickedOutByGracePeriod
        kickedOutByGracePeriod = false
        return currentKickedOutState
    }

    private fun changeToState(gracePeriodState: GracePeriodInternal.State) {
        currentGracePeriodState.cancel()
        currentGracePeriodState = gracePeriodState
        currentGracePeriodState.init(this, gracePeriodExpiryTimeInSeconds)
    }

}