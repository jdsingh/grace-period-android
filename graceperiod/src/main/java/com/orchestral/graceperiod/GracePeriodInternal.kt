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

import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

internal interface GracePeriodInternal {

    fun init(
        gracePeriodStateCallback: GracePeriodStateCallback,
        gracePeriodExpiryTimeInSeconds: Int
    ) {
    }

    fun restart() {
    }

    fun checkExpired() {
    }

    fun cancel() {
    }

    enum class State : GracePeriodInternal {

        STATE_DISABLED,

        STATE_ENABLED {

            private val gracePeriodObservable = PublishSubject.create<Byte>()
            private var gracePeriodExpireTimeInSeconds = 0L

            private lateinit var gracePeriodDisposable: Disposable
            private lateinit var gracePeriodStateCallback: GracePeriodStateCallback

            override fun init(
                gracePeriodStateCallback: GracePeriodStateCallback,
                gracePeriodExpiryTimeInSeconds: Int
            ) {
                this.gracePeriodExpireTimeInSeconds = gracePeriodExpiryTimeInSeconds.toLong()
                this.gracePeriodStateCallback = gracePeriodStateCallback

                startGracePeriod()
            }

            private fun startGracePeriod() {
                gracePeriodDisposable = gracePeriodObservable
                    .restartGracePeriodTimer()
                    .subscribe()
                restart()
            }

            override fun restart() {
                gracePeriodObservable.onNext(1)
            }

            override fun cancel() {
                gracePeriodDisposable.dispose()
            }

            private fun <T> Observable<T>.restartGracePeriodTimer(): Observable<Long> {
                return this
                    .switchMap {
                        Observable.timer(gracePeriodExpireTimeInSeconds, TimeUnit.SECONDS)
                            .doOnComplete {
                                gracePeriodStateCallback.onGracePeriodTimeout()
                            }
                    }
            }

        },

        STATE_EXPIRED {

            private lateinit var gracePeriodStateCallback: GracePeriodStateCallback

            override fun init(
                gracePeriodStateCallback: GracePeriodStateCallback,
                gracePeriodExpiryTimeInSeconds: Int
            ) {
                this.gracePeriodStateCallback = gracePeriodStateCallback
            }

            override fun checkExpired() {
                gracePeriodStateCallback.onGracePeriodExpired()
            }

        };

    }

}
