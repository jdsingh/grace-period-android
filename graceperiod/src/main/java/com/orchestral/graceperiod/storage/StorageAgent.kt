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

package com.orchestral.graceperiod.storage

import io.reactivex.Completable
import io.reactivex.Single
import java.io.Serializable

interface StorageAgent {

    /**
     * If successful, will emit a single null value to observer. On failure, will call observer's onError()
     * with a [WriteFailureException].

     * @param key for object in storage
     * *
     * @param objectToStore
     * *
     * @return
     */
    fun storeObjectWithKey(key: String, objectToStore: Serializable): Completable

    /**
     * If successful, will emit a single null value to observer. On failure, will call observer's onError()
     * with a [WriteFailureException].

     * @param key of object to remove from storage
     * *
     * @return
     */

    fun clearObjectWithKey(key: String): Completable

    /**
     * Clears everything from storage. If successful, will emit a single null value to observer.
     * On failure, will call observer's onError() with a [WriteFailureException].

     * @return
     */

    fun clear(): Completable

    /**
     * Emits the object from storage with the given key, or throws an [NoObjectForKeyException]
     * to the observer if no object exists for given key.

     * @param key
     * *
     * @param classOfObject
     * *
     * @param
     * *
     * @return
     */

    fun <T> getObjectForKey(key: String, classOfObject: Class<T>): Single<T>


    /**
     * Emits the object from storage with the given key, or returns [defaultValue]
     * to the observer if no object exists for given key.

     * @param key
     * *
     * @param classOfObject
     * *
     * @param defaultValue
     * *
     * @return
     */

    fun <T> getObjectForKeyWithDefault(
        key: String,
        classOfObject: Class<T>,
        defaultValue: T
    ): Single<T>

    class WriteFailureException : Throwable()

    class NoObjectForKeyException : Throwable {

        constructor() : super()

        constructor(message: String) : super(message)

    }

}
