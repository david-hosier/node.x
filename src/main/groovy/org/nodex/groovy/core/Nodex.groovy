/*
 * Copyright 2011 VMware, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nodex.groovy.core


/**
 * <p>An instance of {@code Nodex} is available to all event loops in a running application.</p>
 *
 * <p>It handles such things as setting and cancelling timers, global event handlers, amongst other things.</p>
 *
 * <p>Additionally, several enhancements are made to certain classes<p>
 * <p><ol>
 * <li>String/GString.buffer() adds a buffer() method to Strings and GStrings which has the
 *    same effect as calling org.nodex.java.core.buffer.Buffer#create(String,String).</li>
 * <li>MillisecondConverter mixin is added to the Integer and Long classes so that you can
 *    represent time with words and the result is that time converted to milliseconds. This
 *    this is useful when using {@link #setPeriodic(long,Closure)} and 
 *    {@link #setTimer(long,Closure)}.</li>
 * <li>Mini-DSL for {@link #setPeriodic(long,Closure)} and {@link #setTimer(long,Closure)}
 *    that adds some natural language constructs to Closure. See the method javadocs for details.</li>
 * </ol></p>
 *
 * <p>
 * Client code that requires to be executed on a Nodex event loop must reside in a Closure
 * passed to the {@link #go(Closure)} method like the following:
 * <pre>
 * Nodex.go {
 *   // do stuff
 * }
 * </pre>
 * </p>
 *
 * @author <a href="http://tfox.org">Tim Fox</a>
 * @author <a href="http://github.com/david-hosier">David Hosier</a>
 */
class Nodex {

  private static def jDelegate = org.nodex.java.core.Nodex.instance
  static {
    ExpandoMetaClass.enableGlobally()
    String.metaClass.buffer = { encoding -> org.nodex.groovy.core.buffer.Buffer.create(delegate,encoding) }
    String.metaClass.buffer = { -> org.nodex.groovy.core.buffer.Buffer.create(delegate) }
    GString.metaClass.buffer = { encoding -> org.nodex.groovy.core.buffer.Buffer.create(delegate,encoding) }
    GString.metaClass.buffer = { -> org.nodex.groovy.core.buffer.Buffer.create(delegate) }
    Integer.metaClass.mixin org.nodex.groovy.core.util.MillisecondConverter
    Long.metaClass.mixin org.nodex.groovy.core.util.MillisecondConverter

    Closure.metaClass.every = { delay -> Nodex.setPeriodic(delay, delegate) }
    Closure.metaClass.afterDelayOf = { delay -> Nodex.setTimer(delay, delegate) }
  }
  
  /**
   * Run the specified Closure inside an event loop. 
   * An event loop will be picked by the system from all available loops.
   */
  static void go(Closure closure) {
    jDelegate.go(new java.lang.Runnable() {
      public void run() {
        closure.call()
      }
    })
  }

  /**
   * Send a message to the handler with the specified {@code actorID}. This can be called from any event loop.
   * @return true of the message was successfully sent, or false if no such handler exists.
   */
  static boolean sendToHandler(long actorID, message) {
    jDelegate.sendToHandler(actorID, message)
  }

  /**
   * Register a global handler with the system. The handler can be invoked by calling the {@link #sendToHandler}
   * method from any event loop. The handler will always be called on the event loop that invoked the {@code
   * registerHandler} method.
   * @param handler a Closure to register as a Handler
   * @return the unique ID of the handler. This is required when calling {@link #sendToHandler}.
   */
  static long registerHandler(Closure handler) {
    jDelegate.registerHandler(wrapHandler(handler))
  }

  /**
   * Unregister the handler with the specified {@code id}. This must be called from the same event loop that
   * registered the handler.
   * @return true if the handler was successfully unregistered, otherwise false if the handler cannot be found.
   */
  static boolean unregisterHandler(long id) {
    jDelegate.unregisterHandler(id)
  }

  /**
   * Set a periodic timer to fire every {@code delay} milliseconds, at which point {@code handler} will be called with
   * the id of the timer. This method can be called directly on Nodex, or you can use the
   * {@code every(long)} method on your Closure as the following example demonstrates:
   * <p>
   * <pre>
   * { id -> println "Time ${id} was invoked" }.every 2.seconds
   * </pre>
   * </p>
   * @return the unique ID of the timer
   */
  static long setPeriodic(long delay, Closure handler) {
    jDelegate.setPeriodic(delay, wrapHandler(handler))
  }

  /**
   * Set a one-shot timer to fire after {@code delay} milliseconds, at which point {@code handler} will be called with
   * the id of the timer. This method can be called directly on Nodex, or you can use the 
   * afterDelayOf(long) method on your Closure as the following example demonstrates:
   * <p>
   * <pre>
   * { id -> println "Time ${id} was invoked" }.afterDelayOf 2.seconds
   * </pre>
   * @return the unique ID of the timer
   */
  static long setTimer(long delay, Closure handler) {
    jDelegate.setTimer(delay, wrapHandler(handler))
  }

  /**
   * Cancel the timer with the specified {@code id}. Returns {@code} true if the timer was successfully cancelled, or
   * {@code false} if the timer does not exist.
   */
  static boolean cancelTimer(long id) {
    jDelegate.cancelTimer(id)
  }

  /**
   * Call the specified event handler asynchronously on the next "tick" of the event loop.
   */
  void nextTick(Closure handler) {
    def jHandler = new org.nodex.java.core.Handler() {
        void handle(nada) {
          hndlr.call()
        }
    }
    jDelegate.nextTick(jHandler)
  }

  private static def wrapHandler(Closure handler) {
    new org.nodex.java.core.Handler() {
      void handle(data) {
        handler.call(data)
      }
    }
  }
}