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

package com.orchestral.graceperiod.presentation

import com.nhaarman.mockito_kotlin.*
import com.orchestral.graceperiod.callback.GracePeriodCallback
import com.orchestral.graceperiod.config.GracePeriodDialogConfig
import com.orchestral.graceperiod.usecases.WasKickedOutByGracePeriodUseCase
import org.junit.Test

class KickedOutPresenterImplTest {

    private lateinit var kickedOutPresenter: KickedOutPresenter

    private val mockWasKickedOutUseCase: WasKickedOutByGracePeriodUseCase = mock()
    private val mockView: KickedOutMessageView = mock()
    private val mockGPMessageView: GracePeriodMessageView = mock()
    private val mockGracePeriodCallback: GracePeriodCallback = mock()

    @Test
    fun `it should not display the dialog when it is configured to false`() {
        kickedOutPresenter = KickedOutPresenterImpl(
            mockWasKickedOutUseCase,
            GracePeriodDialogConfig.Builder().showDialogWhenExpired(false).build(),
            mockGracePeriodCallback
        )
        kickedOutPresenter.attachView(mockView)

        kickedOutPresenter.onNewKickedOutMessageViewCreated()

        verify(mockView, never()).showDefaultKickedOutByGracePeriodDialog()
        verify(mockView, never()).showCustomKickedOutByGracePeriodDialog(any())
        verify(mockView, never()).showDefaultKickedOutByGracePeriodDialog(any(), any(), any())
    }

    @Test
    fun `it should display default dialog when no custom message or dialog is provided`() {
        kickedOutPresenter = KickedOutPresenterImpl(
            mockWasKickedOutUseCase,
            GracePeriodDialogConfig.Builder().showDialogWhenExpired(true).build(),
            mockGracePeriodCallback
        )
        whenever(mockWasKickedOutUseCase.wasKickedOut()).thenReturn(true)
        kickedOutPresenter.attachView(mockView)

        kickedOutPresenter.onNewKickedOutMessageViewCreated()

        verify(mockView).showDefaultKickedOutByGracePeriodDialog()
        verify(mockView, never()).showCustomKickedOutByGracePeriodDialog(any())
        verify(mockView, never()).showDefaultKickedOutByGracePeriodDialog(any(), any(), any())
    }

    @Test
    fun `it should display dialog with custom title and message when they are provided`() {
        kickedOutPresenter = KickedOutPresenterImpl(
            mockWasKickedOutUseCase,
            GracePeriodDialogConfig.Builder().showDialogWhenExpired(true).dialogText("text").dialogTitle(
                "title"
            ).build(),
            mockGracePeriodCallback
        )
        whenever(mockWasKickedOutUseCase.wasKickedOut()).thenReturn(true)
        kickedOutPresenter.attachView(mockView)

        kickedOutPresenter.onNewKickedOutMessageViewCreated()

        verify(mockView, never()).showDefaultKickedOutByGracePeriodDialog()
        verify(mockView, never()).showCustomKickedOutByGracePeriodDialog(any())
        verify(mockView).showDefaultKickedOutByGracePeriodDialog(
            eq("title"),
            eq("text"),
            any<String>()
        )
    }

    @Test
    fun `it should display custom dialog when one is provided`() {
        // TODO
    }

    @Test
    fun `it should notify callback about grace period message displayed when dialog is enabled and user was kicked out`() {
        kickedOutPresenter = KickedOutPresenterImpl(
            mockWasKickedOutUseCase,
            GracePeriodDialogConfig.Builder().showDialogWhenExpired(true).build(),
            mockGracePeriodCallback
        )
        whenever(mockWasKickedOutUseCase.wasKickedOut()).thenReturn(true)
        kickedOutPresenter.attachView(mockView)

        kickedOutPresenter.onNewKickedOutMessageViewCreated()

        verify(mockGracePeriodCallback).onGracePeriodDialogDisplayed()
    }

    @Test
    fun `it should never notify callback about grace period message displayed when the user was not kicked out`() {
        kickedOutPresenter = KickedOutPresenterImpl(
            mockWasKickedOutUseCase,
            GracePeriodDialogConfig.Builder().showDialogWhenExpired(true).build(),
            mockGracePeriodCallback
        )
        whenever(mockWasKickedOutUseCase.wasKickedOut()).thenReturn(false)
        kickedOutPresenter.attachView(mockView)

        kickedOutPresenter.onNewKickedOutMessageViewCreated()

        verify(mockGracePeriodCallback, never()).onGracePeriodDialogDisplayed()
    }

}
